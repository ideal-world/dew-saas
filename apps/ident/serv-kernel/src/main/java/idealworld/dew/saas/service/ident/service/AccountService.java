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
import idealworld.dew.saas.common.enumeration.CommonStatus;
import idealworld.dew.saas.common.resp.StandardResp;
import idealworld.dew.saas.common.service.dto.IdentOptInfo;
import idealworld.dew.saas.service.ident.IdentConfig;
import idealworld.dew.saas.service.ident.domain.*;
import idealworld.dew.saas.service.ident.dto.account.*;
import idealworld.dew.saas.service.ident.enumeration.AccountIdentKind;
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
 * Account Service.
 *
 * @author gudaoxuri
 */
@Service
@Slf4j
public class AccountService extends IdentBasicService {

    private static final String SK_KIND_VCODE_TMP_REL = "sk-kind:vocde:tmp-rel:";
    private static final String SK_KIND_VCODE_ERROR_TIMES = "sk-kind:vocde:error-times:";

    private static final String BUSINESS_ACCOUNT = "ACCOUNT";
    private static final String BUSINESS_ACCOUNT_CERT = "ACCOUNT_CERT";
    private static final String BUSINESS_ACCOUNT_POST = "ACCOUNT_POST";

    @Autowired
    private IdentConfig identConfig;
    @Autowired
    private TenantService tenantService;
    @Autowired
    private PostService postService;

    /**
     * Login.
     *
     * @param loginReq    the login req
     * @param relTenantId the rel tenant id
     * @return the resp
     */
    @Transactional
    public Resp<IdentOptInfo> login(LoginReq loginReq, Long relTenantId) {
        log.info("login : [{}] {}", relTenantId, $.json.toJsonString(loginReq));
        var qAccount = QAccount.account;
        var qAccountIdent = QAccountIdent.accountIdent;
        var qTenantIdent = QTenantIdent.tenantIdent;
        var now = new Date();
        var accountInfo = sqlBuilder
                .select(qAccountIdent.sk, qAccount.id, qAccount.openId, qAccount.parameters)
                .from(qAccountIdent)
                .innerJoin(qTenantIdent).on(
                        qAccountIdent.relTenantId.eq(qTenantIdent.relTenantId)
                                .and(qTenantIdent.status.eq(CommonStatus.ENABLED)))
                .leftJoin(qAccount).on(qAccountIdent.relAccountId.eq(qAccount.id))
                .where(qTenantIdent.kind.eq(loginReq.getIdentKind()))
                .where(qAccountIdent.kind.eq(loginReq.getIdentKind()))
                .where(qAccountIdent.ak.eq(loginReq.getAk()))
                .where(qAccountIdent.validStartTime.before(now))
                .where(qAccountIdent.validEndTime.after(now))
                .where(qAccount.relTenantId.eq(relTenantId))
                .where(qAccount.status.eq(CommonStatus.ENABLED))
                .fetchOne();
        if (accountInfo == null) {
            log.warn("Login Fail: [{}] AK {} not exist or expired", relTenantId, loginReq.getAk());
            return StandardResp.notFound(BUSINESS_ACCOUNT, "登录认证 %s 不存在或已过期", loginReq.getAk());
        }
        var identSk = accountInfo.get(0, String.class);
        var accountId = accountInfo.get(1, Long.class);
        var openId = accountInfo.get(2, String.class);
        var parameters = accountInfo.get(3, String.class);
        var validateR = validateSK(loginReq.getIdentKind(), loginReq.getAk(), loginReq.getSk(), identSk, relTenantId);
        if (!validateR.ok()) {
            log.warn("Login Fail: [{}] SK {} un-match", relTenantId, loginReq.getAk());
            return StandardResp.error(validateR);
        }
        log.info("Login Success:  [{}] ak {}", relTenantId, loginReq.getAk());
        String token = KeyHelper.generateToken();
        var optInfo = new IdentOptInfo()
                .setAccountCode(openId)
                .setToken(token)
                .setRoleInfo(findRoleInfo(accountId));
        optInfo.setRelTenantId(relTenantId);
        if (StringUtils.isEmpty(parameters)) {
            parameters = "{}";
        }
        optInfo.setParameters($.json.toMap(parameters, String.class, Object.class));
        Dew.auth.setOptInfo(optInfo);
        return StandardResp.success(optInfo);
    }

