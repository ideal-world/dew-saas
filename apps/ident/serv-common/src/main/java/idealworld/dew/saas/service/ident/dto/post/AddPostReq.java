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

package idealworld.dew.saas.service.ident.dto.post;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 添加岗位请求.
 *
 * @author gudaoxuri
 */
@Data
@Builder
@Schema(title = "添加岗位请求")
public class AddPostReq implements Serializable {

    @Size(max = 1000)
    @Schema(title = "机构Id")
    private String relOrganizationCode;
    @NotNull
    @NotBlank
    @Size(max = 1000)
    @Schema(title = "职位编码", required = true)
    private String relPositionCode;
    @Schema(title = "显示排序，asc")
    private Integer sort;

    /**
     * Instantiates a new Add post req.
     */
    @Tolerate
    public AddPostReq() {
    }

}
