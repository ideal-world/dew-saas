/*
 * Copyright 2020. the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package idealworld.dew.saas.service.ident.service;

import com.ecfront.dew.common.$;
import com.ecfront.dew.common.Page;
import com.ecfront.dew.common.Resp;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import group.idealworld.dew.Dew;
import group.idealworld.dew.core.auth.dto.OptInfo;
import idealworld.dew.saas.service.ident.Constant;
import idealworld.dew.saas.service.ident.domain.*;
import idealworld.dew.saas.service.ident.dto.IdentOptInfo;
import idealworld.dew.saas.service.ident.dto.account.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author gudaoxuri
 */
@Service
public class AccountService extends BasicService {

    private static final String SK_KIND_VCODE_TMP_REL = "sk-kind:vocde:tmp-rel:";
    private static final String SK_KIND_VCODE_ERROR_TIMES = "sk-kind:vocde:error-times:";
    // TODO config
    private static final int SK_KIND_VCODE_EXPRIE_SEC = 60 * 5;
    private static final int SK_KIND_VCODE_MAX_ERROR_TIMES = 5;

    @Transactional
    public Resp<IdentOptInfo> login(LoginReq loginReq, Long relTenantId) {
        var qAccount = QAccount.account;
        var qAccountCert = QAccountCert.accountCert;
        var accountInfo = sqlBuilder
                .select(qAccountCert.id, qAccountCert.sk, qAccount.id, qAccountCert.validTimes)
                .from(qAccountCert)
                .leftJoin(qAccount).on(qAccountCert.relAccountId.eq(qAccount.id))
                .where(qAccountCert.kind.eq(loginReq.getCertKind()))
                .where(qAccountCert.ak.eq(loginReq.getAk()))
                .where(qAccountCert.validTime.after(new Date()))
                .where(qAccountCert.validTimes.gt(0).or(qAccountCert.validTimes.eq(-1L)))
                .where(qAccount.relTenantId.eq(relTenantId))
                .where(qAccount.delFlag.eq(false))
                .where(qAccountCert.delFlag.eq(false))
                .fetchOne();
        if (accountInfo == null) {
            logger.warn("Login Fail: [" + relTenantId + "][" + loginReq.getAk() + "] AK not exist or expired");
            return Resp.notFound("登录凭证不存在或已过期");
        }
        var certId = accountInfo.get(0, Long.class);
        var certSk = accountInfo.get(1, String.class);
        var accountId = accountInfo.get(2, Long.class);
        var certValidTimes = accountInfo.get(3, Long.class);
        var validateR = validateSK(loginReq.getCertKind(), loginReq.getAk(), loginReq.getSk(), certSk, relTenantId);
        if (!validateR.ok()) {
            logger.warn("Login Fail: [" + relTenantId + "][" + loginReq.getAk() + "] SK un-match");
            return Resp.error(validateR);
        }
        if (certValidTimes != -1L) {
            updateEntity(sqlBuilder
                    .update(qAccountCert)
                    .set(qAccountCert.validTimes, qAccountCert.validTimes.add(-1L))
                    .where(qAccountCert.id.eq(certId))
                    .where(qAccountCert.validTimes.gt(0))
            );
        }
        logger.info("Login Success:  [" + relTenantId + "][" + loginReq.getAk() + "]");
        String token = $.field.createUUID();
        var optInfo = new IdentOptInfo()
                // 转成String避免转化成Integer
                .setAccountCode(accountId + "")
                .setToken(token)
                .setRoleInfo(findRoleInfo(accountId));
        optInfo.setRelTenantId(relTenantId);
        Dew.auth.setOptInfo(optInfo);
        return Resp.success(optInfo);
    }

    @Transactional
    public Resp<Void> logout(Long accountId, String token) {
        logger.info("Logout:  [" + accountId + "][" + token + "]");
        Dew.auth.removeOptInfo(token);
        return Resp.success(null);
    }

