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

package idealworld.dew.saas.service.ident.dto.account;

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
 * OAuth注册/登录请求.
 *
 * @author gudaoxuri
 */
@Data
@Builder
@ApiModel("OAuth注册/登录请求")
public class OAuthLoginReq implements Serializable {

    @NotNull
    @ApiModelProperty(value = "认证类型", notes = "只能是OAuth类型的认证", required = true)
    private AccountIdentKind identKind;
    @NotNull
    @NotBlank
    @Size(max = 255)
    @ApiModelProperty(value = "授权码", required = true)
    private String code;

    /**
     * Instantiates a new O auth login req.
     */
    @Tolerate
    public OAuthLoginReq() {
    }

}
