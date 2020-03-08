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

package group.idealworld.dew.saas.service.ident;


import group.idealworld.dew.saas.service.ident.domain.Permission;
import group.idealworld.dew.saas.service.ident.domain.Position;
import group.idealworld.dew.saas.service.ident.domain.Post;
import group.idealworld.dew.saas.service.ident.domain.Resource;
import group.idealworld.dew.saas.service.ident.service.BasicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;

/**
 * @author gudaoxuri
 */
@Service
public class IdentInitiator extends BasicService implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private IdentConfig identConfig;

    /**
     * Init.
     */
    @Transactional
    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        entityManager.persist(Position.builder()
                .code(identConfig.getSecurity().getSystemAdminPositionCode())
                .name(identConfig.getSecurity().getSystemAdminPositionName())
                .icon("")
                .relAppId(-1L)
                .build());
        entityManager.persist(Position.builder()
                .code(identConfig.getSecurity().getTenantAdminPositionCode())
                .name(identConfig.getSecurity().getTenantAdminPositionName())
                .icon("")
                .relAppId(-1L)
                .build());

        var adminRes = Resource.builder()
                .kind(Resource.Kind.URI)
                .identifier("/admin/**")
                .method("")
                .name("")
                .icon("")
                .sort(0)
                .parentId(-1L)
                .relAppId(-1L)
                .build();
        entityManager.persist(adminRes);
        var tenantRes = Resource.builder()
                .kind(Resource.Kind.URI)
                .identifier("/console/**")
                .method("")
                .name("")
                .icon("")
                .sort(0)
                .parentId(-1L)
                .relAppId(-1L)
                .build();
        entityManager.persist(tenantRes);

        var adminPost = Post.builder()
                .organizationCode("")
                .positionCode(identConfig.getSecurity().getSystemAdminPositionCode())
                .build();
        entityManager.persist(adminPost);
        var tenantPost = Post.builder()
                .organizationCode("")
                .positionCode(identConfig.getSecurity().getTenantAdminPositionCode())
                .build();
        entityManager.persist(tenantPost);

        entityManager.persist(Permission.builder()
                .relPostId(adminPost.getId())
                .relResourceId(adminRes.getId())
                .build());
        entityManager.persist(Permission.builder()
                .relPostId(tenantPost.getId())
                .relResourceId(tenantRes.getId())
                .build());
    }
}
