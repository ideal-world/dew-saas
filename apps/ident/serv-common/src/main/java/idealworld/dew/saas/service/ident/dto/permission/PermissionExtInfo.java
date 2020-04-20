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

package idealworld.dew.saas.service.ident.dto.permission;

import idealworld.dew.saas.service.ident.enumeration.ResourceKind;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

/**
 * 权限扩展信息.
 *
 * @author gudaoxuri
 */
@Data
@Builder
@ApiModel("权限扩展信息")
public class PermissionExtInfo {

    @ApiModelProperty(value = "权限Id", required = true)
    private Long permissionId;
    @ApiModelProperty(value = "关联资源类型", required = true)
    private ResourceKind resKind;
    @ApiModelProperty(value = "关联资源Id", required = true)
    private Long resId;
    @ApiModelProperty(value = "关联资源标识", required = true)
    private String resIdentifier;
    @ApiModelProperty(value = "关联资源方法", required = true)
    private String resMethod;
    @ApiModelProperty(value = "关联机构编码")
    private String organizationCode;
    @ApiModelProperty(value = "关联岗位编码", required = true)
    private String positionCode;
    @ApiModelProperty(value = "权限所属应用", required = true)
    private Long relAppId;

    /**
     * Instantiates a new Permission ext info.
     */
    @Tolerate
    public PermissionExtInfo() {
    }

}
