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
import idealworld.dew.saas.service.ident.controller.BasicController;
import idealworld.dew.saas.service.ident.dto.permission.AddPermissionReq;
import idealworld.dew.saas.service.ident.dto.permission.PermissionInfoResp;
import idealworld.dew.saas.service.ident.interceptor.AppHandlerInterceptor;
import idealworld.dew.saas.service.ident.service.PermissionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 应用控制台权限管理操作.
 *
 * @author gudaoxuri
 */
@RestController
@Api(value = "应用控制台权限管理操作", description = "应用控制台权限管理操作")
@RequestMapping(value = "/app/permission")
@Validated
public class AppPermissionController extends BasicController {

    @Autowired
    private PermissionService permissionService;
    @Autowired
    private AppHandlerInterceptor appHandlerInterceptor;

    /**
     * 添加当前应用的权限.
     *
     * @param addPermissionReq the add permission req
     * @return the resp
     */
    @PostMapping(value = "")
    @ApiOperation(value = "添加当前应用的权限")
    public Resp<Long> addPermission(@Validated @RequestBody AddPermissionReq addPermissionReq) {
        return permissionService.addPermission(addPermissionReq,
                appHandlerInterceptor.getCurrentTenantAndAppId()._1,
                appHandlerInterceptor.getCurrentTenantAndAppId()._0);
    }

    /**
     * 获取当前应用的权限列表信息.
     *
     * @return the resp
     */
    @GetMapping(value = "")
    @ApiOperation(value = "获取当前应用的权限列表信息")
    public Resp<List<PermissionInfoResp>> findPermissionInfo() {
        return permissionService.findPermissionInfo(
                appHandlerInterceptor.getCurrentTenantAndAppId()._1,
                appHandlerInterceptor.getCurrentTenantAndAppId()._0);
    }

    /**
     * 删除当前应用的某个权限.
     *
     * @param permissionId the permission id
     * @return the resp
     */
    @DeleteMapping(value = "{permissionId}")
    @ApiOperation(value = "删除当前应用的某个权限")
    public Resp<Void> deletePermission(@PathVariable Long permissionId) {
        return permissionService.deletePermission(permissionId,
                appHandlerInterceptor.getCurrentTenantAndAppId()._1,
                appHandlerInterceptor.getCurrentTenantAndAppId()._0);
    }

}
