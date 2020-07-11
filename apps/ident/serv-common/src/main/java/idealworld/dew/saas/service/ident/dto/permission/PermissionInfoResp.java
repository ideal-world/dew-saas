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

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.io.Serializable;

/**
 * 权限信息.
 *
 * @author gudaoxuri
 */
@Data
@Builder
@Schema(title = "权限信息")
public class PermissionInfoResp implements Serializable {

    @Schema(title = "权限Id", required = true)
    private Long id;
    @Schema(title = "关联岗位", required = true)
    private Long relPostId;
    @Schema(title = "关联资源（组）", required = true)
    private Long relResourceId;
    @Schema(title = "权限所属应用", required = true)
    private Long relAppId;

    /**
     * Instantiates a new Permission info.
     */
    @Tolerate
    public PermissionInfoResp() {
    }

}
