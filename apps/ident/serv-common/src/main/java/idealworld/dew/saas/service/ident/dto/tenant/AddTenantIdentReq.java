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

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 添加租户认证请求.
 *
 * @author gudaoxuri
 */
@Data
@Builder
@ApiModel("添加租户认证请求")
public class AddTenantIdentReq implements Serializable {

    @NotNull
    @ApiModelProperty(value = "租户认证类型名称", required = true)
    private AccountIdentKind kind;
    @Size(max = 2000)
    @ApiModelProperty(value = "租户认证校验正则规则说明")
    private String validRuleNote;
    @Size(max = 2000)
    @ApiModelProperty(value = "租户认证校验正则规则")
    private String validRule;
    @ApiModelProperty(value = "租户认证有效时间（秒）")
    private Long validTimeSec;
    @Size(max = 1000)
    @ApiModelProperty(value = "OAuth下的应用密钥ID或是AppId")
    private String oauthAk;
    @Size(max = 2000)
    @ApiModelProperty(value = "OAuth下的应用密钥")
    private String oauthSk;

    /**
     * Instantiates a new Add tenant ident req.
     */
    @Tolerate
    public AddTenantIdentReq() {
    }

}
