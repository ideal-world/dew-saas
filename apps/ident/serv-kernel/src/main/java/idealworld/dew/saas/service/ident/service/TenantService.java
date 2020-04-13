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
import com.ecfront.dew.common.Resp;
import com.querydsl.core.types.Projections;
import idealworld.dew.saas.common.Constant;
import idealworld.dew.saas.common.service.dto.IdentOptInfo;
import idealworld.dew.saas.service.ident.IdentConfig;
import idealworld.dew.saas.service.ident.domain.*;
import idealworld.dew.saas.service.ident.dto.account.AddAccountCertReq;
import idealworld.dew.saas.service.ident.dto.account.AddAccountPostReq;
import idealworld.dew.saas.service.ident.dto.account.AddAccountReq;
import idealworld.dew.saas.service.ident.dto.account.LoginReq;
import idealworld.dew.saas.service.ident.dto.tenant.*;
import idealworld.dew.saas.service.ident.enumeration.AccountCertKind;
import idealworld.dew.saas.service.ident.enumeration.CommonStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * @author gudaoxuri
 */
@Service
@Slf4j
public class TenantService extends IdentBasicService {

    private static final Map<String, Pattern> VALID_RULES = new ConcurrentHashMap<>();

    @Autowired
    private IdentConfig identConfig;
    @Autowired
    private AccountService accountService;
    @Autowired
    private OrganizationService organizationService;
    @Autowired
    private PositionService positionService;
    @Autowired
    private PostService postService;
    @Autowired
    private InterceptService interceptService;

    @Transactional
    public Resp<IdentOptInfo> registerTenant(RegisterTenantReq registerTenantReq) {
        log.info("Register Tenant : {}", $.json.toJsonString(registerTenantReq));
        if (!identConfig.isAllowTenantRegister()) {
            return Resp.forbidden("The current configuration does not allow tenants to self-register");
        }
        var tenantAdminPostId = postService.getTenantAdminPostId();
        var addAccountR = accountService.addAccountExt(AddAccountReq.builder()
                .name(registerTenantReq.getAccountName())
                .certReq(AddAccountCertReq.builder()
                        .kind(registerTenantReq.getCertKind())
                        .ak(registerTenantReq.getAk())
                        .sk(registerTenantReq.getSk())
                        .build())
                .postReq(AddAccountPostReq.builder()
                        .relPostId(tenantAdminPostId)
                        .build())
                .build(), Constant.OBJECT_UNDEFINED);
        if (!addAccountR.ok()) {
            return Resp.error(addAccountR);
        }
        var tenant = Tenant.builder()
                .name(registerTenantReq.getTenantName())
                .icon("")
                .parameters("{}")
                .status(CommonStatus.ENABLED)
                .createUser(addAccountR.getBody())
                .updateUser(addAccountR.getBody())
                .build();
        saveEntity(tenant);
        interceptService.changeTenantStatus(tenant.getId(), CommonStatus.ENABLED);
        accountService.updateAccountTenant(addAccountR.getBody(), tenant.getId());
        addTenantCert(AddTenantCertReq.builder()
                .kind(registerTenantReq.getCertKind())
                .build(), tenant.getId());
        // 自动登录
        return accountService.login(
                LoginReq.builder()
                        .certKind(registerTenantReq.getCertKind())
                        .ak(registerTenantReq.getAk())
                        .sk(registerTenantReq.getSk())
                        .build(), tenant.getId());
    }

    public Resp<TenantInfoResp> getTenantInfo(Long tenantId) {
        var qTenant = QTenant.tenant;
        var qAccountCreateUser = QAccount.account;
        var qAccountUpdateUser = QAccount.account;
        var tenantQuery = sqlBuilder
                .select(Projections.bean(
                        TenantInfoResp.class,
                        qTenant.id,
                        qTenant.name,
                        qTenant.icon,
                        qTenant.parameters,
                        qTenant.status,
                        qTenant.createTime,
                        qTenant.updateTime,
                        qAccountCreateUser.name.as("createUserName"),
                        qAccountUpdateUser.name.as("updateUserName")))
                .from(qTenant)
                .leftJoin(qAccountCreateUser).on(qTenant.createUser.eq(qAccountCreateUser.id))
                .leftJoin(qAccountUpdateUser).on(qTenant.updateUser.eq(qAccountUpdateUser.id))
                .where(qTenant.id.eq(tenantId));
        return getDTO(tenantQuery);
    }

