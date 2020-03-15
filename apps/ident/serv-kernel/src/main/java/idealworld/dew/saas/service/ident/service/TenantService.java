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
import idealworld.dew.saas.service.ident.domain.QAccount;
import idealworld.dew.saas.service.ident.domain.QApp;
import idealworld.dew.saas.service.ident.domain.QTenant;
import idealworld.dew.saas.service.ident.domain.Tenant;
import idealworld.dew.saas.service.ident.dto.IdentOptInfo;
import idealworld.dew.saas.service.ident.dto.account.AddAccountCertReq;
import idealworld.dew.saas.service.ident.dto.account.AddAccountPostReq;
import idealworld.dew.saas.service.ident.dto.account.AddAccountReq;
import idealworld.dew.saas.service.ident.dto.account.LoginReq;
import idealworld.dew.saas.service.ident.dto.tenant.ModifyTenantReq;
import idealworld.dew.saas.service.ident.dto.tenant.RegisterTenantReq;
import idealworld.dew.saas.service.ident.dto.tenant.TenantInfoResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author gudaoxuri
 */
@Service
public class TenantService extends BasicService {

    @Autowired
    private AccountService accountService;
    @Autowired
    private AppService appService;
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
                .build(), -1L);
        if (!addAccountR.ok()) {
            return Resp.error(addAccountR);
        }
        var tenant = Tenant.builder()
                .name(registerTenantReq.getTenantName())
                .icon("")
                .createUser(addAccountR.getBody())
                .updateUser(addAccountR.getBody())
                .build();
        saveEntity(tenant);
        var qAccount = QAccount.account;
        updateEntity(sqlBuilder
                .update(qAccount)
                .set(qAccount.relTenantId, tenant.getId())
                .where(qAccount.id.eq(addAccountR.getBody()))
        );
        // Auto login
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
                        qTenant.delFlag,
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
                .set(qTenant.name, modifyTenantReq.getTenantName());
        if (modifyTenantReq.getTenantIcon() != null) {
            updateClause.set(qTenant.icon, modifyTenantReq.getTenantIcon());
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
        if (!deleteR.ok()) {
            return deleteR;
        }
        accountService.deleteAccounts(tenantId);
        return deleteR;
    }

}