    /**
     * Logout.
     *
     * @param openId the open id
     * @param token  the token
     * @return the resp
     */
    @Transactional
    public Resp<Void> logout(String openId, String token) {
        log.info("Logout Account {} by token {}", openId, token);
        Dew.auth.removeOptInfo(token);
        return StandardResp.success(null);
    }

    /**
     * Add account ext.
     *
     * @param addAccountReq the add account req
     * @param relTenantId   the rel tenant id
     * @return the resp
     */
    @Transactional
    public Resp<Long> addAccountExt(AddAccountReq addAccountReq, Long relTenantId) {
        var checkValidRuleAndReturnValidTimeR = tenantService.checkValidRuleAndReturnValidTime(addAccountReq.getIdentReq().getKind(),
                addAccountReq.getIdentReq().getSk(), relTenantId);
        if (!checkValidRuleAndReturnValidTimeR.ok()) {
            return StandardResp.error(checkValidRuleAndReturnValidTimeR);
        }
        var processR = identProcessSK(addAccountReq.getIdentReq().getKind(),
                addAccountReq.getIdentReq().getAk(),
                addAccountReq.getIdentReq().getSk(),
                relTenantId);
        if (!processR.ok()) {
            return StandardResp.error(processR);
        }
        var qAccountIdent = QAccountIdent.accountIdent;
        if (sqlBuilder.select(qAccountIdent.id)
                .from(qAccountIdent)
                .where(qAccountIdent.relTenantId.eq(relTenantId))
                .where(qAccountIdent.kind.eq(addAccountReq.getIdentReq().getKind()))
                .where(qAccountIdent.ak.eq(addAccountReq.getIdentReq().getAk()))
                .fetchCount() != 0) {
            return StandardResp.conflict(BUSINESS_ACCOUNT_CERT, "认证已存在");
        }
        log.info("Add Account : [{}] {}", relTenantId, $.json.toJsonString(addAccountReq));
        var account = Account.builder()
                .openId($.field.createUUID())
                .name(addAccountReq.getName())
                .avatar(addAccountReq.getAvatar() != null ? addAccountReq.getAvatar() : "")
                .parameters(addAccountReq.getParameters() != null ? addAccountReq.getParameters() : "{}")
                // TODO 父账号
                .parentId(Constant.OBJECT_UNDEFINED)
                .status(CommonStatus.ENABLED)
                .relTenantId(relTenantId)
                .build();
        saveEntity(account);
        doAddAccountIdent(addAccountReq.getIdentReq(), processR.getBody(), checkValidRuleAndReturnValidTimeR.getBody(), account.getId(), relTenantId);
        if (addAccountReq.getPostReq() == null) {
            addAccountReq.setPostReq(AddAccountPostReq.builder()
                    .relPostId(postService.getDefaultPostId())
                    .build());
        }
        addAccountPost(addAccountReq.getPostReq(), account.getId(), relTenantId);
        return StandardResp.success(account.getId());
    }

    /**
     * Gets account info.
     *
     * @param accountId   the account id
     * @param relTenantId the rel tenant id
     * @return the account info
     */
    public Resp<AccountInfoResp> getAccountInfo(Long accountId, Long relTenantId) {
        var qAccount = QAccount.account;
        var qAccountCreateUser = QAccount.account;
        var qAccountUpdateUser = QAccount.account;
        var query = sqlBuilder
                .select(Projections.bean(
                        AccountInfoResp.class,
                        qAccount.id,
                        qAccount.openId,
                        qAccount.name,
                        qAccount.avatar,
                        qAccount.parameters,
                        qAccount.status,
                        qAccount.createTime,
                        qAccount.updateTime,
                        qAccountCreateUser.name.as("createUserName"),
                        qAccountUpdateUser.name.as("updateUserName")))
                .from(qAccount)
                .leftJoin(qAccountCreateUser).on(qAccount.createUser.eq(qAccountCreateUser.openId))
                .leftJoin(qAccountUpdateUser).on(qAccount.updateUser.eq(qAccountUpdateUser.openId))
                .where(qAccount.id.eq(accountId))
                .where(qAccount.relTenantId.eq(relTenantId));
        return getDTO(query);
    }

