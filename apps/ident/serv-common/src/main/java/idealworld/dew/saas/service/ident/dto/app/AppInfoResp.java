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

import java.io.Serializable;
import java.util.Date;

/**
 * 应用信息.
 *
 * @author gudaoxuri
 */
@Data
@Builder
@Schema(title = "应用信息")
public class AppInfoResp implements Serializable {

    /**
     * The Create user name.
     */
    @Schema(title = "创建者", required = true)
    protected String createUserName;
    /**
     * The Update user name.
     */
    @Schema(title = "最后一次修改者", required = true)
    protected String updateUserName;
    /**
     * The Create time.
     */
    @Schema(title = "创建时间", required = true)
    protected Date createTime;
    /**
     * The Update time.
     */
    @Schema(title = "最后一次修改时间", required = true)
    protected Date updateTime;
    @Schema(title = "应用Id", required = true)
    private Long id;
    @Schema(title = "应用名称", required = true)
    private String name;
    @Schema(title = "应用图标")
    private String icon;
    @Schema(title = "应用扩展信息（Json格式）")
    private String parameters;
    @Schema(title = "应用状态", required = true)
    private CommonStatus status;
    @Schema(title = "应用所属租户", required = true)
    private Long relTenantId;

    /**
     * Instantiates a new App info.
     */
    @Tolerate
    public AppInfoResp() {
    }
}
