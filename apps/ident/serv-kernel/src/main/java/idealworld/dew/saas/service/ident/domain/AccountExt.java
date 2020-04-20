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

import idealworld.dew.saas.common.service.domain.IdEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

/**
 * 账号扩展信息.
 *
 * @author gudaoxuri
 */
@Entity
@Table(name = "ident_account_ext", indexes = {
        @Index(columnList = "relAccountId"),
        @Index(columnList = "code")
})
@org.hibernate.annotations.Table(appliesTo = "ident_account_ext",
        comment = "账号扩展信息")
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
// TODO 未实现
public class AccountExt extends IdEntity {

    @Column(nullable = false,
            columnDefinition = "varchar(255) comment '字段编码'")
    private String code;

    @Column(nullable = false,
            columnDefinition = "varchar(2000) comment '字段值'")
    private String value;

    @Column(nullable = false,
            columnDefinition = "bigint comment '关联账号Id'")
    private Long relAccountId;

}
