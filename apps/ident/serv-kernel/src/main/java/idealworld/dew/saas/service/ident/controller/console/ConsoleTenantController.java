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
import idealworld.dew.saas.service.ident.dto.tenant.*;
import idealworld.dew.saas.service.ident.service.TenantService;
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
@Api(value = "租户控制台租户管理操作", description = "租户控制台租户管理操作")
@RequestMapping(value = "/console/tenant")
@Validated
public class ConsoleTenantController extends BasicController {

    @Autowired
    private TenantService tenantService;

    @GetMapping(value = "")
    @ApiOperation(value = "获取当前的租户信息")
    public Resp<TenantInfoResp> getTenant() {
        return tenantService.getTenantInfo(getCurrentTenantId());
    }

    @PatchMapping(value = "")
    @ApiOperation(value = "修改当前的租户")
    public Resp<Void> modifyTenant(@RequestBody ModifyTenantReq modifyTenantReq) {
        return tenantService.modifyTenant(modifyTenantReq, getCurrentTenantId());
    }

    @DeleteMapping(value = "")
    @ApiOperation(value = "注销当前的租户", notes = "必须先删除关联的应用，此操作会同时与此租户相关的所有数据")
    public Resp<Void> unRegisterTenant() {
        return tenantService.unRegisterTenant(getCurrentTenantId());
    }

    // ========================== Cert ==============================

    @PostMapping(value = "cert-config")
    @ApiOperation(value = "添加当前租户的凭证配置")
    public Resp<Long> addTenantCertConfig(@RequestBody AddTenantCertConfigReq addTenantCertConfigReq) {
        return tenantService.addTenantCertConfig(addTenantCertConfigReq, getCurrentTenantId());
    }

    @GetMapping(value = "cert-config")
    @ApiOperation(value = "获取当前租户的凭证配置列表信息")
    public Resp<List<TenantCertConfigInfoResp>> findTenantCertConfigInfo() {
        return tenantService.findTenantCertConfigInfo(getCurrentTenantId());
    }

    @PatchMapping(value = "cert-config/{tenantCertConfigId}")
    @ApiOperation(value = "修改当前租户的某个凭证配置")
    public Resp<Void> modifyTenantCertConfig(@PathVariable Long tenantCertConfigId,
                                             @RequestBody ModifyTenantCertConfigReq modifyTenantCertConfigReq) {
        return tenantService.modifyTenantCertConfig(modifyTenantCertConfigReq, tenantCertConfigId, getCurrentTenantId());
    }

    @DeleteMapping(value = "cert-config/{tenantCertConfigId}")
    @ApiOperation(value = "删除当前租户的某个凭证配置")
    public Resp<Void> deleteTenantCertConfig(@PathVariable Long tenantCertConfigId) {
        return tenantService.deleteTenantCertConfig(tenantCertConfigId, getCurrentTenantId());
    }

    @DeleteMapping(value = "cert-config")
    @ApiOperation(value = "删除当前租户的所有凭证配置")
    public Resp<Long> deleteTenantCertConfig() {
        return tenantService.deleteTenantCertConfig(getCurrentTenantId());
    }

}
