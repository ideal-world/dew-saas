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
import idealworld.dew.saas.service.ident.dto.organization.AddOrganizationReq;
import idealworld.dew.saas.service.ident.dto.organization.ModifyOrganizationReq;
import idealworld.dew.saas.service.ident.dto.organization.OrganizationInfoResp;
import idealworld.dew.saas.service.ident.interceptor.AppHandlerInterceptor;
import idealworld.dew.saas.service.ident.service.OrganizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 应用控制台机构管理操作.
 *
 * @author gudaoxuri
 */
@RestController
@Schema(name = "app organization", description = "应用控制台机构管理操作")
@RequestMapping(value = "/app/organization")
@Validated
public class AppOrganizationController extends BasicController {

    @Autowired
    private OrganizationService organizationService;
    @Autowired
    private AppHandlerInterceptor appHandlerInterceptor;

    /**
     * 添加当前应用的机构.
     *
     * @param addOrganizationReq the add organization req
     * @return the resp
     */
    @PostMapping(value = "")
    @Operation(description = "添加当前应用的机构")
    public Resp<Long> addOrganization(@Validated @RequestBody AddOrganizationReq addOrganizationReq) {
        return organizationService.AddOrganization(addOrganizationReq,
                appHandlerInterceptor.getCurrentTenantAndAppId()._1,
                appHandlerInterceptor.getCurrentTenantAndAppId()._0);
    }

    /**
     * 获取当前应用的机构列表信息.
     *
     * @return the resp
     */
    @GetMapping(value = "")
    @Operation(description = "获取当前应用的机构列表信息")
    public Resp<List<OrganizationInfoResp>> findOrganizationInfo() {
        return organizationService.findOrganizationInfo(
                appHandlerInterceptor.getCurrentTenantAndAppId()._1,
                appHandlerInterceptor.getCurrentTenantAndAppId()._0);
    }

    /**
     * 修改当前应用的某个机构.
     *
     * @param organizationId        the organization id
     * @param modifyOrganizationReq the modify organization req
     * @return the resp
     */
    @PatchMapping(value = "{organizationId}")
    @Operation(description = "修改当前应用的某个机构")
    public Resp<Void> modifyOrganization(@PathVariable Long organizationId,
                                         @Validated @RequestBody ModifyOrganizationReq modifyOrganizationReq) {
        return organizationService.modifyOrganization(modifyOrganizationReq,
                organizationId,
                appHandlerInterceptor.getCurrentTenantAndAppId()._1,
                appHandlerInterceptor.getCurrentTenantAndAppId()._0);
    }

    /**
     * 删除当前应用的某个机构.
     *
     * @param organizationId the organization id
     * @return the resp
     */
    @DeleteMapping(value = "{organizationId}")
    @Operation(description = "删除当前应用的某个机构、关联的岗位、账号岗位、权限")
    public Resp<Long> deleteOrganization(@PathVariable Long organizationId) {
        return organizationService.deleteOrganization(organizationId,
                appHandlerInterceptor.getCurrentTenantAndAppId()._1,
                appHandlerInterceptor.getCurrentTenantAndAppId()._0);
    }

}
