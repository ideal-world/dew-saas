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

package idealworld.dew.saas.service.ident.dto.organization;

import idealworld.dew.saas.service.ident.enumeration.OrganizationKind;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 修改机构请求.
 *
 * @author gudaoxuri
 */
@Data
@Builder
@ApiModel("修改机构请求")
public class ModifyOrganizationReq implements Serializable {

    @ApiModelProperty(value = "机构类型")
    private OrganizationKind kind;
    @Size(max = 1000)
    @ApiModelProperty(value = "业务编码")
    private String busCode;
    @Size(max = 255)
    @ApiModelProperty(value = "机构名称")
    private String name;
    @Size(max = 1000)
    @ApiModelProperty(value = "机构图标")
    private String icon;
    @Size(max = 2000)
    @ApiModelProperty(value = "机构扩展信息（Json格式）")
    private String parameters;
    @ApiModelProperty(value = "机构显示排序，asc")
    private Integer sort;
    @ApiModelProperty(value = "上级机构")
    private Long parentId;

    /**
     * Instantiates a new Modify organization req.
     */
    @Tolerate
    public ModifyOrganizationReq() {
    }

}
