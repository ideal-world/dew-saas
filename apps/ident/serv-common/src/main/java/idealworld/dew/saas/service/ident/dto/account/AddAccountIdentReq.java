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
 * 添加账号认证请求.
 *
 * @author gudaoxuri
 */
@Data
@Builder
@ApiModel("添加账号认证请求")
public class AddAccountIdentReq implements Serializable {

    @NotNull
    @ApiModelProperty(value = "账号认证类型名称", required = true)
    private AccountIdentKind kind;
    @NotNull
    @NotBlank
    @Size(max = 255)
    @ApiModelProperty(value = "账号认证名称", required = true)
    private String ak;
    @NotNull
    @NotBlank
    @Size(max = 255)
    @ApiModelProperty(value = "账号认证密钥", notes = "手机、邮件的认证类型对应的sk为验证码", required = true)
    private String sk;

    /**
     * Instantiates a new Add account ident req.
     */
    @Tolerate
    public AddAccountIdentReq() {
    }

}