    /**
     * Page account info.
     *
     * @param pageNumber  the page number
     * @param pageSize    the page size
     * @param relTenantId the rel tenant id
     * @return the resp
     */
    public Resp<Page<AccountInfoResp>> pageAccountInfo(Long pageNumber, Integer pageSize, Long relTenantId) {
        var qAccount = QAccount.account;
        var qAccountCreateUser = QAccount.account;
        var qAccountUpdateUser = QAccount.account;
        var query = sqlBuilder
                .select(Projections.bean(
                        AccountInfoResp.class,
                        qAccount.id,
                        qAccount.openId,
                        qAccount.name,
                        qAccount.avatar,
                        qAccount.parameters,
                        qAccount.status,
                        qAccount.createTime,
                        qAccount.updateTime,
                        qAccountCreateUser.name.as("createUserName"),
                        qAccountUpdateUser.name.as("updateUserName")))
                .from(qAccount)
                .leftJoin(qAccountCreateUser).on(qAccount.createUser.eq(qAccountCreateUser.openId))
                .leftJoin(qAccountUpdateUser).on(qAccount.updateUser.eq(qAccountUpdateUser.openId))
                .where(qAccount.relTenantId.eq(relTenantId));
        return pageDTOs(query, pageNumber, pageSize);
    }

    /**
     * Gets open id.
     *
     * @param accountId the account id
     * @return the open id
     */
    protected Resp<String> getOpenId(Long accountId) {
        var qAccount = QAccount.account;
        var openId = sqlBuilder.select(qAccount.openId)
                .from(qAccount)
                .where(qAccount.id.eq(accountId))
                .fetchOne();
        return StandardResp.success(openId);
    }

    /**
     * Modify account.
     *
     * @param modifyAccountReq the modify account req
     * @param accountId        the account id
     * @param relTenantId      the rel tenant id
     * @return the resp
     */
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

    /**
     * Delete account.
     *
     * @param accountId   the account id
     * @param relTenantId the rel tenant id
     * @return the resp
     */
    @Transactional
    public Resp<Void> deleteAccount(Long accountId, Long relTenantId) {
        deleteAccountIdents(accountId, relTenantId);
        deleteAccountPosts(accountId, relTenantId);
        var qAccount = QAccount.account;
        return deleteEntity(sqlBuilder
                .delete(qAccount)
                .where(qAccount.id.eq(accountId))
                .where(qAccount.relTenantId.eq(relTenantId))
        );
    }

    /**
     * Delete accounts.
     *
     * @param relTenantId the rel tenant id
     * @return the resp
     */
    @Transactional
    protected Resp<Void> deleteAccounts(Long relTenantId) {
        deleteAccountIdents(relTenantId);
        deleteAccountPosts(relTenantId);
        var qAccount = QAccount.account;
        return softDelEntity(sqlBuilder
                .selectFrom(qAccount)
                .where(qAccount.relTenantId.eq(relTenantId))
        );
    }

    /**
     * Check account membership.
     *
     * @param accountId   the account id
     * @param relTenantId the rel tenant id
     * @return the resp
     */
    protected Resp<Void> checkAccountMembership(Long accountId, Long relTenantId) {
        var qAccount = QAccount.account;
        var num = sqlBuilder
                .selectFrom(qAccount)
                .where(qAccount.id.eq(accountId))
                .where(qAccount.relTenantId.eq(relTenantId))
                .fetchCount();
        return num != 0 ? StandardResp.success(null)
                : StandardResp.unAuthorized(BUSINESS_ACCOUNT, "用户:%s 不属于租户:%s", accountId, relTenantId);
    }

    // ========================== Ident ==============================