    @Transactional
    public Resp<Long> addAccountExt(AddAccountReq addAccountReq, Long relTenantId) {
        var processR = certProcessSK(addAccountReq.getCertReq().getKind(),
                addAccountReq.getCertReq().getAk(),
                addAccountReq.getCertReq().getSk(),
                relTenantId);
        if (!processR.ok()) {
            return Resp.error(processR);
        }
        var account = Account.builder()
                .name(addAccountReq.getName())
                .avatar(addAccountReq.getAvatar() != null ? addAccountReq.getAvatar() : "")
                .parameters(addAccountReq.getParameters() != null ? addAccountReq.getParameters() : "")
                .status(Account.Status.ENABLED)
                .relTenantId(relTenantId)
                .build();
        saveEntity(account);
        doAddAccountCert(addAccountReq.getCertReq(), processR.getBody(), account.getId(), relTenantId);
        addAccountPost(addAccountReq.getPostReq(), account.getId(), relTenantId);
        return Resp.success(account.getId());
    }

    public Resp<AccountInfoResp> getAccountInfo(Long accountId, Long relTenantId) {
        var qAccount = QAccount.account;
        var qAccountCreateUser = QAccount.account;
        var qAccountUpdateUser = QAccount.account;
        var query = sqlBuilder
                .select(Projections.bean(
                        AccountInfoResp.class,
                        qAccount.id,
                        qAccount.name,
                        qAccount.avatar,
                        qAccount.parameters,
                        qAccount.delFlag,
                        qAccount.createTime,
                        qAccount.updateTime,
                        qAccountCreateUser.name.as("createUserName"),
                        qAccountUpdateUser.name.as("updateUserName")))
                .from(qAccount)
                .leftJoin(qAccountCreateUser).on(qAccount.createUser.eq(qAccountCreateUser.id))
                .leftJoin(qAccountUpdateUser).on(qAccount.updateUser.eq(qAccountUpdateUser.id))
                .where(qAccount.id.eq(accountId))
                .where(qAccount.relTenantId.eq(relTenantId));
        return getDTO(query);
    }

    public Resp<Page<AccountInfoResp>> pageAccountInfo(Long pageNumber, Integer pageSize, Long relTenantId) {
        var qAccount = QAccount.account;
        var qAccountCreateUser = QAccount.account;
        var qAccountUpdateUser = QAccount.account;
        var query = sqlBuilder
                .select(Projections.bean(
                        AccountInfoResp.class,
                        qAccount.id,
                        qAccount.name,
                        qAccount.avatar,
                        qAccount.parameters,
                        qAccount.delFlag,
                        qAccount.createTime,
                        qAccount.updateTime,
                        qAccountCreateUser.name.as("createUserName"),
                        qAccountUpdateUser.name.as("updateUserName")))
                .from(qAccount)
                .leftJoin(qAccountCreateUser).on(qAccount.createUser.eq(qAccountCreateUser.id))
                .leftJoin(qAccountUpdateUser).on(qAccount.updateUser.eq(qAccountUpdateUser.id))
                .where(qAccount.relTenantId.eq(relTenantId));
        return pageDTOs(query, pageNumber, pageSize);
    }

    @Transactional
    public Resp<Void> modifyAccount(ModifyAccountReq modifyAccountReq, Long accountId, Long relTenantId) {
        var qAccount = QAccount.account;
        var updateClause = sqlBuilder.update(qAccount)
                .where(qAccount.id.eq(accountId))
                .where(qAccount.relTenantId.eq(relTenantId));
        if (modifyAccountReq.getName() != null) {
            updateClause.set(qAccount.name, modifyAccountReq.getName());
        }
        if (modifyAccountReq.getAvatar() != null) {
            updateClause.set(qAccount.avatar, modifyAccountReq.getAvatar());
        }
        if (modifyAccountReq.getParameters() != null) {
            updateClause.set(qAccount.parameters, modifyAccountReq.getParameters());
        }
        return updateEntity(updateClause);
    }

    @Transactional
    public Resp<Void> deleteAccounts(Long relTenantId) {
        var qAccount = QAccount.account;
        var qAccountCert = QAccountCert.accountCert;
        var deleteAccountR = updateEntity(sqlBuilder
                .update(qAccount)
                .set(qAccount.delFlag, true)
                .where(qAccount.relTenantId.eq(relTenantId))
        );
        if (!deleteAccountR.ok()) {
            return deleteAccountR;
        }
        sqlBuilder
                .update(qAccountCert)
                .set(qAccountCert.delFlag, true)
                .where(qAccountCert.relAccountId.in(
                        JPAExpressions.select()
                                .select(qAccount.id)
                                .from(qAccount)
                                .where(qAccount.relTenantId.eq(relTenantId))))
                .execute();
        return Resp.success(null);
    }

