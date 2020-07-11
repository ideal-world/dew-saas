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

package idealworld.dew.saas.service.ident.dto.app;

import idealworld.dew.saas.common.enumeration.CommonStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

/**
 * 修改应用认证请求.
 *
 * @author gudaoxuri
 */
@Data
@Builder
@Schema(title = "修改应用认证请求")
public class ModifyAppIdentReq implements Serializable {

    @NotNull
    @NotBlank
    @Size(max = 1000)
    @Schema(title = "应用认证用途", required = true)
    private String note;
    @Schema(title = "应用认证有效时间")
    private Date validTime;
    @Schema(title = "应用认证状态")
    private CommonStatus status;

    /**
     * Instantiates a new Modify app ident req.
     */
    @Tolerate
    public ModifyAppIdentReq() {
    }

}