    /**
     * Add account ident.
     *
     * @param addAccountIdentReq the add account ident req
     * @param relAccountId       the rel account id
     * @param relTenantId        the rel tenant id
     * @return the resp
     */
    @Transactional
    public Resp<Long> addAccountIdent(AddAccountIdentReq addAccountIdentReq,
                                      Long relAccountId, Long relTenantId) {
        var membershipCheckR = checkAccountMembership(relAccountId, relTenantId);
        if (!membershipCheckR.ok()) {
            return StandardResp.error(membershipCheckR);
        }
        var checkValidRuleAndReturnValidTimeR = tenantService.checkValidRuleAndReturnValidTime(addAccountIdentReq.getKind(),
                addAccountIdentReq.getSk(), relTenantId);
        if (!checkValidRuleAndReturnValidTimeR.ok()) {
            return StandardResp.error(checkValidRuleAndReturnValidTimeR);
        }
        var qAccountIdent = QAccountIdent.accountIdent;
        if (sqlBuilder.select(qAccountIdent.id)
                .from(qAccountIdent)
                .where(qAccountIdent.relTenantId.eq(relTenantId))
                .where(qAccountIdent.kind.eq(addAccountIdentReq.getKind()))
                .where(qAccountIdent.ak.eq(addAccountIdentReq.getAk()))
                .fetchCount() != 0) {
            return StandardResp.conflict(BUSINESS_ACCOUNT_CERT, "认证已存在");
        }
        log.info("Add Account Ident : [{}] {} : {}", relTenantId, relAccountId, $.json.toJsonString(addAccountIdentReq));
        var processR = identProcessSK(addAccountIdentReq.getKind(),
                addAccountIdentReq.getAk(),
                addAccountIdentReq.getSk(),
                relTenantId);
        if (!processR.ok()) {
            return StandardResp.error(processR);
        }
        return doAddAccountIdent(addAccountIdentReq, processR.getBody(), checkValidRuleAndReturnValidTimeR.getBody(), relAccountId, relTenantId);
    }

    private Resp<Long> doAddAccountIdent(AddAccountIdentReq addAccountIdentReq, String processedSk, Date validTime,
                                         Long relAccountId, Long relTenantId) {
        var accountIdent = AccountIdent.builder()
                .kind(addAccountIdentReq.getKind())
                .ak(addAccountIdentReq.getAk())
                .sk(processedSk)
                .validStartTime(new Date())
                .validEndTime(validTime)
                .validTimes(-1L)
                .relAccountId(relAccountId)
                .relTenantId(relTenantId)
                .build();
        return saveEntity(accountIdent);
    }

    /**
     * Find account ident info.
     *
     * @param relAccountId the rel account id
     * @param relTenantId  the rel tenant id
     * @return the resp
     */
    public Resp<List<AccountIdentInfoResp>> findAccountIdentInfo(Long relAccountId, Long relTenantId) {
        var membershipCheckR = checkAccountMembership(relAccountId, relTenantId);
        if (!membershipCheckR.ok()) {
            return StandardResp.error(membershipCheckR);
        }
        var qAccountIdent = QAccountIdent.accountIdent;
        var qAccountCreateUser = QAccount.account;
        var qAccountUpdateUser = QAccount.account;
        var query = sqlBuilder
                .select(Projections.bean(
                        AccountIdentInfoResp.class,
                        qAccountIdent.id,
                        qAccountIdent.kind,
                        qAccountIdent.ak,
                        qAccountIdent.sk,
                        qAccountIdent.validStartTime,
                        qAccountIdent.validEndTime,
                        qAccountIdent.validTimes,
                        qAccountIdent.createTime,
                        qAccountIdent.updateTime,
                        qAccountCreateUser.name.as("createUserName"),
                        qAccountUpdateUser.name.as("updateUserName")))
                .from(qAccountIdent)
                .leftJoin(qAccountCreateUser).on(qAccountIdent.createUser.eq(qAccountCreateUser.openId))
                .leftJoin(qAccountUpdateUser).on(qAccountIdent.updateUser.eq(qAccountUpdateUser.openId))
                .where(qAccountIdent.relAccountId.eq(relAccountId));
        return findDTOs(query);
    }

    /**
     * Gets account ident ak.
     *
     * @param accountOpenId    the account open id
     * @param accountIdentKind the account ident kind
     * @param relTenantId      the rel tenant id
     * @return the account ident ak
     */
    public Resp<String> getAccountIdentAk(String accountOpenId, String accountIdentKind, Long relTenantId) {
        var qAccountIdent = QAccountIdent.accountIdent;
        var qAccount = QAccount.account;
        var now = new Date();
        var query = sqlBuilder
                .select(qAccountIdent.ak)
                .from(qAccountIdent)
                .leftJoin(qAccount).on(qAccount.id.eq(qAccountIdent.relAccountId))
                .where(qAccount.openId.eq(accountOpenId))
                .where(qAccount.relTenantId.eq(relTenantId))
                .where(qAccountIdent.kind.eq(AccountIdentKind.parse(accountIdentKind)))
                .where(qAccountIdent.validStartTime.before(now))
                .where(qAccountIdent.validEndTime.after(now));
        return getDTO(query);
    }

