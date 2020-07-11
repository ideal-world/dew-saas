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

package idealworld.dew.saas.service.ident.controller;

import com.ecfront.dew.common.Resp;
import group.idealworld.dew.Dew;
import idealworld.dew.saas.common.resp.StandardResp;
import idealworld.dew.saas.common.service.dto.IdentOptInfo;
import idealworld.dew.saas.service.ident.dto.account.LoginReq;
import idealworld.dew.saas.service.ident.dto.account.OAuthLoginReq;
import idealworld.dew.saas.service.ident.dto.tenant.RegisterTenantReq;
import idealworld.dew.saas.service.ident.service.AccountService;
import idealworld.dew.saas.service.ident.service.OAuthService;
import idealworld.dew.saas.service.ident.service.TenantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 公共操作.
 *
 * @author gudaoxuri
 */
@RestController
@Schema(name = "common", description = "公共操作")
@Validated
public class CommonController extends BasicController {

    @Autowired
    private TenantService tenantService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private OAuthService oauthservice;

    /**
     * 注册租户.
     *
     * @param registerTenantReq the register tenant req
     * @return the resp
     */
    @PostMapping(value = "/tenant/register")
    @Operation(description = "注册租户")
    public Resp<IdentOptInfo> registerTenant(@Validated @RequestBody RegisterTenantReq registerTenantReq) {
        return tenantService.registerTenant(registerTenantReq);
    }

    /**
     * 用户登录.
     *
     * @param tenantId the tenant id
     * @param loginReq the login req
     * @return the resp
     */
    @PostMapping(value = "/auth/{tenantId}/login")
    @Operation(description = "用户登录")
    public Resp<IdentOptInfo> login(@PathVariable Long tenantId,
                                    @Validated @RequestBody LoginReq loginReq) {
        return accountService.login(loginReq, tenantId);
    }

    /**
     * OAuth用户注册/登录.
     *
     * @param tenantId      the tenant id
     * @param oauthLoginReq the o auth login req
     * @return the resp
     * @throws Exception the exception
     */
    @PostMapping(value = "/oauth/{tenantId}/login")
    @Operation(description = "OAuth用户注册/登录")
    public Resp<IdentOptInfo> oauthLogin(@PathVariable Long tenantId,
                                         @Validated @RequestBody OAuthLoginReq oauthLoginReq) throws Exception {
        return oauthservice.login(oauthLoginReq, tenantId);
    }

    /**
     * 注销登录.
     *
     * @return the resp
     */
    @DeleteMapping(value = "/auth/{tenantId}/logout")
    @Operation(description = "注销登录")
    public Resp<Void> logout() {
        Dew.auth.getOptInfo().ifPresent(info -> {
            var openId = (String) info.getAccountCode();
            var token = info.getToken();
            accountService.logout(openId, token);
        });
        return StandardResp.success(null);
    }

}
