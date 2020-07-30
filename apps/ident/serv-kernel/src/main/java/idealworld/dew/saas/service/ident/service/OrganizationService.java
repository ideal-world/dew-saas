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
import com.ecfront.dew.common.tuple.Tuple2;
import com.querydsl.core.types.Projections;
import idealworld.dew.saas.common.Constant;
import idealworld.dew.saas.common.resp.StandardResp;
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
import java.util.stream.Collectors;

/**
 * Organization service.
 *
 * @author gudaoxuri
 */
@Service
public class OrganizationService extends IdentBasicService {

    private static final String BUSINESS_ORG = "ORG";

    @Autowired
    private AppService appService;
    @Autowired
    private PostService postService;

    /**
     * Add organization.
     *
     * @param addOrganizationReq the add organization req
     * @param relAppId           the rel app id
     * @param relTenantId        the rel tenant id
     * @return the resp
     */
    @Transactional
    public Resp<Long> AddOrganization(AddOrganizationReq addOrganizationReq, Long relAppId, Long relTenantId) {
        var membershipCheckR = appService.checkAppMembership(relAppId, relTenantId);
        if (!membershipCheckR.ok()) {
            return StandardResp.error(membershipCheckR);
        }
        var qOrganization = QOrganization.organization;
        if (sqlBuilder.select(qOrganization.id)
                .from(qOrganization)
                .where(qOrganization.relTenantId.eq(relTenantId))
                .where(qOrganization.relAppId.eq(relAppId))
                .where(qOrganization.code.eq(addOrganizationReq.getCode()))
                .fetchCount() != 0) {
            return StandardResp.conflict(BUSINESS_ORG, "此机构编码已存在");
        }
        var appIdent = Organization.builder()
                .kind(addOrganizationReq.getKind())
                .code(addOrganizationReq.getCode())
                .busCode(addOrganizationReq.getBusCode() != null ? addOrganizationReq.getBusCode() : "")
                .name(addOrganizationReq.getName())
                .icon(addOrganizationReq.getIcon() != null ? addOrganizationReq.getIcon() : "")
                .parameters(addOrganizationReq.getParameters() != null ? addOrganizationReq.getParameters() : "{}")
                .sort(addOrganizationReq.getSort() != null ? addOrganizationReq.getSort() : 0)
                .parentId(addOrganizationReq.getParentId() != null ? addOrganizationReq.getParentId() : Constant.OBJECT_UNDEFINED)
                .relAppId(relAppId)
                .relTenantId(relTenantId)
                .build();
        return saveEntity(appIdent);
    }

    /**
     * Find organization info.
     *
     * @param relAppId    the rel app id
     * @param relTenantId the rel tenant id
     * @return the resp
     */
    public Resp<List<OrganizationInfoResp>> findOrganizationInfo(Long relAppId, Long relTenantId) {
        var qOrganization = QOrganization.organization;
        var qAccountCreateUser = new QAccount("createUser");
        var qAccountUpdateUser = new QAccount("updateUser");
        var qOrganizationQuery = sqlBuilder
                .select(Projections.bean(
                        OrganizationInfoResp.class,
                        qOrganization.id,
                        qOrganization.code,
                        qOrganization.busCode,
                        qOrganization.kind,
                        qOrganization.name,
                        qOrganization.icon,
                        qOrganization.parameters,
                        qOrganization.sort,
                        qOrganization.parentId,
                        qOrganization.relAppId,
                        qOrganization.relTenantId,
                        qOrganization.createTime,
                        qOrganization.updateTime,
                        qAccountCreateUser.name.as("createUserName"),
                        qAccountUpdateUser.name.as("updateUserName")))
                .from(qOrganization)
                .leftJoin(qAccountCreateUser).on(qOrganization.createUser.eq(qAccountCreateUser.openId))
                .leftJoin(qAccountUpdateUser).on(qOrganization.updateUser.eq(qAccountUpdateUser.openId))
                .where(qOrganization.relAppId.eq(relAppId))
                .where(qOrganization.relTenantId.eq(relTenantId));
        return findDTOs(qOrganizationQuery);
    }

