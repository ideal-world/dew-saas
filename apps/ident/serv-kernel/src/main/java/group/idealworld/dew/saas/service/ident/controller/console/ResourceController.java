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

package group.idealworld.dew.saas.service.ident.controller.console;

import com.ecfront.dew.common.Resp;
import group.idealworld.dew.saas.service.ident.controller.BasicController;
import group.idealworld.dew.saas.service.ident.dto.resouce.AddResourceGroupReq;
import group.idealworld.dew.saas.service.ident.dto.resouce.AddResourceReq;
import group.idealworld.dew.saas.service.ident.dto.resouce.ModifyResourceReq;
import group.idealworld.dew.saas.service.ident.dto.resouce.ResourceInfoResp;
import group.idealworld.dew.saas.service.ident.service.ResourceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Ident controller.
 *
 * @author gudaoxuri
 */
@RestController
@Api(value = "资源管理操作")
@RequestMapping(value = "/console/resource")
@Validated
public class ResourceController extends BasicController {

    @Autowired
    private ResourceService resourceService;

    @PostMapping(value = "group")
    @ApiOperation(value = "添加一个当前应用的资源组")
    public Resp<Long> addResourceGroupByCurrentApp(@RequestBody AddResourceGroupReq addResourceGroupReq) {
        return resourceService.addResourceGroup(addResourceGroupReq, getCurrentAppId());
    }

    @PostMapping(value = "")
    @ApiOperation(value = "添加一个当前应用的资源")
    public Resp<Long> addResourceByCurrentApp(@RequestBody AddResourceReq addResourceReq) {
        return resourceService.addResource(addResourceReq, getCurrentAppId());
    }

    @PutMapping(value = "{resourceId}")
    @ApiOperation(value = "修改一个当前应用的资源（组）")
    public Resp<Void> modifyResourceByCurrentApp(@PathVariable Long resourceId, @RequestBody ModifyResourceReq modifyResourceReq) {
        modifyResourceReq.setId(resourceId);
        return resourceService.modifyResource(modifyResourceReq, getCurrentAppId());
    }

    @GetMapping(value = "{resourceId}")
    @ApiOperation(value = "获取一个当前应用的资源（组）")
    public Resp<ResourceInfoResp> getResourceByCurrentApp(@PathVariable Long resourceId) {
        return resourceService.getResource(resourceId, getCurrentAppId());
    }

    @GetMapping(value = "")
    @ApiOperation(value = "获取当前应用的所有资源（组）")
    public Resp<List<ResourceInfoResp>> findResourcesByCurrentApp() {
        return resourceService.findResources(getCurrentAppId());
    }

    @DeleteMapping(value = "{resourceId}")
    @ApiOperation(value = "删除一个当前应用的资源（组）")
    public Resp<Void> deleteResourceByCurrentApp(@PathVariable Long resourceId) {
        return resourceService.deleteResource(resourceId, getCurrentAppId());
    }

}
