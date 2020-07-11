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

import idealworld.dew.saas.service.ident.enumeration.AccountIdentKind;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.io.Serializable;
import java.util.Date;

/**
 * 账号认证信息.
 *
 * @author gudaoxuri
 */
@Data
@Builder
@Schema(title = "账号认证信息")
public class AccountIdentInfoResp implements Serializable {

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
    @Schema(title = "账号认证Id", required = true)
    private Long id;
    @Schema(title = "账号认证类型名称", required = true)
    private AccountIdentKind kind;
    @Schema(title = "账号认证名称", required = true)
    private String ak;
    @Schema(title = "账号认证密钥", description = "手机、邮件的认证类型对应的sk为验证码", required = true)
    private String sk;
    @Schema(title = "账号认证有效开始时间", required = true)
    private Date validStartTime;
    @Schema(title = "账号认证有效结束时间", required = true)
    private Date validEndTime;
    @Schema(title = "账号认证剩余有效次数")
    private Long validTimes;

    /**
     * Instantiates a new Account ident info.
     */
    @Tolerate
    public AccountIdentInfoResp() {
    }

}
