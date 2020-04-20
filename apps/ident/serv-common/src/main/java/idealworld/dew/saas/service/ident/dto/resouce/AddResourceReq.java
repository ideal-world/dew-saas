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

package idealworld.dew.saas.service.ident.dto.resouce;

import idealworld.dew.saas.service.ident.enumeration.ResourceKind;
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
 * 添加资源请求.
 *
 * @author gudaoxuri
 */
@Data
@Builder
@ApiModel("添加资源请求")
public class AddResourceReq implements Serializable {

    @NotNull
    @ApiModelProperty(value = "资源类型", required = true)
    private ResourceKind kind;
    @NotNull
    @NotBlank
    @Size(max = 1000)
    @ApiModelProperty(value = "资源标识", required = true)
    private String identifier;
    @Size(max = 50)
    @ApiModelProperty(value = "资源方法")
    private String method;
    @Size(max = 255)
    @ApiModelProperty(value = "资源名称")
    private String name;
    @Size(max = 1000)
    @ApiModelProperty(value = "资源图标")
    private String icon;
    @ApiModelProperty(value = "资源显示排序，asc")
    private Integer sort;
    @ApiModelProperty(value = "资源所属组", required = true)
    private Long parentId;

    /**
     * Instantiates a new Add resource req.
     */
    @Tolerate
    public AddResourceReq() {
    }

}
