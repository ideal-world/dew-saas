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

package group.idealworld.dew.saas.service.ident.dto;

import group.idealworld.dew.core.auth.dto.BasicOptInfo;
import group.idealworld.dew.core.auth.dto.OptInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * @author gudaoxuri
 */
@Data
@ApiModel("操作用户信息")
public class IdentOptInfo extends OptInfo<IdentOptInfo> {

    @ApiModelProperty(value = "账号名称",required = true)
    protected String name;
    @ApiModelProperty(value = "最后一次登录时间",required = true)
    protected LocalDateTime lastLoginTime;
    /*protected Set<RoleInfo> roleInfo;

    @ApiModel("角色信息")
    public static class RoleInfo {
        @ApiModelProperty(value = "角色编码",required = true)
        private String code;
        @ApiModelProperty(value = "角色显示名称",required = true)
        private String name;
        @ApiModelProperty(value = "租户编码",required = true)
        private String tenantCode;
    }*/

}
