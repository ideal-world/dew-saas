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

package idealworld.dew.saas.service.ident.controller.console;

import com.ecfront.dew.common.Resp;
import idealworld.dew.saas.service.ident.controller.BasicController;
import idealworld.dew.saas.service.ident.dto.permission.AddPermissionReq;
import idealworld.dew.saas.service.ident.dto.permission.PermissionInfoResp;
import idealworld.dew.saas.service.ident.service.PermissionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author gudaoxuri
 */
@RestController
@Api(value = "租户控制台权限管理操作", description = "租户控制台权限管理操作")
@RequestMapping(value = "/console/permission")
@Validated
public class ConsolePermissionController extends BasicController {

    @Autowired
    private PermissionService permissionService;

    @PostMapping(value = "{appId}")
    @ApiOperation(value = "添加当前租户某个应用的权限")
    public Resp<Long> addPermission(@PathVariable Long appId,
                                    @RequestBody AddPermissionReq addPermissionReq) {
        return permissionService.addPermission(addPermissionReq, appId, getCurrentTenantId());
    }

    @GetMapping(value = "{appId}")
    @ApiOperation(value = "获取当前租户某个应用的权限列表信息")
    public Resp<List<PermissionInfoResp>> findPermissionInfo(@PathVariable Long appId) {
        return permissionService.findPermissionInfo(appId, getCurrentTenantId());
    }

    @DeleteMapping(value = "{appId}/{permissionId}")
    @ApiOperation(value = "删除当前租户某个应用应用的某个权限")
    public Resp<Void> deletePermission(@PathVariable Long appId, @PathVariable Long permissionId) {
        return permissionService.deletePermission(permissionId, appId, getCurrentTenantId());
    }

}
