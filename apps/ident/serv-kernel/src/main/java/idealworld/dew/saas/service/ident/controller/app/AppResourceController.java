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
import idealworld.dew.saas.service.ident.dto.resouce.AddResourceGroupReq;
import idealworld.dew.saas.service.ident.dto.resouce.AddResourceReq;
import idealworld.dew.saas.service.ident.dto.resouce.ModifyResourceReq;
import idealworld.dew.saas.service.ident.dto.resouce.ResourceInfoResp;
import idealworld.dew.saas.service.ident.interceptor.AppHandlerInterceptor;
import idealworld.dew.saas.service.ident.service.ResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 应用控制台资源管理操.
 *
 * @author gudaoxuri
 */
@RestController
@Schema(name = "app resource", description = "应用控制台资源管理操作")
@RequestMapping(value = "/app/resource")
@Validated
public class AppResourceController extends BasicController {

    @Autowired
    private ResourceService resourceService;
    @Autowired
    private AppHandlerInterceptor appHandlerInterceptor;

    /**
     * 添加当前应用的资源组.
     *
     * @param addResourceGroupReq the add resource group req
     * @return the resp
     */
    @PostMapping(value = "group")
    @Operation(description = "添加当前应用的资源组")
    public Resp<Long> addResourceGroup(@Validated @RequestBody AddResourceGroupReq addResourceGroupReq) {
        return resourceService.addResourceGroup(addResourceGroupReq,
                appHandlerInterceptor.getCurrentTenantAndAppId()._1,
                appHandlerInterceptor.getCurrentTenantAndAppId()._0);
    }

    /**
     * 添加当前应用的资源.
     *
     * @param addResourceReq the add resource req
     * @return the resp
     */
    @PostMapping(value = "")
    @Operation(description = "添加当前应用的资源")
    public Resp<Long> addResource(@Validated @RequestBody AddResourceReq addResourceReq) {
        return resourceService.addResource(addResourceReq,
                appHandlerInterceptor.getCurrentTenantAndAppId()._1,
                appHandlerInterceptor.getCurrentTenantAndAppId()._0);
    }

    /**
     * 修改当前应用的某个资源（组）.
     *
     * @param resourceId        the resource id
     * @param modifyResourceReq the modify resource req
     * @return the resp
     */
    @PatchMapping(value = "{resourceId}")
    @Operation(description = "修改当前应用的某个资源（组）")
    public Resp<Void> modifyResource(@PathVariable Long resourceId,
                                     @Validated @RequestBody ModifyResourceReq modifyResourceReq) {
        return resourceService.modifyResource(modifyResourceReq, resourceId,
                appHandlerInterceptor.getCurrentTenantAndAppId()._1,
                appHandlerInterceptor.getCurrentTenantAndAppId()._0);
    }

    /**
     * 获取当前应用的某个资源（组）信息.
     *
     * @param resourceId the resource id
     * @return the resource
     */
    @GetMapping(value = "{resourceId}")
    @Operation(description = "获取当前应用的某个资源（组）信息")
    public Resp<ResourceInfoResp> getResource(@PathVariable Long resourceId) {
        return resourceService.getResource(resourceId,
                appHandlerInterceptor.getCurrentTenantAndAppId()._1,
                appHandlerInterceptor.getCurrentTenantAndAppId()._0);
    }

    /**
     * 获取当前应用的资源（组）列表信息.
     *
     * @return the resp
     */
    @GetMapping(value = "")
    @Operation(description = "获取当前应用的资源（组）列表信息")
    public Resp<List<ResourceInfoResp>> findResources() {
        return resourceService.findResources(
                appHandlerInterceptor.getCurrentTenantAndAppId()._1,
                appHandlerInterceptor.getCurrentTenantAndAppId()._0);
    }

    /**
     * 删除当前应用的某个资源（组）.
     *
     * @param resourceId the resource id
     * @return the resp
     */
    @DeleteMapping(value = "{resourceId}")
    @Operation(description = "删除当前应用的某个资源（组）、权限")
    public Resp<Void> deleteResource(@PathVariable Long resourceId) {
        return resourceService.deleteResource(resourceId,
                appHandlerInterceptor.getCurrentTenantAndAppId()._1,
                appHandlerInterceptor.getCurrentTenantAndAppId()._0);
    }

}
