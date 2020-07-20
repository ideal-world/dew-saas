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
import idealworld.dew.saas.service.ident.dto.organization.AddOrganizationReq;
import idealworld.dew.saas.service.ident.dto.organization.ModifyOrganizationReq;
import idealworld.dew.saas.service.ident.dto.organization.OrganizationInfoResp;
import idealworld.dew.saas.service.ident.service.OrganizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 租户控制台机构管理操作.
 *
 * @author gudaoxuri
 */
@RestController
@Tag(name = "console organization", description = "租户控制台机构管理操作")
@RequestMapping(value = "/console/organization")
@Validated
public class ConsoleOrganizationController extends BasicController {

    @Autowired
    private OrganizationService organizationService;

    /**
     * 添加当前租户某个应用的机构.
     *
     * @param appId              the app id
     * @param addOrganizationReq the add organization req
     * @return the resp
     */
    @PostMapping(value = "{appId}")
    @Operation(summary = "添加当前租户某个应用的机构")
    public Resp<Long> addOrganization(@PathVariable Long appId,
                                      @Validated @RequestBody AddOrganizationReq addOrganizationReq) {
        return organizationService.AddOrganization(addOrganizationReq, appId, getCurrentTenantId());
    }

    /**
     * 获取当前租户某个应用的机构列表信息.
     *
     * @param appId the app id
     * @return the resp
     */
    @GetMapping(value = "{appId}")
    @Operation(summary = "获取当前租户某个应用的机构列表信息")
    public Resp<List<OrganizationInfoResp>> findOrganizationInfo(@PathVariable Long appId) {
        return organizationService.findOrganizationInfo(appId, getCurrentTenantId());
    }

    /**
     * 修改当前租户某个应用的某个机构.
     *
     * @param appId                 the app id
     * @param organizationId        the organization id
     * @param modifyOrganizationReq the modify organization req
     * @return the resp
     */
    @PatchMapping(value = "{appId}/{organizationId}")
    @Operation(summary = "修改当前租户某个应用的某个机构")
    public Resp<Void> modifyOrganization(@PathVariable Long appId,
                                         @PathVariable Long organizationId,
                                         @Validated @RequestBody ModifyOrganizationReq modifyOrganizationReq) {
        return organizationService.modifyOrganization(modifyOrganizationReq,
                organizationId, appId, getCurrentTenantId());
    }

    /**
     * 删除当前租户某个应用的某个机构.
     *
     * @param appId          the app id
     * @param organizationId the organization id
     * @return the resp
     */
    @DeleteMapping(value = "{appId}/{organizationId}")
    @Operation(summary = "删除当前租户某个应用的某个机构、关联的岗位、账号岗位、权限")
    public Resp<Long> deleteOrganization(@PathVariable Long appId, @PathVariable Long organizationId) {
        return organizationService.deleteOrganization(organizationId, appId, getCurrentTenantId());
    }

}
