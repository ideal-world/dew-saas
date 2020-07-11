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

package idealworld.dew.saas.service.ident.dto.tenant;

import idealworld.dew.saas.common.enumeration.CommonStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.io.Serializable;
import java.util.Date;

/**
 * 租户信息.
 *
 * @author gudaoxuri
 */
@Data
@Builder
@Schema(title = "租户信息")
public class TenantInfoResp implements Serializable {

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
    @Schema(title = "租户Id", required = true)
    private Long id;
    @Schema(title = "租户名称", required = true)
    private String name;
    @Schema(title = "租户图标")
    private String icon;
    @Schema(title = "是否开放账号注册", required = true)
    private Boolean allowAccountRegister;
    @Schema(title = "是否是全局账号", required = true)
    private Boolean globalAccount;
    @Schema(title = "是否允许跨租户", required = true)
    private Boolean allowCrossTenant;
    @Schema(title = "租户扩展信息（Json格式）")
    private String parameters;
    @Schema(title = "租户状态", required = true)
    private CommonStatus status;

    /**
     * Instantiates a new Tenant info.
     */
    @Tolerate
    public TenantInfoResp() {
    }


}
