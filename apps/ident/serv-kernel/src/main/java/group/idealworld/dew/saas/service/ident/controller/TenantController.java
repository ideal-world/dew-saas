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

package group.idealworld.dew.saas.service.ident.controller;

import com.ecfront.dew.common.Resp;
import group.idealworld.dew.saas.service.ident.dto.ModifyTenantReq;
import group.idealworld.dew.saas.service.ident.dto.RegisterTenantReq;
import group.idealworld.dew.saas.service.ident.dto.TenantInfoResp;
import group.idealworld.dew.saas.service.ident.service.TenantService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Ident controller.
 *
 * @author gudaoxuri
 */
@RestController
@Api(value = "租户管理操作")
@RequestMapping(value = "/console")
@Validated
public class TenantController {

    @Autowired
    private TenantService tenantService;

    @PostMapping(value = "tenant")
    @ApiOperation(value = "注册租户")
    public Resp<Long> registerTenant(@RequestBody RegisterTenantReq request) {
       return tenantService.registerTenant(request);
    }

    @GetMapping(value = "tenant/{tenantId}")
    @ApiOperation(value = "获取租户信息")
    public Resp<TenantInfoResp> getTenant(@PathVariable Long tenantId) {
        return tenantService.getTenantInfo(tenantId);
    }

    @PutMapping(value = "tenant/{tenantId}")
    @ApiOperation(value = "修改租户信息")
    public Resp<Void> modifyTenant(@PathVariable Long tenantId, @RequestBody ModifyTenantReq request) {
        return tenantService.modifyTenant(tenantId,request);
    }

    @DeleteMapping(value = "tenant/{tenantId}")
    @ApiOperation(value = "注销租户")
    public Resp<Void> unRegisterTenant(@PathVariable Long tenantId) {
        return tenantService.unRegisterTenant(tenantId);
    }

    public void addApp() {

    }

    public void getApps() {

    }

    public void getApp() {

    }

    public void modifyApp() {

    }

    public void deleteApp() {

    }

}
