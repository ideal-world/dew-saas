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
import idealworld.dew.saas.service.ident.dto.resouce.AddResourceGroupReq;
import idealworld.dew.saas.service.ident.dto.resouce.AddResourceReq;
import idealworld.dew.saas.service.ident.dto.resouce.ModifyResourceReq;
import idealworld.dew.saas.service.ident.dto.resouce.ResourceInfoResp;
import idealworld.dew.saas.service.ident.service.ResourceService;
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
@Api(value = "租户控制台资源管理操作", description = "租户控制台资源管理操作")
@RequestMapping(value = "/console/resource")
@Validated
public class ConsoleResourceController extends BasicController {

    @Autowired
    private ResourceService resourceService;

    @PostMapping(value = "{appId}/group")
    @ApiOperation(value = "添加当前租户某个应用的资源组")
    public Resp<Long> addResourceGroup(@PathVariable Long appId,
                                       @RequestBody AddResourceGroupReq addResourceGroupReq) {
        return resourceService.addResourceGroup(addResourceGroupReq, appId, getCurrentTenantId());
    }

    @PostMapping(value = "{appId}")
    @ApiOperation(value = "添加当前租户某个应用的资源")
    public Resp<Long> addResource(@PathVariable Long appId,
                                  @RequestBody AddResourceReq addResourceReq) {
        return resourceService.addResource(addResourceReq, appId, getCurrentTenantId());
    }

    @PutMapping(value = "{appId}/{resourceId}")
    @ApiOperation(value = "修改当前租户某个应用的资源（组）")
    public Resp<Void> modifyResource(@PathVariable Long appId,
                                     @PathVariable Long resourceId,
                                     @RequestBody ModifyResourceReq modifyResourceReq) {
        return resourceService.modifyResource(modifyResourceReq, resourceId, appId, getCurrentTenantId());
    }

    @GetMapping(value = "{appId}/{resourceId}")
    @ApiOperation(value = "获取当前租户某个应用的资源（组）信息")
    public Resp<ResourceInfoResp> getResource(@PathVariable Long appId,
                                              @PathVariable Long resourceId) {
        return resourceService.getResource(resourceId, appId, getCurrentTenantId());
    }

    @GetMapping(value = "{appId}")
    @ApiOperation(value = "获取当前租户某个应用的资源（组）列表信息")
    public Resp<List<ResourceInfoResp>> findResources(@PathVariable Long appId) {
        return resourceService.findResources(appId, getCurrentTenantId());
    }

    @DeleteMapping(value = "{appId}/{resourceId}")
    @ApiOperation(value = "删除当前租户某个应用的资源（组）")
    public Resp<Void> deleteResource(@PathVariable Long appId, @PathVariable Long resourceId) {
        return resourceService.deleteResource(resourceId, appId, getCurrentTenantId());
    }

}
