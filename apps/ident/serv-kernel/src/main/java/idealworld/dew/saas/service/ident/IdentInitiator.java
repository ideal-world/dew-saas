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

package idealworld.dew.saas.service.ident;


import group.idealworld.dew.core.DewContext;
import idealworld.dew.saas.service.ident.domain.*;
import idealworld.dew.saas.service.ident.dto.IdentOptInfo;
import idealworld.dew.saas.service.ident.service.BasicService;
import idealworld.dew.saas.service.ident.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author gudaoxuri
 */
@Service
public class IdentInitiator extends BasicService implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private IdentConfig identConfig;
    @Autowired
    private PermissionService permissionService;

    /**
     * Init.
     */
    @Transactional
    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        DewContext.setOptInfoClazz(IdentOptInfo.class);
        initPermissionData();
        permissionService.buildUrlAuth();
    }

    private void initPermissionData() {
        var qPosition = QPosition.position;
        if (existQuery(sqlBuilder.selectFrom(qPosition)
                .where(qPosition.code.eq(identConfig.getSecurity().getSystemAdminPositionCode())))
                .getBody()) {
            return;
        }
        saveEntity(Position.builder()
                .code(identConfig.getSecurity().getSystemAdminPositionCode())
                .name(identConfig.getSecurity().getSystemAdminPositionName())
                .icon("")
                .relAppId(-1L)
                .relTenantId(-1L)
                .build());
        saveEntity(Position.builder()
                .code(identConfig.getSecurity().getTenantAdminPositionCode())
                .name(identConfig.getSecurity().getTenantAdminPositionName())
                .icon("")
                .relAppId(-1L)
                .relTenantId(-1L)
                .build());

        var adminRes = Resource.builder()
                .kind(Resource.Kind.URI)
                .identifier("/admin/**")
                .method("*")
                .name("")
                .icon("")
                .sort(0)
                .parentId(-1L)
                .relAppId(-1L)
                .relTenantId(-1L)
                .build();
        saveEntity(adminRes);
        var tenantRes = Resource.builder()
                .kind(Resource.Kind.URI)
                .identifier("/console/**")
                .method("*")
                .name("")
                .icon("")
                .sort(0)
                .parentId(-1L)
                .relAppId(-1L)
                .relTenantId(-1L)
                .build();
        saveEntity(tenantRes);

        var adminPost = Post.builder()
                .relOrganizationId(-1L)
                .relPositionCode(identConfig.getSecurity().getSystemAdminPositionCode())
                .relAppId(-1L)
                .relTenantId(-1L)
                .build();
        saveEntity(adminPost);
        var tenantPost = Post.builder()
                .relOrganizationId(-1L)
                .relPositionCode(identConfig.getSecurity().getTenantAdminPositionCode())
                .relAppId(-1L)
                .relTenantId(-1L)
                .build();
        saveEntity(tenantPost);

        saveEntity(Permission.builder()
                .relPostId(adminPost.getId())
                .relResourceId(adminRes.getId())
                .relAppId(-1L)
                .relTenantId(-1L)
                .build());
        saveEntity(Permission.builder()
                .relPostId(tenantPost.getId())
                .relResourceId(tenantRes.getId())
                .relAppId(-1L)
                .relTenantId(-1L)
                .build());
    }
}