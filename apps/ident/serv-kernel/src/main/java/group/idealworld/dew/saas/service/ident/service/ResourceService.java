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
import group.idealworld.dew.saas.service.ident.domain.QResource;
import group.idealworld.dew.saas.service.ident.domain.Resource;
import group.idealworld.dew.saas.service.ident.dto.resouce.AddResourceGroupReq;
import group.idealworld.dew.saas.service.ident.dto.resouce.AddResourceReq;
import group.idealworld.dew.saas.service.ident.dto.resouce.ModifyResourceReq;
import group.idealworld.dew.saas.service.ident.dto.resouce.ResourceInfoResp;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author gudaoxuri
 */
@Service
public class ResourceService extends BasicService {

    @Transactional
    public Resp<Long> addResourceGroup(AddResourceGroupReq addResourceGroupReq, Long relAppId) {
        var resource = Resource.builder()
                .kind(Resource.Kind.GROUP)
                .identifier("")
                .method("")
                .name(addResourceGroupReq.getName() != null ? addResourceGroupReq.getName() : "")
                .icon(addResourceGroupReq.getIcon() != null ? addResourceGroupReq.getIcon() : "")
                .sort(addResourceGroupReq.getSort() != null ? addResourceGroupReq.getSort() : 0)
                .parentId(addResourceGroupReq.getParentId() != null ? addResourceGroupReq.getParentId() : -1L)
                .relAppId(relAppId)
                .build();
        entityManager.persist(resource);
        return Resp.success(resource.getId());
    }

    @Transactional
    public Resp<Long> addResource(AddResourceReq addResourceReq, Long relAppId) {
        var resource = Resource.builder()
                .kind(addResourceReq.getKind())
                .identifier(addResourceReq.getIdentifier())
                .method(addResourceReq.getMethod() != null ? addResourceReq.getMethod() : "")
                .name(addResourceReq.getName() != null ? addResourceReq.getName() : "")
                .icon(addResourceReq.getIcon() != null ? addResourceReq.getIcon() : "")
                .sort(addResourceReq.getSort() != null ? addResourceReq.getSort() : 0)
                .parentId(addResourceReq.getParentId())
                .relAppId(relAppId)
                .build();
        entityManager.persist(resource);
        return Resp.success(resource.getId());
    }

    @Transactional
    public Resp<Void> modifyResource(ModifyResourceReq modifyResourceReq, Long relAppId) {
        var qResource = QResource.resource;
        var resourceUpdate = queryFactory.update(qResource)
                .where(qResource.id.eq(modifyResourceReq.getId()))
                .where(qResource.relAppId.eq(relAppId));
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
        resourceUpdate.execute();
        return Resp.success(null);
    }

    public Resp<ResourceInfoResp> getResource(Long id, Long relAppId) {
        var qResource = QResource.resource;
        var resource = queryFactory
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
                .where(qResource.id.eq(id))
                .where(qResource.relAppId.eq(relAppId))
                .fetchOne();
        return Resp.success(resource);
    }

    public Resp<List<ResourceInfoResp>> findResources(Long relAppId) {
        var qResource = QResource.resource;
        var resources = queryFactory
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
                .fetch();
        return Resp.success(resources);
    }

    @Transactional
    public Resp<Void> deleteResource(Long id, Long relAppId) {
        var qResource = QResource.resource;
        queryFactory.delete(qResource)
                .where(qResource.id.eq(id))
                .where(qResource.relAppId.eq(relAppId))
                .execute();
        return Resp.success(null);
    }

}
