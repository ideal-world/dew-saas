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

package idealworld.dew.saas.common.service.domain;

import group.idealworld.dew.Dew;
import idealworld.dew.saas.common.Constant;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.util.Date;

/**
 * Safe entity.
 *
 * @author gudaoxuri
 */
@MappedSuperclass
@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public abstract class SafeEntity extends IdEntity {

    /**
     * The Create user.
     */
    @Column(nullable = false,
            columnDefinition = "varchar(255) comment '创建者OpenId'")
    protected String createUser;

    /**
     * The Update user.
     */
    @Column(nullable = false,
            columnDefinition = "varchar(255) comment '最后一次修改者OpenId'")
    protected String updateUser;

    /**
     * The Create time.
     */
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(columnDefinition = "timestamp default CURRENT_TIMESTAMP comment '创建时间'")
    // @Column(columnDefinition = "datetime(6) comment '创建时间'")
    protected Date createTime;

    /**
     * The Update time.
     */
    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(columnDefinition = "timestamp default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP comment '最后一次修改时间'")
    // @Column(columnDefinition = "datetime(6) comment '最后一次修改时间'")
    protected Date updateTime;

    /**
     * Add user.
     */
    @PrePersist
    public void addUser() {
        Dew.auth.getOptInfo().ifPresent(optInfo -> {
            if (StringUtils.isEmpty(this.getCreateUser())) {
                this.setCreateUser((String) optInfo.getAccountCode());
            }
            if (StringUtils.isEmpty(this.getUpdateUser())) {
                this.setUpdateUser((String) optInfo.getAccountCode());
            }
        });
        if (StringUtils.isEmpty(this.getCreateUser())) {
            this.setCreateUser(Constant.OBJECT_UNDEFINED + "");
        }
        if (StringUtils.isEmpty(this.getUpdateUser())) {
            this.setUpdateUser(Constant.OBJECT_UNDEFINED + "");
        }
    }

    /**
     * Update user.
     */
    @PreUpdate
    public void updateUser() {
        Dew.auth.getOptInfo().ifPresent(optInfo -> {
            if (StringUtils.isEmpty(this.getUpdateUser())) {
                this.setUpdateUser((String) optInfo.getAccountCode());
            }
        });
        if (StringUtils.isEmpty(this.getUpdateUser())) {
            this.setUpdateUser(Constant.OBJECT_UNDEFINED + "");
        }
    }

}
