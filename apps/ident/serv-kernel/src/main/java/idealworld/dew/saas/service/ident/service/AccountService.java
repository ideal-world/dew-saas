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
import group.idealworld.dew.Dew;
import group.idealworld.dew.core.auth.dto.OptInfo;
import idealworld.dew.saas.common.Constant;
import idealworld.dew.saas.common.service.dto.IdentOptInfo;
import idealworld.dew.saas.service.ident.domain.*;
import idealworld.dew.saas.service.ident.dto.account.*;
import idealworld.dew.saas.service.ident.enumeration.AccountCertKind;
import idealworld.dew.saas.service.ident.enumeration.AccountStatus;
import idealworld.dew.saas.service.ident.utils.KeyHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author gudaoxuri
 */
@Service
@Slf4j
public class AccountService extends IdentBasicService {

    private static final String SK_KIND_VCODE_TMP_REL = "sk-kind:vocde:tmp-rel:";
    private static final String SK_KIND_VCODE_ERROR_TIMES = "sk-kind:vocde:error-times:";
    // TODO config
    private static final int SK_KIND_VCODE_EXPRIE_SEC = 60 * 5;
    private static final int SK_KIND_VCODE_MAX_ERROR_TIMES = 5;

    @Autowired
    private TenantService tenantService;
    @Autowired
    private PostService postService;

    @Transactional
    public Resp<IdentOptInfo> login(LoginReq loginReq, Long relTenantId) {
        log.info("login : [{}] {}", relTenantId,$.json.toJsonString(loginReq));
        var qAccount = QAccount.account;
        var qAccountCert = QAccountCert.accountCert;
        var qTenantCertConfig = QTenantCertConfig.tenantCertConfig;
        var accountInfo = sqlBuilder
                .select(qAccountCert.sk, qAccount.id, qAccount.parameters)
                .from(qAccountCert)
                .innerJoin(qTenantCertConfig).on(qAccountCert.relTenantId.eq(qTenantCertConfig.relTenantId))
                .leftJoin(qAccount).on(qAccountCert.relAccountId.eq(qAccount.id))
                .where(qTenantCertConfig.kind.eq(loginReq.getCertKind()))
                .where(qAccountCert.kind.eq(loginReq.getCertKind()))
                .where(qAccountCert.ak.eq(loginReq.getAk()))
                .where(qAccountCert.validTime.after(new Date()))
                .where(qAccount.relTenantId.eq(relTenantId))
                .where(qAccount.status.eq(AccountStatus.ENABLED))
                .fetchOne();
        if (accountInfo == null) {
            log.warn("Login Fail: [{}] AK {} not exist or expired",relTenantId,loginReq.getAk());
            return Resp.notFound("登录凭证不存在或已过期");
        }
        var certSk = accountInfo.get(0, String.class);
        var accountId = accountInfo.get(1, Long.class);
        var parameters = accountInfo.get(2, String.class);
        var validateR = validateSK(loginReq.getCertKind(), loginReq.getAk(), loginReq.getSk(), certSk, relTenantId);
        if (!validateR.ok()) {
            log.warn("Login Fail: [{}] SK {} un-match",relTenantId,loginReq.getAk());
            return Resp.error(validateR);
        }
        log.info("Login Success:  [{}] ak {}",relTenantId,loginReq.getAk());
        String token = KeyHelper.generateToken();
        var optInfo = new IdentOptInfo()
                // 转成String避免转化成Integer
                .setAccountCode(accountId + "")
                .setToken(token)
                .setRoleInfo(findRoleInfo(accountId));
        optInfo.setRelTenantId(relTenantId);
        if (StringUtils.isEmpty(parameters)) {
            parameters = "{}";
        }
        optInfo.setParameters($.json.toMap(parameters, String.class, Object.class));
        Dew.auth.setOptInfo(optInfo);
        return Resp.success(optInfo);
    }

    @Transactional
    public Resp<Void> logout(Long accountId, String token) {
        log.info("Logout Account {} by token {}",accountId,token);
        Dew.auth.removeOptInfo(token);
        return Resp.success(null);
    }

