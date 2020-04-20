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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 应用控制台认证管理操作.
 *
 * @author gudaoxuri
 */
@RestController
@Api(value = "应用控制台认证管理操作", description = "应用控制台认证管理操作")
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
    @ApiOperation(value = "获取当前登录用户")
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
    @ApiOperation(value = "订阅当前应用的权限信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "heartbeatPeriodSec", value = "心跳周期", paramType = "query", dataType = "int", required = true)
    })
    public Resp<String> subPermissions(@RequestParam(value = "heartbeatPeriodSec") Integer heartbeatPeriodSec) {
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
    @ApiOperation(value = "当前应用的权限信息订阅的心跳检测")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "heartbeatPeriodSec", value = "心跳周期", paramType = "query", dataType = "int", required = true)
    })
    public Resp<Void> subHeartbeat(@RequestParam(value = "heartbeatPeriodSec") Integer heartbeatPeriodSec) {
        return permissionService.subHeartbeat(
                appHandlerInterceptor.getCurrentTenantAndAppId()._1, heartbeatPeriodSec);
    }

    /**
     * 取消当前应用的订阅权限信息.
     *
     * @return the resp
     */
    @DeleteMapping(value = "permission/sub")
    @ApiOperation(value = "取消当前应用的订阅权限信息", notes = "此操作会取消当前应用的所有实例订阅，在多实例场景下慎用")
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
    @ApiOperation(value = "获取OAuth的AccessToken")
    public Resp<String> oauthGetAccessToken(@PathVariable String oauthKind) {
        return oauthservice.getAccessToken(oauthKind, appHandlerInterceptor.getCurrentTenantAndAppId()._0);
    }

    /**
     * 获取当前租户某个账号的某个认证AK.
     *
     * @param accountOpenId    the account open id
     * @param accountIdentKind the account ident kind
     * @return the account ident info
     */
    @GetMapping(value = "account/oauth/{accountOpenId}/{accountIdentKind}/ident-ak")
    @ApiOperation(value = "获取当前租户某个账号的某个认证AK")
    public Resp<String> getAccountIdentInfo(@PathVariable String accountOpenId,
                                            @PathVariable String accountIdentKind) {
        return accountService.getAccountIdentAk(accountOpenId, accountIdentKind,
                appHandlerInterceptor.getCurrentTenantAndAppId()._0);
    }

}