    /**
     * Modify organization.
     *
     * @param modifyOrganizationReq the modify organization req
     * @param organizationId        the organization id
     * @param relAppId              the rel app id
     * @param relTenantId           the rel tenant id
     * @return the resp
     */
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
        if (modifyOrganizationReq.getBusCode() != null) {
            updateClause.set(qOrganization.busCode, modifyOrganizationReq.getBusCode());
        }
        if (modifyOrganizationReq.getName() != null) {
            updateClause.set(qOrganization.name, modifyOrganizationReq.getName());
        }
        if (modifyOrganizationReq.getIcon() != null) {
            updateClause.set(qOrganization.icon, modifyOrganizationReq.getIcon());
        }
        if (modifyOrganizationReq.getParameters() != null) {
            updateClause.set(qOrganization.parameters, modifyOrganizationReq.getParameters());
        }
        if (modifyOrganizationReq.getSort() != null) {
            updateClause.set(qOrganization.sort, modifyOrganizationReq.getSort());
        }
        if (modifyOrganizationReq.getParentId() != null) {
            updateClause.set(qOrganization.parentId, modifyOrganizationReq.getParentId());
        }
        return updateEntity(updateClause);
    }

    /**
     * Delete organization.
     *
     * @param organizationId the organization id
     * @param relAppId       the rel app id
     * @param relTenantId    the rel tenant id
     * @return the resp
     */
    @Transactional
    public Resp<Long> deleteOrganization(Long organizationId, Long relAppId, Long relTenantId) {
        var qOrganization = QOrganization.organization;
        var getOrganizationCodeR = getDTO(sqlBuilder.select(qOrganization.code)
                .from(qOrganization)
                .where(qOrganization.id.eq(organizationId)));
        if (!getOrganizationCodeR.ok()) {
            // 已经被删除
            return StandardResp.error(getOrganizationCodeR);
        }
        // 级联删除机构
        var deleteOrgInfos = findOrganizationIds(organizationId);
        deleteOrgInfos.add(new Tuple2<>(organizationId, getOrganizationCodeR.getBody()));
        // 删除岗位、账号岗位、权限
        postService.deletePostByOrgCodes(deleteOrgInfos
                .stream()
                .map(orgInfoTuple -> orgInfoTuple._1)
                .collect(Collectors.toList()), relAppId, relTenantId);
        return deleteEntities(sqlBuilder
                .delete(qOrganization)
                .where(qOrganization.id.in(deleteOrgInfos
                        .stream()
                        .map(orgInfoTuple -> orgInfoTuple._0)
                        .collect(Collectors.toList())))
                .where(qOrganization.relAppId.eq(relAppId))
                .where(qOrganization.relTenantId.eq(relTenantId)));
    }

    /**
     * Delete organization.
     *
     * @param appId       the app id
     * @param relTenantId the rel tenant id
     * @return the resp
     */
    @Transactional
    protected Resp<Long> deleteOrganization(Long appId, Long relTenantId) {
        var qOrganization = QOrganization.organization;
        // 删除岗位、账号岗位、权限
        postService.deletePost(appId, relTenantId);
        // 删除机构
        return deleteEntities(sqlBuilder
                .delete(qOrganization)
                .where(qOrganization.relAppId.eq(appId))
                .where(qOrganization.relTenantId.eq(relTenantId)));
    }

    private List<Tuple2<Long, String>> findOrganizationIds(Long parentOrgId) {
        var qOrganization = QOrganization.organization;
        return sqlBuilder.select(qOrganization.id, qOrganization.code)
                .from(qOrganization)
                .where(qOrganization.parentId.eq(parentOrgId))
                .fetch()
                .stream()
                .map(orgInfo -> new Tuple2<>(orgInfo.get(0, Long.class), orgInfo.get(1, String.class)))
                .flatMap(orgInfoTuple -> findOrganizationIds(orgInfoTuple._0).stream())
                .collect(Collectors.toList());
    }

}