    @Transactional
    public Resp<Void> deleteAccount(Long accountId, Long relTenantId) {
        var qAccount = QAccount.account;
        var deleteAccountR = updateEntity(sqlBuilder
                .update(qAccount)
                .set(qAccount.delFlag, true)
                .where(qAccount.id.eq(accountId))
                .where(qAccount.relTenantId.eq(relTenantId))
        );
        if (!deleteAccountR.ok()) {
            return deleteAccountR;
        }
        deleteAccountCerts(accountId, relTenantId);
        return Resp.success(null);
    }

    protected Boolean checkAccountMembership(Long accountId, Long relTenantId) {
        var qAccount = QAccount.account;
        var num = sqlBuilder
                .selectFrom(qAccount)
                .where(qAccount.id.eq(accountId))
                .where(qAccount.relTenantId.eq(relTenantId))
                .where(qAccount.delFlag.eq(false))
                .fetchCount();
        return num != 0;
    }

    // ========================== Cert ==============================

    @Transactional
    public Resp<Long> addAccountCert(AddAccountCertReq addAccountCertReq,
                                     Long relAccountId, Long relTenantId) {
        if (!checkAccountMembership(relAccountId, relTenantId)) {
            return Constant.RESP.NOT_FOUNT();
        }
        var processR = certProcessSK(addAccountCertReq.getKind(),
                addAccountCertReq.getAk(),
                addAccountCertReq.getSk(),
                relTenantId);
        if (!processR.ok()) {
            return Resp.error(processR);
        }
        return doAddAccountCert(addAccountCertReq, processR.getBody(), relAccountId, relTenantId);
    }

    private Resp<Long> doAddAccountCert(AddAccountCertReq addAccountCertReq, String processedSk, Long relAccountId, Long relTenantId) {
        var qAccountCert = QAccountCert.accountCert;
        if (sqlBuilder.select(qAccountCert.id)
                .from(qAccountCert)
                .where(qAccountCert.delFlag.eq(false))
                .where(qAccountCert.relTenantId.eq(relTenantId))
                .where(qAccountCert.kind.eq(addAccountCertReq.getKind()))
                .where(qAccountCert.ak.eq(addAccountCertReq.getAk()))
                .fetchCount() != 0) {
            return Resp.conflict("此凭证已存在");
        }
        var accountCert = AccountCert.builder()
                .kind(addAccountCertReq.getKind())
                .ak(addAccountCertReq.getAk())
                .sk(processedSk)
                .validTime(addAccountCertReq.getValidTime() != null ? addAccountCertReq.getValidTime() : Constant.NEVER_EXPIRE_TIME)
                .validTimes(addAccountCertReq.getValidTimes() != null ? addAccountCertReq.getValidTimes() : -1L)
                .relAccountId(relAccountId)
                .relTenantId(relTenantId)
                .build();
        return saveEntity(accountCert);
    }

    public Resp<List<AccountCertInfoResp>> findAccountCertInfo(Long relAccountId, Long relTenantId) {
        if (!checkAccountMembership(relAccountId, relTenantId)) {
            return Constant.RESP.NOT_FOUNT();
        }
        var qAccountCert = QAccountCert.accountCert;
        var qAccountCreateUser = QAccount.account;
        var qAccountUpdateUser = QAccount.account;
        var query = sqlBuilder
                .select(Projections.bean(
                        AccountCertInfoResp.class,
                        qAccountCert.id,
                        qAccountCert.kind,
                        qAccountCert.ak,
                        qAccountCert.sk,
                        qAccountCert.validTimes,
                        qAccountCert.validTime,
                        qAccountCert.delFlag,
                        qAccountCert.createTime,
                        qAccountCert.updateTime,
                        qAccountCreateUser.name.as("createUserName"),
                        qAccountUpdateUser.name.as("updateUserName")))
                .from(qAccountCert)
                .leftJoin(qAccountCreateUser).on(qAccountCert.createUser.eq(qAccountCreateUser.id))
                .leftJoin(qAccountUpdateUser).on(qAccountCert.updateUser.eq(qAccountUpdateUser.id))
                .where(qAccountCert.relAccountId.eq(relAccountId));
        return findDTOs(query);
    }

