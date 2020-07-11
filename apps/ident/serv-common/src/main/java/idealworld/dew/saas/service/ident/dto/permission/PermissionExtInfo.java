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
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(title = "权限扩展信息")
public class PermissionExtInfo {

    @Schema(title = "权限Id", required = true)
    private Long permissionId;
    @Schema(title = "关联资源类型", required = true)
    private ResourceKind resKind;
    @Schema(title = "关联资源Id", required = true)
    private Long resId;
    @Schema(title = "关联资源标识", required = true)
    private String resIdentifier;
    @Schema(title = "关联资源方法", required = true)
    private String resMethod;
    @Schema(title = "关联机构编码")
    private String organizationCode;
    @Schema(title = "关联岗位编码", required = true)
    private String positionCode;
    @Schema(title = "权限所属应用", required = true)
    private Long relAppId;

    /**
     * Instantiates a new Permission ext info.
     */
    @Tolerate
    public PermissionExtInfo() {
    }

}
