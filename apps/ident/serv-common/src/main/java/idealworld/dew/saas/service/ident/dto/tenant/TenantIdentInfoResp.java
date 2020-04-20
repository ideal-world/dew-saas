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
import idealworld.dew.saas.service.ident.enumeration.AccountIdentKind;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.io.Serializable;
import java.util.Date;

/**
 * 租户认证信息.
 *
 * @author gudaoxuri
 */
@Data
@Builder
@ApiModel("租户认证信息")
public class TenantIdentInfoResp implements Serializable {

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
    @ApiModelProperty(value = "租户认证Id", required = true)
    private Long id;
    @ApiModelProperty(value = "租户认证类型名称", required = true)
    private AccountIdentKind kind;
    @ApiModelProperty(value = "租户认证校验正则规则说明")
    private String validRuleNote;
    @ApiModelProperty(value = "租户认证校验正则规则")
    private String validRule;
    @ApiModelProperty(value = "租户认证有效时间（秒）")
    private Long validTimeSec;
    @ApiModelProperty(value = "OAuth下的应用密钥ID或是AppId")
    private String oauthAk;
    @ApiModelProperty(value = "OAuth下的应用密钥")
    private String oauthSk;
    @ApiModelProperty(value = "租户状态", required = true)
    private CommonStatus status;

    /**
     * Instantiates a new Tenant ident info.
     */
    @Tolerate
    public TenantIdentInfoResp() {
    }

}