    @Transactional
    public Resp<Long> addAccountExt(AddAccountReq addAccountReq, Long relTenantId) {
        var checkValidRuleAndReturnValidTimeR = tenantService.checkValidRuleAndReturnValidTime(addAccountReq.getCertReq().getKind(),
                addAccountReq.getCertReq().getSk(), relTenantId);
        if (!checkValidRuleAndReturnValidTimeR.ok()) {
            return Resp.error(checkValidRuleAndReturnValidTimeR);
        }
        var processR = certProcessSK(addAccountReq.getCertReq().getKind(),
                addAccountReq.getCertReq().getAk(),
                addAccountReq.getCertReq().getSk(),
                relTenantId);
        if (!processR.ok()) {
            return Resp.error(processR);
        }
        var qAccountCert = QAccountCert.accountCert;
        if (sqlBuilder.select(qAccountCert.id)
                .from(qAccountCert)
                .where(qAccountCert.relTenantId.eq(relTenantId))
                .where(qAccountCert.kind.eq(addAccountReq.getCertReq().getKind()))
                .where(qAccountCert.ak.eq(addAccountReq.getCertReq().getAk()))
                .fetchCount() != 0) {
            return Resp.conflict("此凭证已存在");
        }
        log.info("Add Account : [{}] {}",relTenantId,$.json.toJsonString(addAccountReq));
        var account = Account.builder()
                .name(addAccountReq.getName())
                .avatar(addAccountReq.getAvatar() != null ? addAccountReq.getAvatar() : "")
                .parameters(addAccountReq.getParameters() != null ? addAccountReq.getParameters() : "{}")
                .status(AccountStatus.ENABLED)
                .relTenantId(relTenantId)
                .build();
        saveEntity(account);
        doAddAccountCert(addAccountReq.getCertReq(), processR.getBody(), checkValidRuleAndReturnValidTimeR.getBody(), account.getId(), relTenantId);
        if (addAccountReq.getPostReq() == null) {
            addAccountReq.setPostReq(AddAccountPostReq.builder()
                    .relPostId(postService.getDefaultPostId())
                    .build());
        }
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
                        qAccount.status,
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
                        qAccount.status,
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
        if (modifyAccountReq.getStatus() != null) {
            updateClause.set(qAccount.status, modifyAccountReq.getStatus());
        }
        return updateEntity(updateClause);
    }

    @Transactional
    public Resp<Void> deleteAccount(Long accountId, Long relTenantId) {
        deleteAccountCerts(accountId, relTenantId);
        deleteAccountPosts(accountId, relTenantId);
        var qAccount = QAccount.account;
        return deleteEntity(sqlBuilder
                .delete(qAccount)
                .where(qAccount.id.eq(accountId))
                .where(qAccount.relTenantId.eq(relTenantId))
        );
    }

    @Transactional
    protected Resp<Void> deleteAccounts(Long relTenantId) {
        deleteAccountCerts(relTenantId);
        deleteAccountPosts(relTenantId);
        var qAccount = QAccount.account;
        return softDelEntity(sqlBuilder
                .selectFrom(qAccount)
                .where(qAccount.relTenantId.eq(relTenantId))
        );
    }

    protected Boolean checkAccountMembership(Long accountId, Long relTenantId) {
        var qAccount = QAccount.account;
        var num = sqlBuilder
                .selectFrom(qAccount)
                .where(qAccount.id.eq(accountId))
                .where(qAccount.relTenantId.eq(relTenantId))
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
        var checkValidRuleAndReturnValidTimeR = tenantService.checkValidRuleAndReturnValidTime(addAccountCertReq.getKind(),
                addAccountCertReq.getSk(), relTenantId);
        if (!checkValidRuleAndReturnValidTimeR.ok()) {
            return Resp.error(checkValidRuleAndReturnValidTimeR);
        }
        var qAccountCert = QAccountCert.accountCert;
        if (sqlBuilder.select(qAccountCert.id)
                .from(qAccountCert)
                .where(qAccountCert.relTenantId.eq(relTenantId))
                .where(qAccountCert.kind.eq(addAccountCertReq.getKind()))
                .where(qAccountCert.ak.eq(addAccountCertReq.getAk()))
                .fetchCount() != 0) {
            return Resp.conflict("此凭证已存在");
        }
        log.info("Add Account Cert : [{}] {} : {}",relTenantId,relAccountId, $.json.toJsonString(addAccountCertReq));
        var processR = certProcessSK(addAccountCertReq.getKind(),
                addAccountCertReq.getAk(),
                addAccountCertReq.getSk(),
                relTenantId);
        if (!processR.ok()) {
            return Resp.error(processR);
        }
        return doAddAccountCert(addAccountCertReq, processR.getBody(), checkValidRuleAndReturnValidTimeR.getBody(), relAccountId, relTenantId);
    }

