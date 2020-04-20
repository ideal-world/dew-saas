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
import idealworld.dew.saas.service.ident.enumeration.OrganizationKind;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

/**
 * 机构信息.
 *
 * @author gudaoxuri
 */
@Entity
@Table(name = "ident_organization", indexes = {
        @Index(columnList = "relTenantId,relAppId,code", unique = true),
        @Index(columnList = "parentId")
})
@org.hibernate.annotations.Table(appliesTo = "ident_organization",
        comment = "机构信息")
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Organization extends SafeEntity {

    @Column(nullable = false,
            columnDefinition = "varchar(100) comment '机构类型名称'")
    @Enumerated(EnumType.STRING)
    private OrganizationKind kind;

    @Column(nullable = false,
            columnDefinition = "varchar(1000) comment '机构编码，同租户、应用下唯一\n" +
                    "5位数字一组，带上下级关系'")
    // TODO 规则未实现
    private String code;

    @Column(nullable = false,
            columnDefinition = "varchar(1000) comment '业务编码'")
    private String busCode;

    @Column(nullable = false,
            columnDefinition = "varchar(255) comment '机构名称'")
    private String name;

    @Column(nullable = false,
            columnDefinition = "varchar(1000) comment '机构图标（路径）'")
    private String icon;

    @Column(nullable = false,
            columnDefinition = "varchar(2000) comment '机构扩展信息，Json格式'")
    private String parameters;

    @Column(nullable = false,
            columnDefinition = "int comment '机构显示排序，asc'")
    private Integer sort;

    @Column(nullable = false,
            columnDefinition = "bigint comment '上级机构Id'")
    private Long parentId;

    // 为空表示租户级机构
    // TODO 暂不支持租户级机构
    @Column(nullable = false,
            columnDefinition = "bigint comment '关联应用Id'")
    private Long relAppId;

    @Column(nullable = false,
            columnDefinition = "bigint comment '关联租户Id'")
    private Long relTenantId;

}