    /**
     * Modify account ident.
     *
     * @param modifyAccountIdentReq the modify account ident req
     * @param accountIdentId        the account ident id
     * @param relAccountId          the rel account id
     * @param relTenantId           the rel tenant id
     * @return the resp
     */
    @Transactional
    public Resp<Void> modifyAccountIdent(ModifyAccountIdentReq modifyAccountIdentReq, Long accountIdentId,
                                         Long relAccountId, Long relTenantId) {
        var membershipCheckR = checkAccountMembership(relAccountId, relTenantId);
        if (!membershipCheckR.ok()) {
            return StandardResp.error(membershipCheckR);
        }
        var qAccountIdent = QAccountIdent.accountIdent;
        var updateClause = sqlBuilder.update(qAccountIdent)
                .where(qAccountIdent.id.eq(accountIdentId))
                .where(qAccountIdent.relAccountId.eq(relAccountId));
        if (modifyAccountIdentReq.getSk() != null) {
            var accountIdentInfo = sqlBuilder.select(qAccountIdent.kind, qAccountIdent.ak)
                    .from(qAccountIdent)
                    .where(qAccountIdent.id.eq(accountIdentId))
                    .fetchOne();
            var accountIdentKind = accountIdentInfo.get(0, AccountIdentKind.class);
            var accountIdentAk = accountIdentInfo.get(1, String.class);
            var checkR = tenantService.checkValidRule(accountIdentKind, modifyAccountIdentReq.getSk(), relTenantId);
            if (!checkR.ok()) {
                return StandardResp.error(checkR);
            }
            var processedSkR = identProcessSK(accountIdentKind, accountIdentAk, modifyAccountIdentReq.getSk(), relTenantId);
            if (!processedSkR.ok()) {
                return StandardResp.error(processedSkR);
            }
            updateClause.set(qAccountIdent.sk, processedSkR.getBody());
        }
        if (modifyAccountIdentReq.getValidStartTime() != null) {
            updateClause.set(qAccountIdent.validStartTime, modifyAccountIdentReq.getValidStartTime());
        }
        if (modifyAccountIdentReq.getValidEndTime() != null) {
            updateClause.set(qAccountIdent.validEndTime, modifyAccountIdentReq.getValidEndTime());
        }
        if (modifyAccountIdentReq.getValidTimes() != null) {
            updateClause.set(qAccountIdent.validTimes, modifyAccountIdentReq.getValidTimes());
        }
        return updateEntity(updateClause);
    }

    /**
     * Delete account idents.
     *
     * @param relAccountId the rel account id
     * @param relTenantId  the rel tenant id
     * @return the resp
     */
    @Transactional
    public Resp<Long> deleteAccountIdents(Long relAccountId, Long relTenantId) {
        var qAccountIdent = QAccountIdent.accountIdent;
        return deleteEntities(sqlBuilder
                .delete(qAccountIdent)
                .where(qAccountIdent.relTenantId.eq(relTenantId))
                .where(qAccountIdent.relAccountId.eq(relAccountId)));
    }

    private Resp<Long> deleteAccountIdents(Long relTenantId) {
        var qAccountIdent = QAccountIdent.accountIdent;
        return softDelEntities(sqlBuilder
                .selectFrom(qAccountIdent)
                .where(qAccountIdent.relTenantId.eq(relTenantId)));
    }

    /**
     * Delete account ident.
     *
     * @param accountIdentId the account ident id
     * @param relAccountId   the rel account id
     * @param relTenantId    the rel tenant id
     * @return the resp
     */
    @Transactional
    public Resp<Void> deleteAccountIdent(Long accountIdentId, Long relAccountId, Long relTenantId) {
        var qAccountIdent = QAccountIdent.accountIdent;
        return deleteEntity(sqlBuilder
                .delete(qAccountIdent)
                .where(qAccountIdent.id.eq(accountIdentId))
                .where(qAccountIdent.relTenantId.eq(relTenantId))
                .where(qAccountIdent.relAccountId.eq(relAccountId)));
    }

