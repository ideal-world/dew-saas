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
import idealworld.dew.saas.service.ident.enumeration.AccountIdentKind;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.Date;

/**
 * 账号认证信息.
 *
 * @author gudaoxuri
 */
@Entity
@Table(name = "ident_account_ident", indexes = {
        @Index(columnList = "relTenantId,kind,ak", unique = true),
        @Index(columnList = "relTenantId,relAccountId,kind,ak"),
        @Index(columnList = "relTenantId,relAccountId,kind,ak,validStartTime,validEndTime"),
        @Index(columnList = "relAccountId,kind,validStartTime,validEndTime")
})
@org.hibernate.annotations.Table(appliesTo = "ident_account_ident",
        comment = "账号认证信息")
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class AccountIdent extends SafeEntity {

    @Column(nullable = false,
            columnDefinition = "varchar(100) comment '账号认证类型名称'")
    @Enumerated(EnumType.STRING)
    private AccountIdentKind kind;

    @Column(nullable = false,
            columnDefinition = "varchar(255) comment '账号认证名称'")
    private String ak;

    @Column(nullable = false,
            columnDefinition = "varchar(255) comment '账号认证密钥'")
    private String sk;

    @Column(nullable = false,
            columnDefinition = "datetime(6) comment '账号认证有效开始时间'")
    private Date validStartTime;

    @Column(nullable = false,
            columnDefinition = "datetime(6) comment '账号认证有效结束时间'")
    private Date validEndTime;

    // TODO 未实现
    @Column(nullable = false,
            columnDefinition = "bigint comment '账号认证剩余有效次数'")
    private Long validTimes;

    @Column(nullable = false,
            columnDefinition = "bigint comment '关联账号Id'")
    private Long relAccountId;

    @Column(nullable = false,
            columnDefinition = "bigint comment '关联租户Id'")
    private Long relTenantId;

}
