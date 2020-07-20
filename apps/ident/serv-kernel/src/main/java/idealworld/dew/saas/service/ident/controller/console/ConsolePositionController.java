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
import idealworld.dew.saas.service.ident.dto.position.AddPositionReq;
import idealworld.dew.saas.service.ident.dto.position.ModifyPositionReq;
import idealworld.dew.saas.service.ident.dto.position.PositionInfoResp;
import idealworld.dew.saas.service.ident.service.PositionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 租户控制台职位管理操作.
 *
 * @author gudaoxuri
 */
@RestController
@Tag(name = "console position", description = "租户控制台职位管理操作")
@RequestMapping(value = "/console/position")
@Validated
public class ConsolePositionController extends BasicController {

    @Autowired
    private PositionService positionService;

    /**
     * 添加当前租户某个应用的职位.
     *
     * @param appId          the app id
     * @param addPositionReq the add position req
     * @return the resp
     */
    @PostMapping(value = "{appId}")
    @Operation(summary = "添加当前租户某个应用的职位")
    public Resp<Long> addPosition(@PathVariable Long appId,
                                  @Validated @RequestBody AddPositionReq addPositionReq) {
        return positionService.addPosition(addPositionReq, appId, getCurrentTenantId());
    }

    /**
     * 获取当前租户某个应用的职位列表信息.
     *
     * @param appId the app id
     * @return the resp
     */
    @GetMapping(value = "{appId}")
    @Operation(summary = "获取当前租户某个应用的职位列表信息")
    public Resp<List<PositionInfoResp>> findPositionInfo(@PathVariable Long appId) {
        return positionService.findPositionInfo(appId, getCurrentTenantId());
    }

    /**
     * 修改当前租户某个应用的某个职位.
     *
     * @param appId             the app id
     * @param positionId        the position id
     * @param modifyPositionReq the modify position req
     * @return the resp
     */
    @PatchMapping(value = "{appId}/{positionId}")
    @Operation(summary = "修改当前租户某个应用的某个职位")
    public Resp<Void> modifyPosition(@PathVariable Long appId,
                                     @PathVariable Long positionId,
                                     @Validated @RequestBody ModifyPositionReq modifyPositionReq) {
        return positionService.modifyPosition(modifyPositionReq, positionId, appId, getCurrentTenantId());
    }

    /**
     * 删除当前租户某个应用的某个职位.
     *
     * @param appId      the app id
     * @param positionId the position id
     * @return the resp
     */
    @DeleteMapping(value = "{appId}/{positionId}")
    @Operation(summary = "删除当前租户某个应用的某个职位、关联的岗位、账号岗位、权限")
    public Resp<Void> deletePosition(@PathVariable Long appId, @PathVariable Long positionId) {
        return positionService.deletePosition(positionId, appId, getCurrentTenantId());
    }

}
