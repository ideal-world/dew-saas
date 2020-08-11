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

package idealworld.dew.saas.service.ident.controller.app;

import com.ecfront.dew.common.Resp;
import group.idealworld.dew.Dew;
import idealworld.dew.saas.common.resp.StandardResp;
import idealworld.dew.saas.common.service.dto.IdentOptInfo;
import idealworld.dew.saas.service.ident.controller.BasicController;
import idealworld.dew.saas.service.ident.interceptor.AppHandlerInterceptor;
import idealworld.dew.saas.service.ident.service.AccountService;
import idealworld.dew.saas.service.ident.service.OAuthService;
import idealworld.dew.saas.service.ident.service.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 应用控制台认证管理操作.
 *
 * @author gudaoxuri
 */
@RestController
@Tag(name = "app auth", description = "应用控制台认证管理操作")
@RequestMapping(value = "/app/auth")
@Validated
public class AppAuthController extends BasicController {

    @Autowired
    private PermissionService permissionService;
    @Autowired
    private AppHandlerInterceptor appHandlerInterceptor;
    @Autowired
    private OAuthService oauthservice;
    @Autowired
    private AccountService accountService;

    /**
     * 获取当前登录用户.
     *
     * @return the opt info
     */
    @GetMapping(value = "optinfo")
    @Operation(summary = "获取当前登录用户")
    public Resp<IdentOptInfo> getOptInfo() {
        if (Dew.auth.getOptInfo().isEmpty()) {
            return StandardResp.unAuthorized("OPT_INFO", "Token不存在或已过期");
        }
        return StandardResp.success((IdentOptInfo) Dew.auth.getOptInfo().get());
    }

    /**
     * 订阅当前应用的权限信息.
     *
     * @param heartbeatPeriodSec the heartbeat period sec
     * @return the resp
     */
    @GetMapping(value = "permission/sub")
    @Operation(summary = "订阅当前应用的权限信息")
    public Resp<String> subPermissions(
            @Parameter(name = "heartbeatPeriodSec", description = "心跳周期", in = ParameterIn.QUERY, required = true)
            @RequestParam(value = "heartbeatPeriodSec") Integer heartbeatPeriodSec) {
        return permissionService.subPermissions(
                appHandlerInterceptor.getCurrentTenantAndAppId()._1, heartbeatPeriodSec);
    }

    /**
     * 当前应用的权限信息订阅的心跳检测.
     *
     * @param heartbeatPeriodSec the heartbeat period sec
     * @return the resp
     */
    @GetMapping(value = "permission/heartbeat")
    @Operation(summary = "当前应用的权限信息订阅的心跳检测")
    public Resp<Void> subHeartbeat(
            @Parameter(name = "heartbeatPeriodSec", description = "心跳周期", in = ParameterIn.QUERY, required = true)
            @RequestParam(value = "heartbeatPeriodSec") Integer heartbeatPeriodSec) {
        return permissionService.subHeartbeat(
                appHandlerInterceptor.getCurrentTenantAndAppId()._1, heartbeatPeriodSec);
    }

    /**
     * 取消当前应用的订阅权限信息.
     *
     * @return the resp
     */
    @DeleteMapping(value = "permission/sub")
    @Operation(summary = "取消当前应用的订阅权限信息，此操作会取消当前应用的所有实例订阅，在多实例场景下慎用")
    public Resp<Void> unSubPermission() {
        return permissionService.unSubPermission(appHandlerInterceptor.getCurrentTenantAndAppId()._1);
    }

    /**
     * 获取OAuth的AccessToken.
     *
     * @param oauthKind the oauth kind
     * @return the resp
     */
    @GetMapping(value = "tenant/oauth/{oauthKind}/access-token")
    @Operation(summary = "获取OAuth的AccessToken")
    public Resp<String> oauthGetAccessToken(@PathVariable String oauthKind) {
        return oauthservice.getAccessToken(oauthKind, appHandlerInterceptor.getCurrentTenantAndAppId()._0);
    }

    /**
     * 获取当前应用的租户Id.
     *
     * @return the resp
     */
    @GetMapping(value = "tenant/tenant-id")
    @Operation(summary = "获取当前应用的租户Id")
    public Resp<Long> fetchCurrentTenantId() {
        return Resp.success(appHandlerInterceptor.getCurrentTenantAndAppId()._0);
    }

    /**
     * 获取当前租户某个账号的某个认证AK.
     *
     * @param accountOpenId    the account open id
     * @param accountIdentKind the account ident kind
     * @return the account ident info
     */
    @GetMapping(value = "account/oauth/{accountOpenId}/{accountIdentKind}/ident-ak")
    @Operation(summary = "获取当前租户某个账号的某个认证AK")
    public Resp<String> getAccountIdentInfo(@PathVariable String accountOpenId,
                                            @PathVariable String accountIdentKind) {
        return accountService.getAccountIdentAk(accountOpenId, accountIdentKind,
                appHandlerInterceptor.getCurrentTenantAndAppId()._0);
    }

}
