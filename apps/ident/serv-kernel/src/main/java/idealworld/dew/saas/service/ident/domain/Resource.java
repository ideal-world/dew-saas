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
import idealworld.dew.saas.service.ident.enumeration.ResourceKind;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

/**
 * 资源信息.
 *
 * @author gudaoxuri
 */
@Entity
@Table(name = "ident_resource", indexes = {
        @Index(columnList = "relAppId,identifier,method", unique = true),
        @Index(columnList = "relTenantId,relAppId"),
        @Index(columnList = "parentId")
})
@org.hibernate.annotations.Table(appliesTo = "ident_resource",
        comment = "资源信息")
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Resource extends SafeEntity {

    @Column(nullable = false,
            columnDefinition = "varchar(100) comment '资源类型名称'")
    @Enumerated(EnumType.STRING)
    private ResourceKind kind;

    @Column(nullable = false,
            columnDefinition = "varchar(1000) comment '资源标识'")
    private String identifier;

    @Column(nullable = false,
            columnDefinition = "varchar(50) comment '资源方法'")
    private String method;

    @Column(nullable = false,
            columnDefinition = "varchar(255) comment '资源名称'")
    private String name;

    @Column(nullable = false,
            columnDefinition = "varchar(1000) comment '资源图标（路径）'")
    private String icon;

    @Column(nullable = false,
            columnDefinition = "int comment '资源显示排序，asc'")
    private Integer sort;

    @Column(nullable = false,
            columnDefinition = "bigint comment '资源所属组Id'")
    private Long parentId;

    // 为空表示是系统或租户控制台资源
    @Column(nullable = false,
            columnDefinition = "bigint comment '关联应用Id'")
    private Long relAppId;

    // 为空表示是系统或租户控制台资源
    @Column(nullable = false,
            columnDefinition = "bigint comment '关联租户Id'")
    private Long relTenantId;

}
