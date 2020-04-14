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
import group.idealworld.dew.Dew;
import idealworld.dew.saas.common.Constant;
import idealworld.dew.saas.common.resp.StandardResp;
import idealworld.dew.saas.service.ident.domain.Permission;
import idealworld.dew.saas.service.ident.domain.QPermission;
import idealworld.dew.saas.service.ident.domain.QPost;
import idealworld.dew.saas.service.ident.domain.QResource;
import idealworld.dew.saas.service.ident.dto.permission.AddPermissionReq;
import idealworld.dew.saas.service.ident.dto.permission.PermissionExtInfo;
import idealworld.dew.saas.service.ident.dto.permission.PermissionInfoResp;
import idealworld.dew.saas.service.ident.dto.permission.PermissionInfoSub;
import idealworld.dew.saas.service.ident.enumeration.ResourceKind;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author gudaoxuri
 */
@Service
public class PermissionService extends IdentBasicService {

    public static final String IDENT_PERMISSION_SUB_LIST = "ident:cache:permission:app:";
    public static final String IDENT_PERMISSION_SUB_APPS = "ident:mq:permission:app:";

    private static final int HEARTBEAT_DELAY_FIX = 60;
    private static final String BUSINESS_PERMISSION = "PERMISSION";

    @Autowired
    private ResourceService resourceService;
    @Autowired
    private AppService appService;

    @Transactional
    public Resp<Long> addPermission(AddPermissionReq addPermissionReq, Long relAppId, Long relTenantId) {
        var membershipCheckR = appService.checkAppMembership(relAppId, relTenantId);
        if (!membershipCheckR.ok()) {
            return StandardResp.error(membershipCheckR);
        }
        var qPermission = QPermission.permission;
        if (sqlBuilder.select(qPermission.id)
                .from(qPermission)
                .where(qPermission.relPostId.eq(addPermissionReq.getRelPostId()))
                .where(qPermission.relResourceId.eq(addPermissionReq.getRelResourceId()))
                .fetchCount() != 0) {
            return StandardResp.conflict(BUSINESS_PERMISSION,"此权限已存在");
        }
        var permission = Permission.builder()
                .relPostId(addPermissionReq.getRelPostId())
                .relResourceId(addPermissionReq.getRelResourceId())
                .relAppId(relAppId)
                .relTenantId(relTenantId)
                .build();
        var saveR = saveEntity(permission);
        if (saveR.ok()) {
            onPermissionChanged(new ArrayList<>() {{
                add(permission.getId());
            }}, relAppId, false);
        }
        return saveR;
    }

    public Resp<List<PermissionInfoResp>> findPermissionInfo(Long relAppId, Long relTenantId) {
        var qPermission = QPermission.permission;
        var positionQuery = sqlBuilder
                .select(Projections.bean(PermissionInfoResp.class,
                        qPermission.id,
                        qPermission.relPostId,
                        qPermission.relResourceId,
                        qPermission.relAppId))
                .from(qPermission)
                .where(qPermission.relAppId.eq(relAppId))
                .where(qPermission.relTenantId.eq(relTenantId));
        return findDTOs(positionQuery);
    }

    @Transactional
    public Resp<Void> deletePermission(Long permissionId, Long relAppId, Long relTenantId) {
        var qPermission = QPermission.permission;
        sqlBuilder.delete(qPermission)
                .where(qPermission.id.eq(permissionId))
                .where(qPermission.relAppId.eq(relAppId))
                .where(qPermission.relTenantId.eq(relTenantId))
                .execute();
        onPermissionChanged(new ArrayList<>() {{
            add(permissionId);
        }}, relAppId, true);
        return StandardResp.success(null);
    }

    @Transactional
    protected Resp<Void> deletePermissionByPostIds(List<Long> postIds, Long relAppId, Long relTenantId) {
        var qPermission = QPermission.permission;
        var deletePermissionIds = sqlBuilder.select(qPermission.id)
                .from(qPermission)
                .where(qPermission.relPostId.in(postIds))
                .fetch();
        sqlBuilder
                .delete(qPermission)
                .where(qPermission.id.in(deletePermissionIds))
                .where(qPermission.relAppId.eq(relAppId))
                .where(qPermission.relTenantId.eq(relTenantId))
                .execute();
        onPermissionChanged(deletePermissionIds, relAppId, true);
        return StandardResp.success(null);
    }

    @Transactional
    protected Resp<Void> deletePermissionByResourceIds(List<Long> resourceIds, Long relAppId, Long relTenantId) {
        if (resourceIds.isEmpty()) {
            return StandardResp.notFound(BUSINESS_PERMISSION,"没有要删除的资源");
        }
        var qPermission = QPermission.permission;
        var deletePermissionIds = sqlBuilder.select(qPermission.id)
                .from(qPermission)
                .where(qPermission.relResourceId.in(resourceIds))
                .fetch();
        if (deletePermissionIds.isEmpty()) {
            return StandardResp.success(null);
        }
        sqlBuilder
                .update(qPermission)
                .where(qPermission.id.in(deletePermissionIds))
                .where(qPermission.relAppId.eq(relAppId))
                .where(qPermission.relTenantId.eq(relTenantId))
                .execute();
        onPermissionChanged(deletePermissionIds, relAppId, true);
        return StandardResp.success(null);
    }

