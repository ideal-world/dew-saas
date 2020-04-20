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
 * 添加账号请求.
 *
 * @author gudaoxuri
 */
@Data
@Builder
@ApiModel("添加账号请求")
public class AddAccountReq implements Serializable {

    @NotNull
    @NotBlank
    @Size(max = 255)
    @ApiModelProperty(value = "账号名称", required = true)
    private String name;
    @Size(max = 1000)
    @ApiModelProperty(value = "账号头像")
    private String avatar;
    @Size(max = 2000)
    @ApiModelProperty(value = "账号扩展信息，Json格式")
    private String parameters;
    @NotNull
    @ApiModelProperty(value = "账号认证", required = true)
    private AddAccountIdentReq identReq;
    @ApiModelProperty(value = "账号岗位")
    private AddAccountPostReq postReq;

    /**
     * Instantiates a new Add account req.
     */
    @Tolerate
    public AddAccountReq() {
    }

}
