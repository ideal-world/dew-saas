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
import idealworld.dew.saas.service.ident.dto.app.*;
import idealworld.dew.saas.service.ident.service.AppService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 租户控制台应用管理操作.
 *
 * @author gudaoxuri
 */
@RestController
@Schema(name = "console app", description = "租户控制台应用管理操作")
@RequestMapping(value = "/console/app")
@Validated
public class ConsoleAppController extends BasicController {

    @Autowired
    private AppService appService;

    /**
     * 添加当前租户的应用.
     *
     * @param addAppReq the add app req
     * @return the resp
     */
    @PostMapping(value = "")
    @Operation(description = "添加当前租户的应用")
    public Resp<Long> addApp(@Validated @RequestBody AddAppReq addAppReq) {
        return appService.addApp(addAppReq, getCurrentTenantId());
    }

    /**
     * 获取当前租户的应用列表信息.
     *
     * @return the resp
     */
    @GetMapping(value = "")
    @Operation(description = "获取当前租户的应用列表信息")
    public Resp<List<AppInfoResp>> findAppInfo() {
        return appService.findAppInfo(getCurrentTenantId());
    }

    /**
     * Modify app.
     *
     * @param appId        the app id
     * @param modifyAppReq the modify app req
     * @return the resp
     */
    @PatchMapping(value = "{appId}")
    @Operation(description = "修改当前租户的某个应用")
    public Resp<Void> modifyApp(@PathVariable Long appId,
                                @Validated @RequestBody ModifyAppReq modifyAppReq) {
        return appService.modifyApp(modifyAppReq, appId, getCurrentTenantId());
    }

    /**
     * 删除当前租户的某个应用.
     *
     * @param appId the app id
     * @return the resp
     */
    @DeleteMapping(value = "{appId}")
    @Operation(description = "删除当前租户的某个应用、应用认证、机构、职位、岗位、账号岗位、权限")
    public Resp<Void> deleteApp(@PathVariable Long appId) {
        return appService.deleteApp(appId, getCurrentTenantId());
    }

    // ========================== Ident ==============================

    /**
     * 添加当前租户某个应用的认证.
     *
     * @param appId          the app id
     * @param addAppIdentReq the add app ident req
     * @return the resp
     */
    @PostMapping(value = "{appId}/ident")
    @Operation(description = "添加当前租户某个应用的认证")
    public Resp<Long> addAppIdent(@PathVariable Long appId,
                                  @Validated @RequestBody AddAppIdentReq addAppIdentReq) {
        return appService.addAppIdent(addAppIdentReq, appId, getCurrentTenantId());
    }

    /**
     * 获取当前租户某个应用的认证列表信息.
     *
     * @param appId the app id
     * @return the resp
     */
    @GetMapping(value = "{appId}/ident")
    @Operation(description = "获取当前租户某个应用的认证列表信息")
    public Resp<List<AppIdentInfoResp>> findAppIdentInfo(@PathVariable Long appId) {
        return appService.findAppIdentInfo(appId, getCurrentTenantId());
    }

    /**
     * 修改当前租户某个应用的某个认证.
     *
     * @param appId             the app id
     * @param appIdentId        the app ident id
     * @param modifyAppIdentReq the modify app ident req
     * @return the resp
     */
    @PatchMapping(value = "{appId}/ident/{appIdentId}")
    @Operation(description = "修改当前租户某个应用的某个认证")
    public Resp<Void> modifyAppIdent(@PathVariable Long appId,
                                     @PathVariable Long appIdentId,
                                     @Validated @RequestBody ModifyAppIdentReq modifyAppIdentReq) {
        return appService.modifyAppIdent(modifyAppIdentReq, appIdentId, appId, getCurrentTenantId());
    }

    /**
     * 删除当前租户某个应用的某个认证.
     *
     * @param appId      the app id
     * @param appIdentId the app ident id
     * @return the resp
     */
    @DeleteMapping(value = "{appId}/ident/{appIdentId}")
    @Operation(description = "删除当前租户某个应用的某个认证")
    public Resp<Void> deleteAppIdent(@PathVariable Long appId,
                                     @PathVariable Long appIdentId) {
        return appService.deleteAppIdent(appIdentId, appId, getCurrentTenantId());
    }

    /**
     * 删除当前租户某个应用的所有认证.
     *
     * @param appId the app id
     * @return the resp
     */
    @DeleteMapping(value = "{appId}/ident")
    @Operation(description = "删除当前租户某个应用的所有认证")
    public Resp<Long> deleteAppIdents(@PathVariable Long appId) {
        return appService.deleteAppIdents(appId, getCurrentTenantId());
    }

}
