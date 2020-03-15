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
import group.idealworld.dew.core.web.interceptor.BasicHandlerInterceptor;
import idealworld.dew.saas.service.ident.Constant;
import idealworld.dew.saas.service.ident.domain.*;
import idealworld.dew.saas.service.ident.dto.permission.AddPermissionReq;
import idealworld.dew.saas.service.ident.dto.permission.PermissionInfoResp;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author gudaoxuri
 */
@Service
public class PermissionService extends BasicService {

    public static final String ROLE_SPLIT = "-";

    @Autowired
    private ResourceService resourceService;
    @Autowired
    private AppService appService;

    public void buildUrlAuth() {
        Map<String, Map<String, List<String>>> roleAuth = findPermissionInfo(Resource.Kind.URI).stream()
                .collect(Collectors.groupingBy(
                        info -> info.getRelAppId() + ROLE_SPLIT + info.getPositionCode(),
                        Collectors.groupingBy(
                                PermissionInfo::getResMethod,
                                Collectors.mapping(PermissionInfo::getResIdentifier, Collectors.toList())
                        )
                ));
        BasicHandlerInterceptor.fillAuthInfo(null, roleAuth);
    }

    @Transactional
    public Resp<Long> addPermission(AddPermissionReq addPermissionReq, Long relAppId, Long relTenantId) {
        if (!appService.checkAppMembership(relAppId, relTenantId)) {
            return Constant.RESP.NOT_FOUNT();
        }
        var permission = Permission.builder()
                .relPostId(addPermissionReq.getRelPostId())
                .relResourceId(addPermissionReq.getRelResourceId())
                .relAppId(relAppId)
                .relTenantId(relTenantId)
                .build();
        return saveEntity(permission);
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
        return Resp.success(null);
    }

    private List<PermissionInfo> findPermissionInfo(Resource.Kind resourceKind) {
        var qResource = QResource.resource;
        var qPost = QPost.post;
        var qPosition = QPosition.position;
        var qPermission = QPermission.permission;
        return sqlBuilder
                .select(Projections.bean(
                        PermissionInfo.class,
                        qResource.kind.as("resKind"),
                        qResource.id.as("resId"),
                        qResource.identifier.as("resIdentifier"),
                        qResource.method.as("resMethod"),
                        qPosition.code.as("positionCode"),
                        qPosition.relAppId))
                .from(qPermission)
                .leftJoin(qPost).on(qPermission.relPostId.eq(qPost.id))
                .leftJoin(qPosition).on(qPost.relPositionCode.eq(qPosition.code))
                .leftJoin(qResource).on(qPermission.relResourceId.eq(qResource.id))
                .where(qResource.kind.in(resourceKind, Resource.Kind.GROUP))
                .fetch()
                .stream()
                .flatMap(info -> {
                    if (info.resKind == Resource.Kind.GROUP) {
                        return resourceService.findResourceByGroup(info.getResId())
                                .stream()
                                .map(resInfo -> PermissionInfo.builder()
                                        .resKind(resInfo.getKind())
                                        .resId(resInfo.getId())
                                        .resIdentifier(resInfo.getIdentifier())
                                        .resMethod(resInfo.getMethod())
                                        .positionCode(info.positionCode)
                                        .relAppId(info.relAppId)
                                        .build()
                                );
                    } else {
                        return new ArrayList<PermissionInfo>() {{
                            add(info);
                        }}.stream();
                    }
                })
                .collect(Collectors.toList());
    }

    @Data
    @Builder
    public static class PermissionInfo {

        @Tolerate
        public PermissionInfo() {
        }

        private Resource.Kind resKind;
        private Long resId;
        private String resIdentifier;
        private String resMethod;
        private String positionCode;
        private Long relAppId;

    }

}
