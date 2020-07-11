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

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 权限请求.
 *
 * @author gudaoxuri
 */
@Data
@Builder
@Schema(title = "权限请求")
public class AddPermissionReq implements Serializable {

    @NotNull
    @Schema(title = "关联岗位", required = true)
    private Long relPostId;
    @NotNull
    @Schema(title = "关联资源（组）", required = true)
    private Long relResourceId;

    /**
     * Instantiates a new Add permission req.
     */
    @Tolerate
    public AddPermissionReq() {
    }

}
