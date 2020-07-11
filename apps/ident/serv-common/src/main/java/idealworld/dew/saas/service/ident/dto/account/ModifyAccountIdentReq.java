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

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

/**
 * 修改账号认证请求.
 *
 * @author gudaoxuri
 */
@Data
@Builder
@Schema(title = "修改账号认证请求")
public class ModifyAccountIdentReq implements Serializable {

    @Size(max = 255)
    @Schema(title = "账号认证密钥")
    private String sk;
    @Schema(title = "账号认证有效开始时间")
    private Date validStartTime;
    @Schema(title = "账号认证有效结束时间")
    private Date validEndTime;
    @Schema(title = "账号认证剩余有效次数")
    private Long validTimes;

    /**
     * Instantiates a new Modify account ident req.
     */
    @Tolerate
    public ModifyAccountIdentReq() {
    }

}
