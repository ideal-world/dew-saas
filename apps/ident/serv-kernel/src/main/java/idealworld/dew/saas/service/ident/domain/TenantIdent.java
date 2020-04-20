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

import idealworld.dew.saas.common.enumeration.CommonStatus;
import idealworld.dew.saas.common.service.domain.SafeEntity;
import idealworld.dew.saas.service.ident.enumeration.AccountIdentKind;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

/**
 * 租户认证信息.
 *
 * @author gudaoxuri
 */
@Entity
@Table(name = "ident_tenant_ident", indexes = {
        @Index(columnList = "relTenantId,kind", unique = true),
        @Index(columnList = "status")
})
@org.hibernate.annotations.Table(appliesTo = "ident_tenant_ident",
        comment = "租户认证信息")
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class TenantIdent extends SafeEntity {

    @Column(nullable = false,
            columnDefinition = "varchar(100) comment '租户认证类型名称'")
    @Enumerated(EnumType.STRING)
    private AccountIdentKind kind;

    @Column(nullable = false,
            columnDefinition = "varchar(2000) comment '租户认证校验正则规则说明'")
    private String validRuleNote;

    @Column(nullable = false,
            columnDefinition = "varchar(2000) comment '租户认证校验正则规则'")
    private String validRule;

    @Column(nullable = false,
            columnDefinition = "bigint comment '租户认证有效时间（秒）'")
    private Long validTimeSec;

    @Column(nullable = false,
            columnDefinition = "varchar(1000) comment 'OAuth下的应用密钥ID或是AppId'")
    private String oauthAk;

    @Column(nullable = false,
            columnDefinition = "varchar(2000) comment 'OAuth下的应用密钥'")
    private String oauthSk;

    @Column(nullable = false,
            columnDefinition = "varchar(50) comment '租户认证状态'")
    private CommonStatus status;

    @Column(nullable = false,
            columnDefinition = "bigint comment '关联租户Id'")
    private Long relTenantId;

}
