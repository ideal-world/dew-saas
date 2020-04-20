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
 * 租户控制台租户管理操作.
 *
 * @author gudaoxuri
 */
@RestController
@Api(value = "租户控制台租户管理操作", description = "租户控制台租户管理操作")
@RequestMapping(value = "/console/tenant")
@Validated
public class ConsoleTenantController extends BasicController {

    @Autowired
    private TenantService tenantService;

    /**
     * 获取当前的租户信息.
     *
     * @return the tenant
     */
    @GetMapping(value = "")
    @ApiOperation(value = "获取当前的租户信息")
    public Resp<TenantInfoResp> getTenant() {
        return tenantService.getTenantInfo(getCurrentTenantId());
    }

    /**
     * 修改当前的租户.
     *
     * @param modifyTenantReq the modify tenant req
     * @return the resp
     */
    @PatchMapping(value = "")
    @ApiOperation(value = "修改当前的租户")
    public Resp<Void> modifyTenant(@Validated @RequestBody ModifyTenantReq modifyTenantReq) {
        return tenantService.modifyTenant(modifyTenantReq, getCurrentTenantId());
    }

    /**
     * 注销当前的租户.
     *
     * @return the resp
     */
    @DeleteMapping(value = "")
    @ApiOperation(value = "注销当前的租户", notes = "必须先删除关联的应用，此操作会同时与此租户相关的所有数据")
    public Resp<Void> unRegisterTenant() {
        return tenantService.unRegisterTenant(getCurrentTenantId());
    }

    // ========================== Ident ==============================

    /**
     * 添加当前租户的认证.
     *
     * @param addTenantIdentReq the add tenant ident req
     * @return the resp
     */
    @PostMapping(value = "ident")
    @ApiOperation(value = "添加当前租户的认证")
    public Resp<Long> addTenantIdent(@Validated @RequestBody AddTenantIdentReq addTenantIdentReq) {
        return tenantService.addTenantIdent(addTenantIdentReq, getCurrentTenantId());
    }

    /**
     * 获取当前租户的认证列表信息.
     *
     * @return the resp
     */
    @GetMapping(value = "ident")
    @ApiOperation(value = "获取当前租户的认证列表信息")
    public Resp<List<TenantIdentInfoResp>> findTenantIdentInfo() {
        return tenantService.findTenantIdentInfo(getCurrentTenantId());
    }

    /**
     * 修改当前租户的某个认证.
     *
     * @param tenantIdentId        the tenant ident id
     * @param modifyTenantIdentReq the modify tenant ident req
     * @return the resp
     */
    @PatchMapping(value = "ident/{tenantIdentId}")
    @ApiOperation(value = "修改当前租户的某个认证")
    public Resp<Void> modifyTenantIdent(@PathVariable Long tenantIdentId,
                                        @Validated @RequestBody ModifyTenantIdentReq modifyTenantIdentReq) {
        return tenantService.modifyTenantIdent(modifyTenantIdentReq, tenantIdentId, getCurrentTenantId());
    }

    /**
     * 删除当前租户的某个认证.
     *
     * @param tenantIdentId the tenant ident id
     * @return the resp
     */
    @DeleteMapping(value = "ident/{tenantIdentId}")
    @ApiOperation(value = "删除当前租户的某个认证")
    public Resp<Void> deleteTenantIdent(@PathVariable Long tenantIdentId) {
        return tenantService.deleteTenantIdent(tenantIdentId, getCurrentTenantId());
    }

    /**
     * 删除当前租户的所有认证.
     *
     * @return the resp
     */
    @DeleteMapping(value = "ident")
    @ApiOperation(value = "删除当前租户的所有认证")
    public Resp<Long> deleteTenantIdent() {
        return tenantService.deleteTenantIdent(getCurrentTenantId());
    }

}
