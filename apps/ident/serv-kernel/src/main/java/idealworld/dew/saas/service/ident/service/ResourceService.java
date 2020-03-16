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
import idealworld.dew.saas.service.ident.domain.QResource;
import idealworld.dew.saas.service.ident.domain.Resource;
import idealworld.dew.saas.service.ident.dto.resouce.AddResourceGroupReq;
import idealworld.dew.saas.service.ident.dto.resouce.AddResourceReq;
import idealworld.dew.saas.service.ident.dto.resouce.ModifyResourceReq;
import idealworld.dew.saas.service.ident.dto.resouce.ResourceInfoResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author gudaoxuri
 */
@Service
public class ResourceService extends BasicService {

    @Autowired
    private AppService appService;

    @Transactional
    public Resp<Long> addResourceGroup(AddResourceGroupReq addResourceGroupReq, Long relAppId, Long relTenantId) {
        if (!appService.checkAppMembership(relAppId, relTenantId)) {
            return Constant.RESP.NOT_FOUNT();
        }
        var resource = Resource.builder()
                .kind(Resource.Kind.GROUP)
                .identifier("")
                .method("")
                .name(addResourceGroupReq.getName() != null ? addResourceGroupReq.getName() : "")
                .icon(addResourceGroupReq.getIcon() != null ? addResourceGroupReq.getIcon() : "")
                .sort(addResourceGroupReq.getSort() != null ? addResourceGroupReq.getSort() : 0)
                .parentId(addResourceGroupReq.getParentId() != null ? addResourceGroupReq.getParentId() : -1L)
                .relAppId(relAppId)
                .relTenantId(relTenantId)
                .build();
        return saveEntity(resource);
    }

    @Transactional
    public Resp<Long> addResource(AddResourceReq addResourceReq, Long relAppId, Long relTenantId) {
        if (!appService.checkAppMembership(relAppId, relTenantId)) {
            return Constant.RESP.NOT_FOUNT();
        }
        var resource = Resource.builder()
                .kind(addResourceReq.getKind())
                .identifier(addResourceReq.getIdentifier())
                .method(addResourceReq.getMethod() != null ? addResourceReq.getMethod() : "")
                .name(addResourceReq.getName() != null ? addResourceReq.getName() : "")
                .icon(addResourceReq.getIcon() != null ? addResourceReq.getIcon() : "")
                .sort(addResourceReq.getSort() != null ? addResourceReq.getSort() : 0)
                .parentId(addResourceReq.getParentId())
                .relAppId(relAppId)
                .relTenantId(relTenantId)
                .build();
        return saveEntity(resource);
    }

    @Transactional
    public Resp<Void> modifyResource(ModifyResourceReq modifyResourceReq, Long resourceId,
                                     Long relAppId, Long relTenantId) {
        var qResource = QResource.resource;
        var resourceUpdate = sqlBuilder.update(qResource)
                .where(qResource.id.eq(resourceId))
                .where(qResource.relAppId.eq(relAppId))
                .where(qResource.relTenantId.eq(relTenantId));
        if (modifyResourceReq.getIdentifier() != null) {
            resourceUpdate.set(qResource.identifier, modifyResourceReq.getIdentifier());
        }
        if (modifyResourceReq.getIdentifier() != null) {
            resourceUpdate.set(qResource.identifier, modifyResourceReq.getIdentifier());
        }
        if (modifyResourceReq.getMethod() != null) {
            resourceUpdate.set(qResource.method, modifyResourceReq.getMethod());
        }
        if (modifyResourceReq.getName() != null) {
            resourceUpdate.set(qResource.name, modifyResourceReq.getName());
        }
        if (modifyResourceReq.getIcon() != null) {
            resourceUpdate.set(qResource.icon, modifyResourceReq.getIcon());
        }
        if (modifyResourceReq.getSort() != null) {
            resourceUpdate.set(qResource.sort, modifyResourceReq.getSort());
        }
        if (modifyResourceReq.getParentId() != null) {
            resourceUpdate.set(qResource.parentId, modifyResourceReq.getParentId());
        }
        return updateEntity(resourceUpdate);
    }

    public Resp<ResourceInfoResp> getResource(Long resourceId, Long relAppId, Long relTenantId) {
        var qResource = QResource.resource;
        var resourceQuery = sqlBuilder
                .select(Projections.bean(ResourceInfoResp.class,
                        qResource.id,
                        qResource.kind,
                        qResource.identifier,
                        qResource.method,
                        qResource.name,
                        qResource.icon,
                        qResource.sort,
                        qResource.parentId,
                        qResource.relAppId))
                .from(qResource)
                .where(qResource.id.eq(resourceId))
                .where(qResource.relAppId.eq(relAppId))
                .where(qResource.relTenantId.eq(relTenantId));
        return getDTO(resourceQuery);
    }

    public Resp<List<ResourceInfoResp>> findResources(Long relAppId, Long relTenantId) {
        var qResource = QResource.resource;
        var resourceQuery = sqlBuilder
                .select(Projections.bean(ResourceInfoResp.class,
                        qResource.id,
                        qResource.kind,
                        qResource.identifier,
                        qResource.method,
                        qResource.name,
                        qResource.icon,
                        qResource.sort,
                        qResource.parentId,
                        qResource.relAppId))
                .from(qResource)
                .where(qResource.relAppId.eq(relAppId))
                .where(qResource.relTenantId.eq(relTenantId));
        return findDTOs(resourceQuery);
    }

    @Transactional
    public Resp<Void> deleteResource(Long resourceId, Long relAppId, Long relTenantId) {
        // TODO permission
        var qResource = QResource.resource;
        return deleteEntity(sqlBuilder
                .delete(qResource)
                .where(qResource.id.eq(resourceId))
                .where(qResource.relAppId.eq(relAppId))
                .where(qResource.relTenantId.eq(relTenantId))
        );
    }

    protected List<ResourceInfoResp> findResourceByGroup(Long resourceGroupId) {
        var qResource = QResource.resource;
        return sqlBuilder
                .select(Projections.bean(ResourceInfoResp.class,
                        qResource.id,
                        qResource.kind,
                        qResource.identifier,
                        qResource.method,
                        qResource.name,
                        qResource.icon,
                        qResource.sort,
                        qResource.parentId,
                        qResource.relAppId))
                .from(qResource)
                .where(qResource.parentId.eq(resourceGroupId))
                .fetch()
                .stream()
                .flatMap(res -> {
                    if (res.getKind() == Resource.Kind.GROUP) {
                        return findResourceByGroup(res.getId()).stream();
                    } else {
                        return new ArrayList<ResourceInfoResp>() {{
                            add(res);
                        }}.stream();
                    }
                })
                .collect(Collectors.toList());
    }

}
