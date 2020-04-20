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

package idealworld.dew.saas.service.ident.dto.app;

import idealworld.dew.saas.common.enumeration.CommonStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 修改应用请求.
 *
 * @author gudaoxuri
 */
@Data
@Builder
@ApiModel("修改应用请求")
public class ModifyAppReq implements Serializable {

    @Size(max = 255)
    @ApiModelProperty(value = "应用名称")
    private String name;
    @Size(max = 1000)
    @ApiModelProperty(value = "应用图标")
    private String icon;
    @Size(max = 2000)
    @ApiModelProperty(value = "应用扩展信息（Json格式）")
    private String parameters;
    @ApiModelProperty(value = "应用状态")
    private CommonStatus status;

    /**
     * Instantiates a new Modify app req.
     */
    @Tolerate
    public ModifyAppReq() {
    }

}
