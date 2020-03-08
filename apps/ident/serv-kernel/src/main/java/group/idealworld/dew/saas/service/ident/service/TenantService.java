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

package group.idealworld.dew.saas.service.ident.service;

import com.ecfront.dew.common.Resp;
import com.querydsl.core.types.Projections;
import group.idealworld.dew.saas.service.ident.domain.*;
import group.idealworld.dew.saas.service.ident.dto.tenant.ModifyTenantReq;
import group.idealworld.dew.saas.service.ident.dto.tenant.RegisterTenantReq;
import group.idealworld.dew.saas.service.ident.dto.tenant.TenantInfoResp;
import group.idealworld.dew.saas.service.ident.dto.account.AddAccountCertReq;
import group.idealworld.dew.saas.service.ident.dto.account.AddAccountPostReq;
import group.idealworld.dew.saas.service.ident.dto.account.AddAccountReq;
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
    private PostService postService;

    @Transactional
    public Resp<Long> registerTenant(RegisterTenantReq registerTenantReq) {
        var getTenantPostIdR = postService.getTenantPostId();
        if(!getTenantPostIdR.ok()){
            return Resp.error(getTenantPostIdR);
        }
        var addAccountR = accountService.addAccount(AddAccountReq.builder()
                .name(registerTenantReq.getAccountName())
                .certReq(AddAccountCertReq.builder()
                        .kind(registerTenantReq.getCertKind())
                        .ak(registerTenantReq.getAk())
                        .sk(registerTenantReq.getSk())
                        .build())
                .postReq(AddAccountPostReq.builder()
                        .relPostId(getTenantPostIdR.getBody())
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
        entityManager.persist(tenant);
        var qAccount = QAccount.account;
        queryFactory.update(qAccount)
                .set(qAccount.relTenantId, tenant.getId())
                .where(qAccount.id.eq(addAccountR.getBody()))
                .execute();
        return Resp.success(tenant.getId());
    }

    public Resp<TenantInfoResp> getTenantInfo(Long tenantId) {
        QTenant qTenant = QTenant.tenant;
        var tenantInfo = queryFactory
                .select(Projections.bean(TenantInfoResp.class, qTenant.id, qTenant.name, qTenant.icon))
                .from(qTenant)
                .where(qTenant.id.eq(tenantId))
                .fetchOne();
        if (tenantInfo == null) {
            return Resp.badRequest("租户 [" + tenantId + "] 不存在");
        } else {
            return Resp.success(tenantInfo);
        }
    }

    @Transactional
    public Resp<Void> modifyTenant(ModifyTenantReq modifyTenantReq,Long tenantId) {
        QTenant qTenant = QTenant.tenant;
        var tenant = queryFactory
                .selectFrom(qTenant)
                .where(qTenant.id.eq(tenantId))
                .fetchOne();
        if (tenant == null) {
            return Resp.badRequest("租户 [" + tenantId + "] 不存在");
        }
        tenant.setName(modifyTenantReq.getTenantName());
        tenant.setIcon(modifyTenantReq.getTenantIcon());
        entityManager.persist(tenant);
        return Resp.success(null);
    }

    @Transactional
    public Resp<Void> unRegisterTenant(Long tenantId) {
        QApp qApp = QApp.app;
        var onlineApps = queryFactory
                .selectFrom(qApp)
                .where(qApp.relTenantId.eq(tenantId))
                .where(qApp.delFlag.eq(false))
                .fetchCount();
        if (onlineApps != 0) {
            return Resp.conflict("请先删除租户下的所有应用");
        }
        delete(Tenant.class, tenantId);
        return Resp.success(null);
    }

}
