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

package idealworld.dew.saas.service.ident.domain;

import idealworld.dew.saas.common.service.domain.SafeEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

/**
 * 权限信息.
 *
 * @author gudaoxuri
 */
@Entity
@Table(name = "ident_permission", indexes = {
        @Index(columnList = "relPostId,relResourceId", unique = true),
        @Index(columnList = "relPostId"),
        @Index(columnList = "relResourceId"),
        @Index(columnList = "relAppId")
})
@org.hibernate.annotations.Table(appliesTo = "ident_permission",
        comment = "权限信息")
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Permission extends SafeEntity {

    @Column(nullable = false,
            columnDefinition = "bigint comment '关联岗位Id'")
    private Long relPostId;

    @Column(nullable = false,
            columnDefinition = "bigint comment '关联资源Id'")
    private Long relResourceId;

    // 为空表示是系统或租户管理员权限
    @Column(nullable = false,
            columnDefinition = "bigint comment '关联应用Id'")
    private Long relAppId;

    // 为空表示是系统或租户管理员权限
    @Column(nullable = false,
            columnDefinition = "bigint comment '关联租户Id'")
    private Long relTenantId;

}
