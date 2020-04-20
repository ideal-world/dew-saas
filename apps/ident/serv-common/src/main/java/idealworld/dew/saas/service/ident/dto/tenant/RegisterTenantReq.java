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

import idealworld.dew.saas.service.ident.enumeration.AccountIdentKind;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 租户注册请求.
 *
 * @author gudaoxuri
 */
@Data
@Builder
@ApiModel("租户注册请求")
public class RegisterTenantReq implements Serializable {

    @NotNull
    @NotBlank
    @Size(max = 255)
    @ApiModelProperty(value = "租户名称", required = true)
    private String tenantName;
    @NotNull
    @NotBlank
    @Size(max = 255)
    @ApiModelProperty(value = "账户名称", required = true)
    private String accountName;
    @ApiModelProperty(value = "是否开放账号注册")
    private Boolean allowAccountRegister = false;
    @ApiModelProperty(value = "是否是全局账号")
    private Boolean globalAccount = true;
    @ApiModelProperty(value = "是否允许跨租户")
    private Boolean allowCrossTenant = false;
    @NotNull
    @ApiModelProperty(value = "认证类型", required = true)
    private AccountIdentKind identKind;
    @NotNull
    @NotBlank
    @Size(max = 255)
    @ApiModelProperty(value = "认证名", required = true)
    private String ak;
    @NotNull
    @NotBlank
    @Size(max = 255)
    @ApiModelProperty(value = "认证密钥", required = true)
    private String sk;

    /**
     * Instantiates a new Register tenant req.
     */
    @Tolerate
    public RegisterTenantReq() {
    }

}