    // ========================== Post ==============================

    /**
     * Add account post.
     *
     * @param addAccountPostReq the add account post req
     * @param relAccountId      the rel account id
     * @param relTenantId       the rel tenant id
     * @return the resp
     */
    @Transactional
    public Resp<Long> addAccountPost(AddAccountPostReq addAccountPostReq, Long relAccountId, Long relTenantId) {
        var membershipCheckR = checkAccountMembership(relAccountId, relTenantId);
        if (!membershipCheckR.ok()) {
            return StandardResp.error(membershipCheckR);
        }
        var qAccountPost = QAccountPost.accountPost;
        if (sqlBuilder.select(qAccountPost.id)
                .from(qAccountPost)
                .where(qAccountPost.relAccountId.eq(relAccountId))
                .where(qAccountPost.relPostId.eq(addAccountPostReq.getRelPostId()))
                .fetchCount() != 0) {
            return StandardResp.conflict(BUSINESS_ACCOUNT_POST, "关联岗位已存在");
        }
        var accountIdent = AccountPost.builder()
                .relPostId(addAccountPostReq.getRelPostId())
                .sort(addAccountPostReq.getSort() != null ? addAccountPostReq.getSort() : 0)
                .relAccountId(relAccountId)
                .build();
        return saveEntity(accountIdent);
    }

    /**
     * Find account post info.
     *
     * @param relAccountId the rel account id
     * @param relTenantId  the rel tenant id
     * @return the resp
     */
    public Resp<List<AccountPostInfoResp>> findAccountPostInfo(Long relAccountId, Long relTenantId) {
        var membershipCheckR = checkAccountMembership(relAccountId, relTenantId);
        if (!membershipCheckR.ok()) {
            return StandardResp.error(membershipCheckR);
        }
        var qAccountPost = QAccountPost.accountPost;
        var query = sqlBuilder
                .select(Projections.bean(
                        AccountPostInfoResp.class,
                        qAccountPost.id,
                        qAccountPost.relPostId,
                        qAccountPost.sort))
                .from(qAccountPost)
                .where(qAccountPost.relAccountId.eq(relAccountId));
        return findDTOs(query);
    }

    /**
     * Delete account post.
     *
     * @param accountPostId the account post id
     * @param relAccountId  the rel account id
     * @param relTenantId   the rel tenant id
     * @return the resp
     */
    @Transactional
    public Resp<Void> deleteAccountPost(Long accountPostId, Long relAccountId, Long relTenantId) {
        var membershipCheckR = checkAccountMembership(relAccountId, relTenantId);
        if (!membershipCheckR.ok()) {
            return StandardResp.error(membershipCheckR);
        }
        var qAccountPost = QAccountPost.accountPost;
        return deleteEntity(sqlBuilder
                .delete(qAccountPost)
                .where(qAccountPost.id.eq(accountPostId))
                .where(qAccountPost.relAccountId.eq(relAccountId))
        );
    }

    /**
     * Delete account posts.
     *
     * @param relAccountId the rel account id
     * @param relTenantId  the rel tenant id
     * @return the resp
     */
    @Transactional
    public Resp<Long> deleteAccountPosts(Long relAccountId, Long relTenantId) {
        var membershipCheckR = checkAccountMembership(relAccountId, relTenantId);
        if (!membershipCheckR.ok()) {
            return StandardResp.error(membershipCheckR);
        }
        var qAccountPost = QAccountPost.accountPost;
        return deleteEntities(sqlBuilder
                .delete(qAccountPost)
                .where(qAccountPost.relAccountId.eq(relAccountId)));
    }

    /**
     * Delete account posts.
     *
     * @param postIds the post ids
     * @return the resp
     */
    @Transactional
    protected Resp<Long> deleteAccountPosts(List<Long> postIds) {
        if (postIds.isEmpty()) {
            return StandardResp.notFound(BUSINESS_ACCOUNT_POST, "没有要删除的岗位");
        }
        var qAccountPost = QAccountPost.accountPost;
        return deleteEntities(sqlBuilder
                .delete(qAccountPost)
                .where(qAccountPost.relPostId.in(postIds)));
    }

