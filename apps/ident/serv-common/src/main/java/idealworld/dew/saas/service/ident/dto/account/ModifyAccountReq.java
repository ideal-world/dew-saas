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

package idealworld.dew.saas.service.ident.dto.account;

import idealworld.dew.saas.common.enumeration.CommonStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 修改账号请求.
 *
 * @author gudaoxuri
 */
@Data
@Builder
@Schema(title = "修改账号请求")
public class ModifyAccountReq implements Serializable {

    @Size(max = 255)
    @Schema(title = "账号名称")
    private String name;
    @Size(max = 1000)
    @Schema(title = "账号头像")
    private String avatar;
    @Size(max = 2000)
    @Schema(title = "账号扩展信息（Json格式）")
    private String parameters;
    @Schema(title = "账号状态")
    private CommonStatus status;

    /**
     * Instantiates a new Modify account req.
     */
    @Tolerate
    public ModifyAccountReq() {
    }

}
