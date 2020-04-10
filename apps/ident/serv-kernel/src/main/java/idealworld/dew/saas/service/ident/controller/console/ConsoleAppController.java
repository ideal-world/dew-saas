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
import idealworld.dew.saas.service.ident.dto.app.*;
import idealworld.dew.saas.service.ident.service.AppService;
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
@Api(value = "租户控制台应用管理操作", description = "租户控制台应用管理操作")
@RequestMapping(value = "/console/app")
@Validated
public class ConsoleAppController extends BasicController {

    @Autowired
    private AppService appService;

    @PostMapping(value = "")
    @ApiOperation(value = "添加当前租户的应用")
    public Resp<Long> addApp(@RequestBody AddAppReq addAppReq) {
        return appService.addApp(addAppReq, getCurrentTenantId());
    }

    @GetMapping(value = "")
    @ApiOperation(value = "获取当前租户的应用列表信息")
    public Resp<List<AppInfoResp>> findAppInfo() {
        return appService.findAppInfo(getCurrentTenantId());
    }

    @PatchMapping(value = "{appId}")
    @ApiOperation(value = "修改当前租户的某个应用")
    public Resp<Void> modifyApp(@PathVariable Long appId,
                                @RequestBody ModifyAppReq modifyAppReq) {
        return appService.modifyApp(modifyAppReq, appId, getCurrentTenantId());
    }

    @DeleteMapping(value = "{appId}")
    @ApiOperation(value = "删除当前租户的某个应用", notes = "删除应用，关联的应用凭证")
    public Resp<Void> deleteApp(@PathVariable Long appId) {
        return appService.deleteApp(appId, getCurrentTenantId());
    }

    // ========================== Cert ==============================

    @PostMapping(value = "{appId}/cert")
    @ApiOperation(value = "添加当前租户某个应用的凭证")
    public Resp<Long> addAppCert(@PathVariable Long appId,
                                 @RequestBody AddAppCertReq addAppCertReq) {
        return appService.addAppCert(addAppCertReq, appId, getCurrentTenantId());
    }

    @GetMapping(value = "{appId}/cert")
    @ApiOperation(value = "获取当前租户某个应用的凭证列表信息")
    public Resp<List<AppCertInfoResp>> findAppCertInfo(@PathVariable Long appId) {
        return appService.findAppCertInfo(appId, getCurrentTenantId());
    }

    @PatchMapping(value = "{appId}/cert/{appCertId}")
    @ApiOperation(value = "修改当前租户某个应用的某个凭证")
    public Resp<Void> modifyAppCert(@PathVariable Long appId,
                                    @PathVariable Long appCertId,
                                    @RequestBody ModifyAppCertReq modifyAppCertReq) {
        return appService.modifyAppCert(modifyAppCertReq, appCertId, appId, getCurrentTenantId());
    }

    @DeleteMapping(value = "{appId}/cert/{appCertId}")
    @ApiOperation(value = "删除当前租户某个应用的某个凭证")
    public Resp<Void> deleteAppCert(@PathVariable Long appId,
                                    @PathVariable Long appCertId) {
        return appService.deleteAppCert(appCertId, appId, getCurrentTenantId());
    }

    @DeleteMapping(value = "{appId}/cert")
    @ApiOperation(value = "删除当前租户某个应用的所有凭证")
    public Resp<Void> deleteAppCerts(@PathVariable Long appId) {
        return appService.deleteAppCerts(appId, getCurrentTenantId());
    }

}
