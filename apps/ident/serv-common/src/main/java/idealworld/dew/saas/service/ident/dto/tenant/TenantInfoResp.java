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
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel("租户信息")
public class TenantInfoResp implements Serializable {

    /**
     * The Create user name.
     */
    @ApiModelProperty(value = "创建者", required = true)
    protected String createUserName;
    /**
     * The Update user name.
     */
    @ApiModelProperty(value = "最后一次修改者", required = true)
    protected String updateUserName;
    /**
     * The Create time.
     */
    @ApiModelProperty(value = "创建时间", required = true)
    protected Date createTime;
    /**
     * The Update time.
     */
    @ApiModelProperty(value = "最后一次修改时间", required = true)
    protected Date updateTime;
    @ApiModelProperty(value = "租户Id", required = true)
    private Long id;
    @ApiModelProperty(value = "租户名称", required = true)
    private String name;
    @ApiModelProperty(value = "租户图标")
    private String icon;
    @ApiModelProperty(value = "是否开放账号注册", required = true)
    private Boolean allowAccountRegister;
    @ApiModelProperty(value = "是否是全局账号", required = true)
    private Boolean globalAccount;
    @ApiModelProperty(value = "是否允许跨租户", required = true)
    private Boolean allowCrossTenant;
    @ApiModelProperty(value = "租户扩展信息（Json格式）")
    private String parameters;
    @ApiModelProperty(value = "租户状态", required = true)
    private CommonStatus status;

    /**
     * Instantiates a new Tenant info.
     */
    @Tolerate
    public TenantInfoResp() {
    }


}
