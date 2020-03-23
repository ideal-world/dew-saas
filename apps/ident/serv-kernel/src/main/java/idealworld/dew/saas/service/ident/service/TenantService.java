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

import com.ecfront.dew.common.Resp;
import com.querydsl.core.types.Projections;
import idealworld.dew.saas.common.Constant;
import idealworld.dew.saas.common.service.dto.IdentOptInfo;
import idealworld.dew.saas.service.ident.domain.*;
import idealworld.dew.saas.service.ident.dto.account.AddAccountCertReq;
import idealworld.dew.saas.service.ident.dto.account.AddAccountPostReq;
import idealworld.dew.saas.service.ident.dto.account.AddAccountReq;
import idealworld.dew.saas.service.ident.dto.account.LoginReq;
import idealworld.dew.saas.service.ident.dto.tenant.*;
import idealworld.dew.saas.service.ident.enumeration.AccountCertKind;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * @author gudaoxuri
 */
@Service
public class TenantService extends BasicService {

    private static final Map<String, Pattern> VALID_RULES = new ConcurrentHashMap<>();

    @Autowired
    private AccountService accountService;
    @Autowired
    private PostService postService;

    @Transactional
    public Resp<IdentOptInfo> registerTenant(RegisterTenantReq registerTenantReq) {
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
                .createUser(addAccountR.getBody())
                .updateUser(addAccountR.getBody())
                .build();
        saveEntity(tenant);
        accountService.updateAccountTenant(addAccountR.getBody(), tenant.getId());
        addTenantCertConfig(AddTenantCertConfigReq.builder()
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
                        qTenant.delFlag,
                        qTenant.createTime,
                        qTenant.updateTime,
                        qAccountCreateUser.name.as("createUserName"),
                        qAccountUpdateUser.name.as("updateUserName")))
                .from(qTenant)
                .leftJoin(qAccountCreateUser).on(qTenant.createUser.eq(qAccountCreateUser.id))
                .leftJoin(qAccountUpdateUser).on(qTenant.updateUser.eq(qAccountUpdateUser.id))
                .where(qTenant.id.eq(tenantId))
                .where(qTenant.delFlag.eq(false));
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
        return updateEntity(updateClause);
    }

    @Transactional
    public Resp<Void> unRegisterTenant(Long tenantId) {
        var qApp = QApp.app;
        var count = sqlBuilder
                .selectFrom(qApp)
                .where(qApp.relTenantId.eq(tenantId))
                .where(qApp.delFlag.eq(false))
                .fetchCount();
        if (count != 0) {
            return Resp.conflict("请先删除租户下的所有应用");
        }
        var qTenant = QTenant.tenant;
        var deleteR = updateEntity(sqlBuilder
                .update(qTenant)
                .set(qTenant.delFlag, true)
                .where(qTenant.id.eq(tenantId))
        );
        deleteTenantCertConfig(tenantId);
        return deleteR;
    }

    // ========================== Cert ==============================

    @Transactional
    public Resp<Long> addTenantCertConfig(AddTenantCertConfigReq addTenantCertConfigReq,
                                          Long relTenantId) {
        var qTenantCertConfig = QTenantCertConfig.tenantCertConfig;
        if (sqlBuilder.select(qTenantCertConfig.id)
                .from(qTenantCertConfig)
                .where(qTenantCertConfig.delFlag.eq(false))
                .where(qTenantCertConfig.relTenantId.eq(relTenantId))
                .where(qTenantCertConfig.kind.eq(addTenantCertConfigReq.getKind()))
                .fetchCount() != 0) {
            return Resp.conflict("此凭证配置已存在");
        }
        var tenantCertConfig = TenantCertConfig.builder()
                .kind(addTenantCertConfigReq.getKind())
                .validRuleNote(addTenantCertConfigReq.getValidRuleNote() != null ? addTenantCertConfigReq.getValidRuleNote() : "")
                .validRule(addTenantCertConfigReq.getValidRule() != null ? addTenantCertConfigReq.getValidRule() : "")
                .validTimeSec(addTenantCertConfigReq.getValidTimeSec() != null
                        ? addTenantCertConfigReq.getValidTimeSec() : Constant.OBJECT_UNDEFINED)
                .oauthAk(addTenantCertConfigReq.getOauthAk() != null ? addTenantCertConfigReq.getOauthAk() : "")
                .oauthSk(addTenantCertConfigReq.getOauthSk() != null ? addTenantCertConfigReq.getOauthSk() : "")
                .relTenantId(relTenantId)
                .build();
        return saveEntity(tenantCertConfig);
    }

    public Resp<List<TenantCertConfigInfoResp>> findTenantCertConfigInfo(Long relTenantId) {
        var qTenantCertConfig = QTenantCertConfig.tenantCertConfig;
        var qAccountCreateUser = QAccount.account;
        var qAccountUpdateUser = QAccount.account;
        var query = sqlBuilder
                .select(Projections.bean(
                        TenantCertConfigInfoResp.class,
                        qTenantCertConfig.id,
                        qTenantCertConfig.kind,
                        qTenantCertConfig.validRuleNote,
                        qTenantCertConfig.validRule,
                        qTenantCertConfig.validTimeSec,
                        qTenantCertConfig.oauthAk,
                        qTenantCertConfig.oauthSk,
                        qTenantCertConfig.delFlag,
                        qTenantCertConfig.createTime,
                        qTenantCertConfig.updateTime,
                        qAccountCreateUser.name.as("createUserName"),
                        qAccountUpdateUser.name.as("updateUserName")))
                .from(qTenantCertConfig)
                .leftJoin(qAccountCreateUser).on(qTenantCertConfig.createUser.eq(qAccountCreateUser.id))
                .leftJoin(qAccountUpdateUser).on(qTenantCertConfig.updateUser.eq(qAccountUpdateUser.id))
                .where(qTenantCertConfig.delFlag.eq(false));
        return findDTOs(query);
    }

    @Transactional
    public Resp<Void> modifyTenantCertConfig(ModifyTenantCertConfigReq modifyTenantCertConfigReq, Long tenantCertConfigId,
                                             Long relTenantId) {
        var qTenantCertConfig = QTenantCertConfig.tenantCertConfig;
        var updateClause = sqlBuilder.update(qTenantCertConfig)
                .where(qTenantCertConfig.id.eq(tenantCertConfigId))
                .where(qTenantCertConfig.relTenantId.eq(relTenantId));
        if (modifyTenantCertConfigReq.getValidRule() != null) {
            updateClause.set(qTenantCertConfig.validRule, modifyTenantCertConfigReq.getValidRule());
        }
        if (modifyTenantCertConfigReq.getValidRuleNote() != null) {
            updateClause.set(qTenantCertConfig.validRuleNote, modifyTenantCertConfigReq.getValidRuleNote());
        }
        if (modifyTenantCertConfigReq.getValidTimeSec() != null) {
            updateClause.set(qTenantCertConfig.validTimeSec, modifyTenantCertConfigReq.getValidTimeSec());
        }
        if (modifyTenantCertConfigReq.getOauthAk() != null) {
            updateClause.set(qTenantCertConfig.oauthAk, modifyTenantCertConfigReq.getOauthAk());
        }
        if (modifyTenantCertConfigReq.getOauthSk() != null) {
            updateClause.set(qTenantCertConfig.oauthSk, modifyTenantCertConfigReq.getOauthSk());
        }
        return updateEntity(updateClause);
    }

    @Transactional
    public Resp<Void> deleteTenantCertConfig(Long relTenantId) {
        var qTenantCertConfig = QTenantCertConfig.tenantCertConfig;
        sqlBuilder
                .update(qTenantCertConfig)
                .set(qTenantCertConfig.delFlag, true)
                .where(qTenantCertConfig.relTenantId.eq(relTenantId))
                .execute();
        return Resp.success(null);
    }

    @Transactional
    public Resp<Void> deleteTenantCertConfig(Long tenantCertConfigId, Long relTenantId) {
        var qTenantCertConfig = QTenantCertConfig.tenantCertConfig;
        sqlBuilder
                .update(qTenantCertConfig)
                .set(qTenantCertConfig.delFlag, true)
                .where(qTenantCertConfig.id.eq(tenantCertConfigId))
                .where(qTenantCertConfig.relTenantId.eq(relTenantId))
                .execute();
        return Resp.success(null);
    }

    protected Resp<Date> checkValidRuleAndReturnValidTime(AccountCertKind kind, String sk, Long relTenantId) {
        if (relTenantId.equals(Constant.OBJECT_UNDEFINED)) {
            // 表示租户管理员注册时临时分配的虚拟租户号
            return Resp.success(Constant.NEVER_EXPIRE_TIME);
        }
        var qTenantCertConfig = QTenantCertConfig.tenantCertConfig;
        var tenantCertConfig = sqlBuilder
                .select(qTenantCertConfig.validRule,
                        qTenantCertConfig.validTimeSec)
                .from(qTenantCertConfig)
                .where(qTenantCertConfig.delFlag.eq(false))
                .where(qTenantCertConfig.kind.eq(kind))
                .where(qTenantCertConfig.relTenantId.eq(relTenantId))
                .fetchOne();
        if (tenantCertConfig == null) {
            return Resp.badRequest("凭证不存在");
        }
        var validRule = tenantCertConfig.get(0, String.class);
        var validTimeSec = tenantCertConfig.get(1, Long.class);
        if (validRule != null) {
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

    public Resp<TenantCertConfig> getTenantCertConfig(AccountCertKind kind, Long relTenantId) {
        var qTenantCertConfig = QTenantCertConfig.tenantCertConfig;
        return getDTO(sqlBuilder
                .selectFrom(qTenantCertConfig)
                .where(qTenantCertConfig.delFlag.eq(false))
                .where(qTenantCertConfig.relTenantId.eq(relTenantId))
                .where(qTenantCertConfig.kind.eq(kind)));
    }

}
