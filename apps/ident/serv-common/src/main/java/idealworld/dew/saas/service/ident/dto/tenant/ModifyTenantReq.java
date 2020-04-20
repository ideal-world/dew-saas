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

package idealworld.dew.saas.service.ident.dto.tenant;

import idealworld.dew.saas.common.enumeration.CommonStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 租户修改请求.
 *
 * @author gudaoxuri
 */
@Data
@Builder
@ApiModel("租户修改请求")
public class ModifyTenantReq implements Serializable {

    @Size(max = 2000)
    @ApiModelProperty(value = "租户名称", required = true)
    private String name;
    @ApiModelProperty(value = "租户图标")
    private String icon;
    @ApiModelProperty(value = "是否开放账号注册")
    private Boolean allowAccountRegister;
    @ApiModelProperty(value = "是否是全局账号")
    private Boolean globalAccount;
    @ApiModelProperty(value = "是否允许跨租户")
    private Boolean allowCrossTenant;
    @ApiModelProperty(value = "租户扩展信息（Json格式）")
    private String parameters;
    @ApiModelProperty(value = "租户状态")
    private CommonStatus status;

    /**
     * Instantiates a new Modify tenant req.
     */
    @Tolerate
    public ModifyTenantReq() {
    }

}