    @Transactional
    public Resp<Void> modifyAccountCert(ModifyAccountCertReq modifyAccountCertReq, Long accountCertId,
                                        Long relAccountId, Long relTenantId) {
        if (!checkAccountMembership(relAccountId, relTenantId)) {
            return Constant.RESP.NOT_FOUNT();
        }
        var qAccountCert = QAccountCert.accountCert;
        var updateClause = sqlBuilder.update(qAccountCert)
                .where(qAccountCert.id.eq(accountCertId))
                .where(qAccountCert.relAccountId.eq(relAccountId));
        if (modifyAccountCertReq.getValidTime() != null) {
            updateClause.set(qAccountCert.validTime, modifyAccountCertReq.getValidTime());
        }
        if (modifyAccountCertReq.getValidTimes() != null) {
            updateClause.set(qAccountCert.validTimes, modifyAccountCertReq.getValidTimes());
        }
        return updateEntity(updateClause);
    }

    @Transactional
    public Resp<Void> deleteAccountCerts(Long relAccountId, Long relTenantId) {
        if (!checkAccountMembership(relAccountId, relTenantId)) {
            return Constant.RESP.NOT_FOUNT();
        }
        var qAccountCert = QAccountCert.accountCert;
        sqlBuilder
                .update(qAccountCert)
                .set(qAccountCert.delFlag, true)
                .where(qAccountCert.relAccountId.eq(relAccountId))
                .execute();
        return Resp.success(null);
    }

    @Transactional
    public Resp<Void> deleteAccountCert(Long accountCertId, Long relAccountId, Long relTenantId) {
        if (!checkAccountMembership(relAccountId, relTenantId)) {
            return Constant.RESP.NOT_FOUNT();
        }
        var qAccountCert = QAccountCert.accountCert;
        return updateEntity(sqlBuilder
                .update(qAccountCert)
                .set(qAccountCert.delFlag, true)
                .where(qAccountCert.id.eq(accountCertId))
                .where(qAccountCert.relAccountId.eq(relAccountId))
        );
    }

    // ========================== Post ==============================

    @Transactional
    public Resp<Long> addAccountPost(AddAccountPostReq addAccountPostReq, Long relAccountId, Long relTenantId) {
        if (!checkAccountMembership(relAccountId, relTenantId)) {
            return Constant.RESP.NOT_FOUNT();
        }
        var accountCert = AccountPost.builder()
                .relPostId(addAccountPostReq.getRelPostId())
                .relAccountId(relAccountId)
                .build();
        return saveEntity(accountCert);
    }

    public Resp<List<AccountPostInfoResp>> findAccountPostInfo(Long relAccountId, Long relTenantId) {
        if (!checkAccountMembership(relAccountId, relTenantId)) {
            return Constant.RESP.NOT_FOUNT();
        }
        var qAccountPost = QAccountPost.accountPost;
        var query = sqlBuilder
                .select(Projections.bean(
                        AccountPostInfoResp.class,
                        qAccountPost.id,
                        qAccountPost.relPostId))
                .from(qAccountPost)
                .where(qAccountPost.relAccountId.eq(relAccountId));
        return findDTOs(query);
    }

    @Transactional
    public Resp<Void> deleteAccountPost(Long accountPostId, Long relAccountId, Long relTenantId) {
        if (!checkAccountMembership(relAccountId, relTenantId)) {
            return Constant.RESP.NOT_FOUNT();
        }
        var qAccountPost = QAccountPost.accountPost;
        return deleteEntity(sqlBuilder
                .delete(qAccountPost)
                .where(qAccountPost.id.eq(accountPostId))
                .where(qAccountPost.relAccountId.eq(relAccountId))
        );
    }

    // ========================== Others ==============================

