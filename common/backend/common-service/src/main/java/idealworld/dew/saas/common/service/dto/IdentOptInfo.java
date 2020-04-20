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

package idealworld.dew.saas.common.service.dto;

import group.idealworld.dew.core.auth.dto.OptInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

/**
 * Ident opt info.
 *
 * @author gudaoxuri
 */
@Data
@ApiModel(value = "操作用户信息")
public class IdentOptInfo extends OptInfo<IdentOptInfo> {

    @ApiModelProperty(value = "关联租户Id", required = true)
    private Long relTenantId;

    @ApiModelProperty(value = "账号扩展信息", required = true)
    private Map<String, Object> parameters;

}
