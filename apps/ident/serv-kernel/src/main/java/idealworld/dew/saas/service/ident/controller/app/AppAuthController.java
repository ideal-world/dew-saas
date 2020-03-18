/*
 * Copyright 2019. the original author or authors.
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
import idealworld.dew.saas.service.ident.controller.BasicController;
import idealworld.dew.saas.service.ident.interceptor.AppHandlerInterceptor;
import idealworld.dew.saas.service.ident.service.PermissionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
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

    @GetMapping(value = "permission/sub")
    @ApiOperation(value = "订阅当前应用的权限信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "expireSec", value = "过期时间", paramType = "query", dataType = "long", required = true)
    })
    public Resp<String> subPermissions(@RequestParam(value = "expireSec") Long expireSec) {
        return permissionService.subPermissions(
                appHandlerInterceptor.getCurrentTenantAndAppId()._1, expireSec);
    }

    @DeleteMapping(value = "permission/sub")
    @ApiOperation(value = "取消当前应用的订阅权限信息", notes = "此操作会取消当前应用的所有实例订阅，在多实例场景下慎用")
    public Resp<Void> unSubPermission() {
        return permissionService.unSubPermission(appHandlerInterceptor.getCurrentTenantAndAppId()._1);
    }

}
