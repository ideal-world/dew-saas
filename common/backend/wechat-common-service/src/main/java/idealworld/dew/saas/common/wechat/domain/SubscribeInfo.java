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

package idealworld.dew.saas.common.wechat.domain;

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
 * Subscribe info.
 *
 * @author gudaoxuri
 */
@Entity
@Table(name = "dew_subscribe", indexes = {
        @Index(columnList = "relAccountId,templateId")
})
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class SubscribeInfo extends IdEntity {

    @Column(nullable = false)
    private String templateId;

    @Column(nullable = false)
    private Integer totalCounter;

    @Column(nullable = false)
    private Integer balanceCounter;

    @Column(nullable = false)
    private String relAccountId;

}
