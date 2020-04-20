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
 * 账号凭证信息.
 *
 * @author gudaoxuri
 */
@Entity
@Table(name = "ident_account_cert", indexes = {
        @Index(columnList = "relAccountId,kind")
})
@org.hibernate.annotations.Table(appliesTo = "ident_account_cert",
        comment = "账号凭证信息")
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
// TODO 未实现
public class AccountCert extends SafeEntity {

    @Column(nullable = false,
            columnDefinition = "varchar(255) comment '凭证类型名称'")
    private String kind;

    @Column(nullable = false,
            columnDefinition = "tinyint comment '凭证保留的版本数量'")
    private Integer version;

    @Column(nullable = false,
            columnDefinition = "bigint comment '凭证有效时间（秒）'")
    private Long validTimeSec;

    @Column(nullable = false,
            columnDefinition = "bigint comment '关联账号Id'")
    private Long relAccountId;


}