    private Resp<Long> doAddAccountCert(AddAccountCertReq addAccountCertReq, String processedSk, Date validTime,
                                        Long relAccountId, Long relTenantId) {
        var accountCert = AccountCert.builder()
                .kind(addAccountCertReq.getKind())
                .ak(addAccountCertReq.getAk())
                .sk(processedSk)
                .validTime(validTime)
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
                        qAccountCert.validTime,
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

    public Resp<String> getAccountCertAk(Long relAccountId, String accountCertKind, Long relTenantId) {
        if (!checkAccountMembership(relAccountId, relTenantId)) {
            return Constant.RESP.NOT_FOUNT();
        }
        var qAccountCert = QAccountCert.accountCert;
        var query = sqlBuilder
                .select(qAccountCert.ak)
                .from(qAccountCert)
                .where(qAccountCert.relAccountId.eq(relAccountId))
                .where(qAccountCert.kind.eq(AccountCertKind.parse(accountCertKind)))
                .where(qAccountCert.validTime.gt(new Date()));
        return getDTO(query);
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
        return updateEntity(updateClause);
    }

    @Transactional
    public Resp<Long> deleteAccountCerts(Long relAccountId, Long relTenantId) {
        var qAccountCert = QAccountCert.accountCert;
        return deleteEntities(sqlBuilder
                .delete(qAccountCert)
                .where(qAccountCert.relTenantId.eq(relTenantId))
                .where(qAccountCert.relAccountId.eq(relAccountId)));
    }

    @Transactional
    public Resp<Void> deleteAccountCert(Long accountCertId, Long relAccountId, Long relTenantId) {
        var qAccountCert = QAccountCert.accountCert;
        return deleteEntity(sqlBuilder
                .delete(qAccountCert)
                .where(qAccountCert.id.eq(accountCertId))
                .where(qAccountCert.relTenantId.eq(relTenantId))
                .where(qAccountCert.relAccountId.eq(relAccountId)));
    }

    private Resp<Long> deleteAccountCerts(Long relTenantId) {
        var qAccountCert = QAccountCert.accountCert;
        return softDelEntities(sqlBuilder
                .selectFrom(qAccountCert)
                .where(qAccountCert.relTenantId.eq(relTenantId)));
    }

    // ========================== Post ==============================

    @Transactional
    public Resp<Long> addAccountPost(AddAccountPostReq addAccountPostReq, Long relAccountId, Long relTenantId) {
        if (!checkAccountMembership(relAccountId, relTenantId)) {
            return Constant.RESP.NOT_FOUNT();
        }
        var qAccountPost = QAccountPost.accountPost;
        if (sqlBuilder.select(qAccountPost.id)
                .from(qAccountPost)
                .where(qAccountPost.relAccountId.eq(relAccountId))
                .where(qAccountPost.relPostId.eq(addAccountPostReq.getRelPostId()))
                .fetchCount() != 0) {
            return Resp.conflict("此关联岗位已存在");
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
    public Resp<Long> deleteAccountPosts(Long relAccountId, Long relTenantId) {
        if (!checkAccountMembership(relAccountId, relTenantId)) {
            return Constant.RESP.NOT_FOUNT();
        }
        var qAccountPost = QAccountPost.accountPost;
        return deleteEntities(sqlBuilder
                .delete(qAccountPost)
                .where(qAccountPost.relAccountId.eq(relAccountId)));
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

    @Transactional
    protected Resp<Long> deleteAccountPosts(Long relTenantId) {
        var qAccount = QAccount.account;
        var qAccountPost = QAccountPost.accountPost;
        var accountIds = sqlBuilder.select(qAccount.id)
                .from(qAccount)
                .where(qAccount.relTenantId.eq(relTenantId))
                .fetch();
        return deleteEntities(sqlBuilder
                .delete(qAccountPost)
                .where(qAccountPost.relAccountId.in(accountIds)));
    }

    @Transactional
    protected Resp<Long> deleteAccountPosts(List<Long> postIds) {
        if(postIds.isEmpty()){
            return Constant.RESP.NOT_FOUNT();
        }
        var qAccountPost = QAccountPost.accountPost;
        return deleteEntities(sqlBuilder
                .delete(qAccountPost)
                .where(qAccountPost.relPostId.in(postIds)));
    }

    // ========================== Others ==============================

    protected void updateAccountTenant(Long accountId, Long newTenantId) {
        var qAccount = QAccount.account;
        updateEntity(sqlBuilder
                .update(qAccount)
                .set(qAccount.relTenantId, newTenantId)
                .where(qAccount.id.eq(accountId))
        );
        var qAccountCert = QAccountCert.accountCert;
        updateEntity(sqlBuilder
                .update(qAccountCert)
                .set(qAccountCert.relTenantId, newTenantId)
                .where(qAccountCert.relAccountId.eq(accountId))
        );
    }

    public void certSendVC(String ak, Long tenantId) {
        String tmpSk = (int) ((Math.random() * 9 + 1) * 1000) + "";
        Dew.cluster.cache.setex(SK_KIND_VCODE_TMP_REL + tenantId + ":" + ak, tmpSk, SK_KIND_VCODE_EXPRIE_SEC);
    }

    private Resp<String> certProcessSK(AccountCertKind certKind, String ak, String sk, Long tenantId) {
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
            case USERNAME:
                return Resp.success($.security.digest.digest(ak + sk, "SHA-512"));
            default:
                return Resp.success("");
        }
    }

    private Resp<Void> validateSK(AccountCertKind certKind,
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
            case USERNAME:
                if (!$.security.digest.validate(ak + inputSk, storageSk, "SHA-512")) {
                    return Resp.badRequest("密码错误");
                } else {
                    return Resp.success(null);
                }
            default:
                return Resp.success(null);
        }
    }

    public Set<OptInfo.RoleInfo> findRoleInfo(Long accountId) {
        var qAccountPost = QAccountPost.accountPost;
        var qPost = QPost.post;
        var qPosition = QPosition.position;
        var qOrganization = QOrganization.organization;
        return sqlBuilder.select(qPost.relPositionCode, qPost.relOrganizationCode, qPost.relAppId, qPosition.name, qOrganization.name)
                .from(qAccountPost)
                .leftJoin(qPost).on(qAccountPost.relPostId.eq(qPost.id))
                .leftJoin(qPosition).on(qPost.relTenantId.eq(qPosition.relTenantId)
                        .and(qPost.relAppId.eq(qPosition.relAppId))
                        .and(qPost.relPositionCode.eq(qPosition.code)))
                .leftJoin(qOrganization).on(qPost.relTenantId.eq(qOrganization.relTenantId)
                        .and(qPost.relAppId.eq(qOrganization.relAppId))
                        .and(qPost.relOrganizationCode.eq(qOrganization.code)))
                .where(qAccountPost.relAccountId.eq(accountId))
                .fetch()
                .stream()
                .map(info -> {
                    var positionCode = info.get(0, String.class);
                    var orgCode = info.get(1, String.class);
                    if (orgCode == null || orgCode.isEmpty()) {
                        orgCode = Constant.OBJECT_UNDEFINED + "";
                    }
                    var relAppId = info.get(2, Long.class);
                    var positionName = info.get(3, String.class);
                    var orgName = info.get(4, String.class);
                    if (orgName == null) {
                        orgName = "";
                    }
                    return new OptInfo.RoleInfo()
                            .setCode(relAppId + Constant.ROLE_SPLIT + orgCode + Constant.ROLE_SPLIT + positionCode)
                            .setName(orgName + " " + positionName);
                })
                .collect(Collectors.toSet());
    }

}