    public Resp<String> subPermissions(Long appId, Integer heartbeatPeriodSec) {
        Dew.cluster.cache.setex(IDENT_PERMISSION_SUB_LIST + appId, IDENT_PERMISSION_SUB_APPS + appId,
                heartbeatPeriodSec + HEARTBEAT_DELAY_FIX);
        var permissionExtInfo = findPermissionExtInfo(appId, Optional.empty());
        var permissionInfoSub = PermissionInfoSub.builder()
                .changedPermissions(permissionExtInfo)
                .build();
        Dew.cluster.mq.publish(IDENT_PERMISSION_SUB_APPS + appId,
                $.json.toJsonString(permissionInfoSub));
        return StandardResp.success(IDENT_PERMISSION_SUB_APPS + appId);
    }

    public Resp<Void> subHeartbeat(Long appId, Integer periodSec) {
        Dew.cluster.cache.setex(IDENT_PERMISSION_SUB_LIST + appId, IDENT_PERMISSION_SUB_APPS + appId,
                periodSec + HEARTBEAT_DELAY_FIX);
        return StandardResp.success(null);
    }

    @Transactional
    public Resp<Void> unSubPermission(Long appId) {
        Dew.cluster.cache.del(IDENT_PERMISSION_SUB_LIST + appId);
        Dew.cluster.cache.del(IDENT_PERMISSION_SUB_APPS + appId);
        return StandardResp.success(null);
    }

    private void onPermissionChanged(List<Long> changedPermissionIds, Long relAppId, Boolean isDel) {
        if (!ELECTION.isLeader()) {
            return;
        }
        if (!Dew.cluster.cache.exists(IDENT_PERMISSION_SUB_LIST + relAppId)) {
            return;
        }
        if (isDel) {
            var permissionInfoSub = PermissionInfoSub.builder()
                    .removedPermissionIds(changedPermissionIds)
                    .build();
            Dew.cluster.mq.publish(IDENT_PERMISSION_SUB_APPS + relAppId,
                    $.json.toJsonString(permissionInfoSub));
        } else {
            var permissionExtInfo = findPermissionExtInfo(relAppId, Optional.of(changedPermissionIds));
            var permissionInfoSub = PermissionInfoSub.builder()
                    .changedPermissions(permissionExtInfo)
                    .build();
            Dew.cluster.mq.publish(IDENT_PERMISSION_SUB_APPS + relAppId,
                    $.json.toJsonString(permissionInfoSub));
        }
    }

    private List<PermissionExtInfo> findPermissionExtInfo(Long relAppId,
                                                          Optional<List<Long>> permissionIdsOpt) {
        var qResource = QResource.resource;
        var qPost = QPost.post;
        var qPermission = QPermission.permission;
        var permissionQuery = sqlBuilder
                .select(Projections.bean(
                        PermissionExtInfo.class,
                        qPermission.id.as("permissionId"),
                        qResource.kind.as("resKind"),
                        qResource.id.as("resId"),
                        qResource.identifier.as("resIdentifier"),
                        qResource.method.as("resMethod"),
                        qPost.relPositionCode.as("positionCode"),
                        qPost.relOrganizationCode.as("organizationCode"),
                        qPost.relAppId))
                .from(qPermission)
                .innerJoin(qPost).on(qPermission.relPostId.eq(qPost.id))
                .innerJoin(qResource).on(qPermission.relResourceId.eq(qResource.id));
        permissionIdsOpt.ifPresent(ids -> permissionQuery.where(qPermission.id.in(ids)));
        return permissionQuery.where(qPermission.relAppId.eq(relAppId))
                .fetch()
                .stream()
                .flatMap(info -> {
                    if (info.getResKind() == ResourceKind.GROUP) {
                        return resourceService.findResourceByGroup(info.getResId())
                                .stream()
                                .map(resInfo -> PermissionExtInfo.builder()
                                        .permissionId(info.getPermissionId())
                                        .resKind(resInfo.getKind())
                                        .resId(resInfo.getId())
                                        .resIdentifier(resInfo.getIdentifier())
                                        .resMethod(resInfo.getMethod())
                                        .positionCode(info.getPositionCode())
                                        .organizationCode(info.getOrganizationCode())
                                        .relAppId(info.getRelAppId())
                                        .build()
                                );
                    } else {
                        return new ArrayList<PermissionExtInfo>() {{
                            add(info);
                        }}.stream();
                    }
                })
                .map(info -> {
                    if (info.getOrganizationCode() == null || info.getOrganizationCode().isEmpty()) {
                        info.setOrganizationCode(Constant.OBJECT_UNDEFINED + "");
                    }
                    return info;
                })
                .collect(Collectors.toList());
    }

}