    public void certSendVC(String ak, Long tenantId) {
        String tmpSk = (int) ((Math.random() * 9 + 1) * 1000) + "";
        Dew.cluster.cache.setex(SK_KIND_VCODE_TMP_REL + tenantId + ":" + ak, tmpSk, SK_KIND_VCODE_EXPRIE_SEC);
    }

    private Resp<String> certProcessSK(AccountCert.Kind certKind, String ak, String sk, Long tenantId) {
        switch (certKind) {
            case EMAIL:
            case PHONE:
                String tmpSk = Dew.cluster.cache.get(SK_KIND_VCODE_TMP_REL + tenantId + ":" + ak);
                if (tmpSk == null) {
                    return Resp.badRequest("验证码不存在或已过期");
                }
                if (!tmpSk.equalsIgnoreCase(sk)) {
                    return Resp.badRequest("验证码错误");
                }
                return Resp.success("");
            case WECHAT:
                // TODO
                return Resp.success("");
            case USERNAME:
                return Resp.success($.security.digest.digest(ak + sk, "SHA-512"));
            default:
                return Resp.notFound("凭证类型不合法");
        }
    }

    private Resp<Void> validateSK(AccountCert.Kind certKind,
                                  String ak, String inputSk, String storageSk, Long tenantId) {
        switch (certKind) {
            case EMAIL:
            case PHONE:
                String tmpSk = Dew.cluster.cache.get(SK_KIND_VCODE_TMP_REL + tenantId + ":" + ak);
                if (tmpSk == null) {
                    return Resp.badRequest("验证码不存在或已过期，请重新获取");
                }
                if (!tmpSk.equalsIgnoreCase(inputSk)) {
                    if (Dew.cluster.cache.incrBy(SK_KIND_VCODE_ERROR_TIMES + tenantId + ":" + ak, 1)
                            >= SK_KIND_VCODE_MAX_ERROR_TIMES) {
                        Dew.cluster.cache.del(SK_KIND_VCODE_TMP_REL + tenantId + ":" + ak);
                        Dew.cluster.cache.del(SK_KIND_VCODE_ERROR_TIMES + tenantId + ":" + ak);
                        return Resp.badRequest("验证码不存在或已过期，请重新获取");
                    }
                    return Resp.badRequest("验证码错误");
                }
                Dew.cluster.cache.del(SK_KIND_VCODE_TMP_REL + tenantId + ":" + ak);
                Dew.cluster.cache.del(SK_KIND_VCODE_ERROR_TIMES + tenantId + ":" + ak);
                return Resp.success(null);
            case WECHAT:
                // TODO
                return Resp.success(null);
            case USERNAME:
                if (!$.security.digest.validate(ak + inputSk, storageSk, "SHA-512")) {
                    return Resp.badRequest("密码错误");
                } else {
                    return Resp.success(null);
                }
            default:
                return Resp.notFound("凭证类型不合法");
        }
    }

    private Set<OptInfo.RoleInfo> findRoleInfo(Long accountId) {
        var qAccountPost = QAccountPost.accountPost;
        var qPost = QPost.post;
        var qPosition = QPosition.position;
        var qOrganization = QOrganization.organization;
        return sqlBuilder.select(qPosition.code, qPosition.relAppId, qPosition.name, qOrganization.name)
                .from(qAccountPost)
                .leftJoin(qPost).on(qAccountPost.relPostId.eq(qPost.id))
                .leftJoin(qPosition).on(qPost.relPositionCode.eq(qPosition.code))
                .leftJoin(qOrganization).on(qPost.relOrganizationId.eq(qOrganization.id))
                .where(qAccountPost.relAccountId.eq(accountId))
                .fetch()
                .stream()
                .map(info -> {
                    var positionCode = info.get(0, String.class);
                    var positionRelAppId = info.get(1, Long.class);
                    var positionName = info.get(2, String.class);
                    var orgName = info.get(3, String.class);
                    return new OptInfo.RoleInfo()
                            .setCode(positionRelAppId + PermissionService.ROLE_SPLIT + positionCode)
                            .setName(orgName + " " + positionName);
                })
                .collect(Collectors.toSet());

    }
}
