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

package idealworld.dew.saas.service.ident.controller.app;

import com.ecfront.dew.common.Resp;
import idealworld.dew.saas.service.ident.controller.BasicController;
import idealworld.dew.saas.service.ident.dto.position.AddPositionReq;
import idealworld.dew.saas.service.ident.dto.position.ModifyPositionReq;
import idealworld.dew.saas.service.ident.dto.position.PositionInfoResp;
import idealworld.dew.saas.service.ident.interceptor.AppHandlerInterceptor;
import idealworld.dew.saas.service.ident.service.PositionService;
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
@Api(value = "应用控制台职位管理操作", description = "应用控制台职位管理操作")
@RequestMapping(value = "/app/position")
@Validated
public class AppPositionController extends BasicController {

    @Autowired
    private PositionService positionService;
    @Autowired
    private AppHandlerInterceptor appHandlerInterceptor;

    @PostMapping(value = "")
    @ApiOperation(value = "添加当前应用的职位")
    public Resp<Long> addPosition(@RequestBody AddPositionReq addPositionReq) {
        return positionService.addPosition(addPositionReq,
                appHandlerInterceptor.getCurrentTenantAndAppId()._1,
                appHandlerInterceptor.getCurrentTenantAndAppId()._0);
    }

    @GetMapping(value = "")
    @ApiOperation(value = "获取当前应用的职位列表信息")
    public Resp<List<PositionInfoResp>> findPositionInfo() {
        return positionService.findPositionInfo(
                appHandlerInterceptor.getCurrentTenantAndAppId()._1,
                appHandlerInterceptor.getCurrentTenantAndAppId()._0);
    }

    @PatchMapping(value = "/{positionId}")
    @ApiOperation(value = "修改当前应用的某个职位")
    public Resp<Void> modifyPosition(@PathVariable Long positionId,
                                     @RequestBody ModifyPositionReq modifyPositionReq) {
        return positionService.modifyPosition(modifyPositionReq, positionId,
                appHandlerInterceptor.getCurrentTenantAndAppId()._1,
                appHandlerInterceptor.getCurrentTenantAndAppId()._0);
    }

    @DeleteMapping(value = "/{positionId}")
    @ApiOperation(value = "删除当前应用的某个职位", notes = "删除职位，关联的岗位、账号岗位、权限")
    public Resp<Void> deletePosition(@PathVariable Long positionId) {
        return positionService.deletePosition(positionId,
                appHandlerInterceptor.getCurrentTenantAndAppId()._1,
                appHandlerInterceptor.getCurrentTenantAndAppId()._0);
    }

}
