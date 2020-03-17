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
import idealworld.dew.saas.service.ident.dto.resouce.AddResourceGroupReq;
import idealworld.dew.saas.service.ident.dto.resouce.AddResourceReq;
import idealworld.dew.saas.service.ident.dto.resouce.ModifyResourceReq;
import idealworld.dew.saas.service.ident.dto.resouce.ResourceInfoResp;
import idealworld.dew.saas.service.ident.interceptor.AppHandlerInterceptor;
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
@Api(value = "应用控制台资源管理操作", description = "应用控制台资源管理操作")
@RequestMapping(value = "/app/resource")
@Validated
public class AppResourceController extends BasicController {

    @Autowired
    private ResourceService resourceService;
    @Autowired
    private AppHandlerInterceptor appHandlerInterceptor;

    @PostMapping(value = "group")
    @ApiOperation(value = "添加当前应用的资源组")
    public Resp<Long> addResourceGroup(@RequestBody AddResourceGroupReq addResourceGroupReq) {
        return resourceService.addResourceGroup(addResourceGroupReq,
                appHandlerInterceptor.getCurrentTenantAndAppId()._1,
                appHandlerInterceptor.getCurrentTenantAndAppId()._0);
    }

    @PostMapping(value = "")
    @ApiOperation(value = "添加当前应用的资源")
    public Resp<Long> addResource(@RequestBody AddResourceReq addResourceReq) {
        return resourceService.addResource(addResourceReq,
                appHandlerInterceptor.getCurrentTenantAndAppId()._1,
                appHandlerInterceptor.getCurrentTenantAndAppId()._0);
    }

    @PutMapping(value = "{resourceId}")
    @ApiOperation(value = "修改当前应用的某个资源（组）")
    public Resp<Void> modifyResource(@PathVariable Long resourceId,
                                     @RequestBody ModifyResourceReq modifyResourceReq) {
        return resourceService.modifyResource(modifyResourceReq, resourceId,
                appHandlerInterceptor.getCurrentTenantAndAppId()._1,
                appHandlerInterceptor.getCurrentTenantAndAppId()._0);
    }

    @GetMapping(value = "{resourceId}")
    @ApiOperation(value = "获取当前应用的某个资源（组）信息")
    public Resp<ResourceInfoResp> getResource(@PathVariable Long resourceId) {
        return resourceService.getResource(resourceId,
                appHandlerInterceptor.getCurrentTenantAndAppId()._1,
                appHandlerInterceptor.getCurrentTenantAndAppId()._0);
    }

    @GetMapping(value = "")
    @ApiOperation(value = "获取当前应用的资源（组）列表信息")
    public Resp<List<ResourceInfoResp>> findResources() {
        return resourceService.findResources(
                appHandlerInterceptor.getCurrentTenantAndAppId()._1,
                appHandlerInterceptor.getCurrentTenantAndAppId()._0);
    }

    @DeleteMapping(value = "{resourceId}")
    @ApiOperation(value = "删除当前应用的某个资源（组）")
    public Resp<Void> deleteResource(@PathVariable Long resourceId) {
        return resourceService.deleteResource(resourceId,
                appHandlerInterceptor.getCurrentTenantAndAppId()._1,
                appHandlerInterceptor.getCurrentTenantAndAppId()._0);
    }

}
