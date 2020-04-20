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

import java.io.Serializable;

/**
 * 资源信息.
 *
 * @author gudaoxuri
 */
@Data
@Builder
@ApiModel("资源信息")
public class ResourceInfoResp implements Serializable {

    @ApiModelProperty(value = "资源（组）Id", required = true)
    private Long id;
    @ApiModelProperty(value = "资源类型", required = true)
    private ResourceKind kind;
    @ApiModelProperty(value = "资源标识", required = true)
    private String identifier;
    @ApiModelProperty(value = "资源方法")
    private String method;
    @ApiModelProperty(value = "资源名称")
    private String name;
    @ApiModelProperty(value = "资源图标")
    private String icon;
    @ApiModelProperty(value = "资源显示排序，asc")
    private Integer sort;
    @ApiModelProperty(value = "资源所属组")
    private Long parentId;
    @ApiModelProperty(value = "资源所属应用", required = true)
    private Long relAppId;

    /**
     * Instantiates a new Resource info.
     */
    @Tolerate
    public ResourceInfoResp() {
    }

}
