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

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 修改资源（组）请求.
 *
 * @author gudaoxuri
 */
@Data
@Builder
@Schema(title = "修改资源（组）请求")
public class ModifyResourceReq implements Serializable {

    @Size(max = 1000)
    @Schema(title = "资源标识")
    private String identifier;
    @Size(max = 50)
    @Schema(title = "资源方法")
    private String method;
    @Size(max = 255)
    @Schema(title = "资源（组）名称")
    private String name;
    @Size(max = 1000)
    @Schema(title = "资源（组）图标")
    private String icon;
    @Schema(title = "资源（组）显示排序，asc")
    private Integer sort;
    @Schema(title = "资源所属组")
    private Long parentId;

    /**
     * Instantiates a new Modify resource req.
     */
    @Tolerate
    public ModifyResourceReq() {
    }

}