    @Transactional
    public Resp<Void> modifyTenant(ModifyTenantReq modifyTenantReq, Long tenantId) {
        var qTenant = QTenant.tenant;
        var updateClause = sqlBuilder.update(qTenant)
                .where(qTenant.id.eq(tenantId))
                .set(qTenant.name, modifyTenantReq.getName());
        if (modifyTenantReq.getIcon() != null) {
            updateClause.set(qTenant.icon, modifyTenantReq.getIcon());
        }
        if (modifyTenantReq.getParameters() != null) {
            updateClause.set(qTenant.parameters, modifyTenantReq.getParameters());
        }
        if (modifyTenantReq.getStatus() != null) {
            updateClause.set(qTenant.status, modifyTenantReq.getStatus());
            interceptService.changeTenantStatus(tenantId, modifyTenantReq.getStatus());
        }
        return updateEntity(updateClause);
    }

    @Transactional
    public Resp<Void> unRegisterTenant(Long tenantId) {
        log.info("Un-Register Tenant : {}", tenantId);
        var qApp = QApp.app;
        var count = sqlBuilder
                .selectFrom(qApp)
                .where(qApp.relTenantId.eq(tenantId))
                .fetchCount();
        if (count != 0) {
            log.warn("Un-Register Tenant error: need to delete the app first");
            return Resp.conflict("请先删除租户下的所有应用");
        }
        // 删除账号
        accountService.deleteAccounts(tenantId);
        // 删除机构
        organizationService.deleteOrganization(Constant.OBJECT_UNDEFINED, tenantId);
        // 删除职位
        positionService.deletePositions(Constant.OBJECT_UNDEFINED, tenantId);
        // 删除租户凭证
        deleteTenantCert(tenantId);
        var qTenant = QTenant.tenant;
        interceptService.changeTenantStatus(tenantId, CommonStatus.DISABLED);
        return softDelEntity(sqlBuilder
                .selectFrom(qTenant)
                .where(qTenant.id.eq(tenantId))
        );
    }

    // ========================== Cert ==============================

    @Transactional
    public Resp<Long> addTenantCert(AddTenantCertReq addTenantCertReq,
                                    Long relTenantId) {
        var qTenantCert = QTenantCert.tenantCert;
        if (sqlBuilder.select(qTenantCert.id)
                .from(qTenantCert)
                .where(qTenantCert.relTenantId.eq(relTenantId))
                .where(qTenantCert.kind.eq(addTenantCertReq.getKind()))
                .fetchCount() != 0) {
            return Resp.conflict("此凭证已存在");
        }
        log.info("Add Tenant Cert : {}", $.json.toJsonString(addTenantCertReq));
        var tenantCert = TenantCert.builder()
                .kind(addTenantCertReq.getKind())
                .validRuleNote(addTenantCertReq.getValidRuleNote() != null ? addTenantCertReq.getValidRuleNote() : "")
                .validRule(addTenantCertReq.getValidRule() != null ? addTenantCertReq.getValidRule() : "")
                .validTimeSec(addTenantCertReq.getValidTimeSec() != null
                        ? addTenantCertReq.getValidTimeSec() : Constant.OBJECT_UNDEFINED)
                .oauthAk(addTenantCertReq.getOauthAk() != null ? addTenantCertReq.getOauthAk() : "")
                .oauthSk(addTenantCertReq.getOauthSk() != null ? addTenantCertReq.getOauthSk() : "")
                .status(CommonStatus.ENABLED)
                .relTenantId(relTenantId)
                .build();
        return saveEntity(tenantCert);
    }

    public Resp<List<TenantCertInfoResp>> findTenantCertInfo(Long relTenantId) {
        var qTenantCert = QTenantCert.tenantCert;
        var qAccountCreateUser = QAccount.account;
        var qAccountUpdateUser = QAccount.account;
        var query = sqlBuilder
                .select(Projections.bean(
                        TenantCertInfoResp.class,
                        qTenantCert.id,
                        qTenantCert.kind,
                        qTenantCert.validRuleNote,
                        qTenantCert.validRule,
                        qTenantCert.validTimeSec,
                        qTenantCert.oauthAk,
                        qTenantCert.oauthSk,
                        qTenantCert.status,
                        qTenantCert.createTime,
                        qTenantCert.updateTime,
                        qAccountCreateUser.name.as("createUserName"),
                        qAccountUpdateUser.name.as("updateUserName")))
                .from(qTenantCert)
                .leftJoin(qAccountCreateUser).on(qTenantCert.createUser.eq(qAccountCreateUser.id))
                .leftJoin(qAccountUpdateUser).on(qTenantCert.updateUser.eq(qAccountUpdateUser.id))
                .where(qTenantCert.relTenantId.eq(relTenantId));
        return findDTOs(query);
    }

