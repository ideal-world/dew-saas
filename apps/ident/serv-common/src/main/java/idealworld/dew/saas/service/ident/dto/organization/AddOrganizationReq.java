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
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 添加机构请求.
 *
 * @author gudaoxuri
 */
@Data
@Builder
@Schema(title = "添加机构请求")
public class AddOrganizationReq implements Serializable {

    @NotNull
    @Schema(title = "机构类型", required = true)
    private OrganizationKind kind;
    @NotNull
    @NotBlank
    @Size(max = 1000)
    @Schema(title = "机构编码", required = true)
    private String code;
    @Size(max = 1000)
    @Schema(title = "业务编码")
    private String busCode;
    @NotNull
    @NotBlank
    @Size(max = 255)
    @Schema(title = "机构名称", required = true)
    private String name;
    @Size(max = 1000)
    @Schema(title = "机构图标")
    private String icon;
    @Size(max = 2000)
    @Schema(title = "机构扩展信息（Json格式）")
    private String parameters;
    @Schema(title = "机构显示排序，asc")
    private Integer sort;
    @Schema(title = "上级机构", required = true)
    private Long parentId;

    /**
     * Instantiates a new Add organization req.
     */
    @Tolerate
    public AddOrganizationReq() {
    }

}
