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
import idealworld.dew.saas.service.ident.Constant;
import idealworld.dew.saas.service.ident.domain.Organization;
import idealworld.dew.saas.service.ident.domain.QAccount;
import idealworld.dew.saas.service.ident.domain.QOrganization;
import idealworld.dew.saas.service.ident.dto.organization.AddOrganizationReq;
import idealworld.dew.saas.service.ident.dto.organization.ModifyOrganizationReq;
import idealworld.dew.saas.service.ident.dto.organization.OrganizationInfoResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author gudaoxuri
 */
@Service
public class OrganizationService extends BasicService {

    @Autowired
    private AppService appService;

    @Transactional
    public Resp<Long> AddOrganization(AddOrganizationReq addOrganizationReq, Long relAppId, Long relTenantId) {
        if (!appService.checkAppMembership(relAppId, relTenantId)) {
            return Constant.RESP.NOT_FOUNT();
        }
        var appCert = Organization.builder()
                .kind(addOrganizationReq.getKind())
                .name(addOrganizationReq.getName())
                .icon(addOrganizationReq.getIcon() != null ? addOrganizationReq.getIcon() : "")
                .sort(addOrganizationReq.getSort() != null ? addOrganizationReq.getSort() : 0)
                .parentId(addOrganizationReq.getParentId() != null ? addOrganizationReq.getParentId() : -1L)
                .relAppId(relAppId)
                .relTenantId(relTenantId)
                .build();
        return saveEntity(appCert);
    }

    public Resp<List<OrganizationInfoResp>> findOrganizationInfo(Long relAppId, Long relTenantId) {
        var qOrganization = QOrganization.organization;
        var qAccountCreateUser = QAccount.account;
        var qAccountUpdateUser = QAccount.account;
        var OrganizationQuery = sqlBuilder
                .select(Projections.bean(
                        OrganizationInfoResp.class,
                        qOrganization.id,
                        qOrganization.kind,
                        qOrganization.name,
                        qOrganization.icon,
                        qOrganization.sort,
                        qOrganization.parentId,
                        qOrganization.relAppId,
                        qOrganization.relTenantId,
                        qOrganization.createTime,
                        qOrganization.updateTime,
                        qAccountCreateUser.name.as("createUserName"),
                        qAccountUpdateUser.name.as("updateUserName")))
                .from(qOrganization)
                .leftJoin(qAccountCreateUser).on(qOrganization.createUser.eq(qAccountCreateUser.id))
                .leftJoin(qAccountUpdateUser).on(qOrganization.updateUser.eq(qAccountUpdateUser.id))
                .where(qOrganization.relAppId.eq(relAppId))
                .where(qOrganization.relTenantId.eq(relTenantId));
        return findDTOs(OrganizationQuery);
    }

    @Transactional
    public Resp<Void> modifyOrganization(ModifyOrganizationReq modifyOrganizationReq, Long organizationId,
                                         Long relAppId, Long relTenantId) {
        var qOrganization = QOrganization.organization;
        var updateClause = sqlBuilder.update(qOrganization)
                .where(qOrganization.id.eq(organizationId))
                .where(qOrganization.relAppId.eq(relAppId))
                .where(qOrganization.relTenantId.eq(relTenantId));
        if (modifyOrganizationReq.getKind() != null) {
            updateClause.set(qOrganization.kind, modifyOrganizationReq.getKind());
        }
        if (modifyOrganizationReq.getName() != null) {
            updateClause.set(qOrganization.name, modifyOrganizationReq.getName());
        }
        if (modifyOrganizationReq.getIcon() != null) {
            updateClause.set(qOrganization.icon, modifyOrganizationReq.getIcon());
        }
        if (modifyOrganizationReq.getSort() != null) {
            updateClause.set(qOrganization.sort, modifyOrganizationReq.getSort());
        }
        if (modifyOrganizationReq.getParentId() != null) {
            updateClause.set(qOrganization.parentId, modifyOrganizationReq.getParentId());
        }
        return updateEntity(updateClause);
    }

    @Transactional
    public Resp<Void> deleteOrganization(Long organizationId, Long relAppId, Long relTenantId) {
        // TODO 级联删除机构、岗位、用户岗位关联、权限
        var qOrganization = QOrganization.organization;
        return deleteEntity(sqlBuilder
                .delete(qOrganization)
                .where(qOrganization.id.eq(organizationId))
                .where(qOrganization.relAppId.eq(relAppId))
                .where(qOrganization.relTenantId.eq(relTenantId))
        );
    }

}