    @Transactional
    public Resp<Void> modifyTenantCert(ModifyTenantCertReq modifyTenantCertReq, Long tenantCertId,
                                       Long relTenantId) {
        var qTenantCert = QTenantCert.tenantCert;
        var updateClause = sqlBuilder.update(qTenantCert)
                .where(qTenantCert.id.eq(tenantCertId))
                .where(qTenantCert.relTenantId.eq(relTenantId));
        if (modifyTenantCertReq.getValidRule() != null) {
            updateClause.set(qTenantCert.validRule, modifyTenantCertReq.getValidRule());
        }
        if (modifyTenantCertReq.getValidRuleNote() != null) {
            updateClause.set(qTenantCert.validRuleNote, modifyTenantCertReq.getValidRuleNote());
        }
        if (modifyTenantCertReq.getValidTimeSec() != null) {
            updateClause.set(qTenantCert.validTimeSec, modifyTenantCertReq.getValidTimeSec());
        }
        if (modifyTenantCertReq.getOauthAk() != null) {
            updateClause.set(qTenantCert.oauthAk, modifyTenantCertReq.getOauthAk());
        }
        if (modifyTenantCertReq.getOauthSk() != null) {
            updateClause.set(qTenantCert.oauthSk, modifyTenantCertReq.getOauthSk());
        }
        if (modifyTenantCertReq.getStatus() != null) {
            updateClause.set(qTenantCert.status, modifyTenantCertReq.getStatus());
        }
        return updateEntity(updateClause);
    }

    @Transactional
    public Resp<Long> deleteTenantCert(Long relTenantId) {
        log.info("Delete Tenant Cert By TenantId : {}", relTenantId);
        var qTenantCert = QTenantCert.tenantCert;
        return softDelEntities(sqlBuilder
                .selectFrom(qTenantCert)
                .where(qTenantCert.relTenantId.eq(relTenantId)));
    }

    @Transactional
    public Resp<Void> deleteTenantCert(Long tenantCertId, Long relTenantId) {
        log.info("Delete Tenant Cert By Id : {}", tenantCertId);
        var qTenantCert = QTenantCert.tenantCert;
        return deleteEntity(sqlBuilder
                .delete(qTenantCert)
                .where(qTenantCert.id.eq(tenantCertId))
                .where(qTenantCert.relTenantId.eq(relTenantId)));
    }

    protected Resp<Date> checkValidRuleAndReturnValidTime(AccountCertKind kind, String sk, Long relTenantId) {
        if (relTenantId.equals(Constant.OBJECT_UNDEFINED)) {
            // 表示租户管理员注册时临时分配的虚拟租户号
            return Resp.success(Constant.NEVER_EXPIRE_TIME);
        }
        var qTenantCert = QTenantCert.tenantCert;
        var tenantCert = sqlBuilder
                .select(qTenantCert.validRule,
                        qTenantCert.validTimeSec)
                .from(qTenantCert)
                .where(qTenantCert.status.eq(CommonStatus.ENABLED))
                .where(qTenantCert.kind.eq(kind))
                .where(qTenantCert.relTenantId.eq(relTenantId))
                .fetchOne();
        if (tenantCert == null) {
            return Resp.badRequest("凭证不存在或已禁用");
        }
        var validRule = tenantCert.get(0, String.class);
        var validTimeSec = tenantCert.get(1, Long.class);
        if (!StringUtils.isEmpty(validRule)) {
            if (!VALID_RULES.containsKey(validRule)) {
                VALID_RULES.put(validRule, Pattern.compile(validRule));
            }
            if (!VALID_RULES.get(validRule).matcher(sk).matches()) {
                return Resp.badRequest("凭证密钥规则不合法");
            }
        }
        return Resp.success(validTimeSec == null || validTimeSec.equals(Constant.OBJECT_UNDEFINED)
                ? Constant.NEVER_EXPIRE_TIME
                : new Date(System.currentTimeMillis() + validTimeSec * 1000));
    }

    public Resp<TenantCert> getTenantCert(AccountCertKind kind, Long relTenantId) {
        var qTenantCert = QTenantCert.tenantCert;
        return getDTO(sqlBuilder
                .selectFrom(qTenantCert)
                .where(qTenantCert.relTenantId.eq(relTenantId))
                .where(qTenantCert.status.eq(CommonStatus.ENABLED))
                .where(qTenantCert.kind.eq(kind)));
    }

}
