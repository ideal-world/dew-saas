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
import group.idealworld.dew.saas.service.ident.dto.ModifyTenantReq;
import group.idealworld.dew.saas.service.ident.dto.RegisterTenantReq;
import group.idealworld.dew.saas.service.ident.dto.TenantInfoResp;
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

    @Transactional
    public Resp<Long> registerTenant(RegisterTenantReq request) {
        var validateSk = accountService.validateSk(request.getCertKind(), request.getAk(), request.getSk());
        if (!validateSk.ok()) {
            return Resp.error(validateSk);
        }
        var account = Account.builder()
                .name(request.getAccountName())
                .status(Account.Status.DISABLED)
                .relTenantId(-1L)
                .createUser(-1L)
                .updateUser(-1L)
                .build();
        entityManager.persist(account);
        var tenant = Tenant.builder()
                .name(request.getTenantName())
                .createUser(account.getId())
                .updateUser(account.getId())
                .build();
        entityManager.persist(tenant);
        var qAccount = QAccount.account;
        queryFactory.update(qAccount)
                .set(qAccount.relTenantId, tenant.getId())
                .set(qAccount.status, Account.Status.ENABLED)
                .set(qAccount.createUser, account.getId())
                .set(qAccount.updateUser, account.getId())
                .execute();
        var certAccount = CertAccount.builder()
                .kind(request.getCertKind())
                .ak(request.getAk())
                // TODO 加密存储
                .sk(request.getSk())
                .relAccountId(account.getId())
                .createUser(account.getId())
                .updateUser(account.getId())
                .build();
        entityManager.persist(certAccount);
        return Resp.success(tenant.getId());
    }

    public Resp<TenantInfoResp> getTenantInfo(Long tenantId) {
        QTenant qTenant = QTenant.tenant;
        var tenantInfo = queryFactory
                .select(Projections.constructor(TenantInfoResp.class, qTenant.id, qTenant.name, qTenant.icon))
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
    public Resp<Void> modifyTenant(Long tenantId, ModifyTenantReq request) {
        QTenant qTenant = QTenant.tenant;
        var tenant = queryFactory
                .selectFrom(qTenant)
                .where(qTenant.id.eq(tenantId))
                .fetchOne();
        if (tenant == null) {
            return Resp.badRequest("租户 [" + tenantId + "] 不存在");
        }
        tenant.setName(request.getTenantName());
        tenant.setIcon(request.getTenantIcon());
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
