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

import java.io.Serializable;
import java.util.Date;

/**
 * 机构信息.
 *
 * @author gudaoxuri
 */
@Data
@Builder
@ApiModel("机构信息")
public class OrganizationInfoResp implements Serializable {

    /**
     * The Create user name.
     */
    @ApiModelProperty(value = "创建者", required = true)
    protected String createUserName;
    /**
     * The Update user name.
     */
    @ApiModelProperty(value = "最后一次修改者", required = true)
    protected String updateUserName;
    /**
     * The Create time.
     */
    @ApiModelProperty(value = "创建时间", required = true)
    protected Date createTime;
    /**
     * The Update time.
     */
    @ApiModelProperty(value = "最后一次修改时间", required = true)
    protected Date updateTime;
    @ApiModelProperty(value = "机构Id", required = true)
    private Long id;
    @ApiModelProperty(value = "机构类型", required = true)
    private OrganizationKind kind;
    @ApiModelProperty(value = "机构编码", required = true)
    private String code;
    @ApiModelProperty(value = "业务编码")
    private String busCode;
    @ApiModelProperty(value = "机构名称", required = true)
    private String name;
    @ApiModelProperty(value = "机构图标")
    private String icon;
    @ApiModelProperty(value = "机构扩展信息（Json格式）")
    private String parameters;
    @ApiModelProperty(value = "机构显示排序，asc")
    private Integer sort;
    @ApiModelProperty(value = "上级机构", required = true)
    private Long parentId;
    @ApiModelProperty(value = "机构所属应用", required = true)
    private Long relAppId;
    @ApiModelProperty(value = "机构所属租户", required = true)
    private Long relTenantId;

    /**
     * Instantiates a new Organization info.
     */
    @Tolerate
    public OrganizationInfoResp() {
    }

}
