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

import java.io.Serializable;

/**
 * 账号岗位信息.
 *
 * @author gudaoxuri
 */
@Data
@Builder
@ApiModel("账号岗位信息")
public class AccountPostInfoResp implements Serializable {

    @ApiModelProperty(value = "账号岗位Id", required = true)
    private Long id;
    @ApiModelProperty(value = "岗位Id", required = true)
    private Long relPostId;
    @ApiModelProperty(value = "显示排序，asc", required = true)
    private Integer sort;

    /**
     * Instantiates a new Account post info.
     */
    @Tolerate
    public AccountPostInfoResp() {
    }


}