    /**
     * Delete account posts.
     *
     * @param relTenantId the rel tenant id
     * @return the resp
     */
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


    // ========================== Others ==============================

    /**
     * Update account tenant.
     *
     * @param accountId   the account id
     * @param newTenantId the new tenant id
     */
    protected void updateAccountTenant(Long accountId, Long newTenantId) {
        var qAccount = QAccount.account;
        updateEntity(sqlBuilder
                .update(qAccount)
                .set(qAccount.relTenantId, newTenantId)
                .where(qAccount.id.eq(accountId))
        );
        var qAccountIdent = QAccountIdent.accountIdent;
        updateEntity(sqlBuilder
                .update(qAccountIdent)
                .set(qAccountIdent.relTenantId, newTenantId)
                .where(qAccountIdent.relAccountId.eq(accountId))
        );
    }

    /**
     * Send vc.
     *
     * @param ak       the ak
     * @param tenantId the tenant id
     */
    public void identSendVC(String ak, Long tenantId) {
        String tmpSk = (int) ((Math.random() * 9 + 1) * 1000) + "";
        Dew.cluster.cache.setex(SK_KIND_VCODE_TMP_REL + tenantId + ":" + ak, tmpSk, identConfig.getSecurity().getSkKindByVCodeExpireSec());
    }

    private Resp<String> identProcessSK(AccountIdentKind identKind, String ak, String sk, Long tenantId) {
        switch (identKind) {
            case EMAIL:
            case PHONE:
                String tmpSk = Dew.cluster.cache.get(SK_KIND_VCODE_TMP_REL + tenantId + ":" + ak);
                if (tmpSk == null) {
                    return StandardResp.badRequest(BUSINESS_ACCOUNT_CERT, "验证码不存在或已过期");
                }
                if (!tmpSk.equalsIgnoreCase(sk)) {
                    return StandardResp.badRequest(BUSINESS_ACCOUNT_CERT, "验证码错误");
                }
                return StandardResp.success("");
            case USERNAME:
                return StandardResp.success($.security.digest.digest(ak + sk, "SHA-512"));
            default:
                return StandardResp.success("");
        }
    }

    private Resp<Void> validateSK(AccountIdentKind identKind,
                                  String ak, String inputSk, String storageSk, Long tenantId) {
        switch (identKind) {
            case EMAIL:
            case PHONE:
                String tmpSk = Dew.cluster.cache.get(SK_KIND_VCODE_TMP_REL + tenantId + ":" + ak);
                if (tmpSk == null) {
                    return StandardResp.badRequest(BUSINESS_ACCOUNT_CERT, "验证码不存在或已过期，请重新获取");
                }
                if (!tmpSk.equalsIgnoreCase(inputSk)) {
                    if (Dew.cluster.cache.incrBy(SK_KIND_VCODE_ERROR_TIMES + tenantId + ":" + ak, 1)
                            >= identConfig.getSecurity().getSkKindByVCodeMaxErrorTimes()) {
                        Dew.cluster.cache.del(SK_KIND_VCODE_TMP_REL + tenantId + ":" + ak);
                        Dew.cluster.cache.del(SK_KIND_VCODE_ERROR_TIMES + tenantId + ":" + ak);
                        return StandardResp.badRequest(BUSINESS_ACCOUNT_CERT, "验证码不存在或已过期，请重新获取");
                    }
                    return StandardResp.badRequest(BUSINESS_ACCOUNT_CERT, "验证码错误");
                }
                Dew.cluster.cache.del(SK_KIND_VCODE_TMP_REL + tenantId + ":" + ak);
                Dew.cluster.cache.del(SK_KIND_VCODE_ERROR_TIMES + tenantId + ":" + ak);
                return StandardResp.success(null);
            case USERNAME:
                if (!$.security.digest.validate(ak + inputSk, storageSk, "SHA-512")) {
                    return StandardResp.badRequest(BUSINESS_ACCOUNT_CERT, "密码错误");
                } else {
                    return StandardResp.success(null);
                }
            default:
                return StandardResp.success(null);
        }
    }

    /**
     * Find role info set.
     *
     * @param accountId the account id
     * @return the set
     */
    protected Set<OptInfo.RoleInfo> findRoleInfo(Long accountId) {
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
