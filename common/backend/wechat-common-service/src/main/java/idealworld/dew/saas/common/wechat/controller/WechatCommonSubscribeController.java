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

package idealworld.dew.saas.common.wechat.controller;

import com.ecfront.dew.common.Resp;
import idealworld.dew.saas.common.controller.BasicController;
import idealworld.dew.saas.common.wechat.dto.SubscribeReq;
import idealworld.dew.saas.common.wechat.service.WechatCommonSubscribeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Subscribe controller.
 *
 * @author gudaoxuri
 */
@RestController
@Api(value = "订阅操作", description = "订阅操作")
@RequestMapping(value = "/subscribe")
@Validated
public class WechatCommonSubscribeController extends BasicController {

    /**
     * The Subscribe service.
     */
    @Autowired
    private WechatCommonSubscribeService wechatCommonSubscribeService;

    /**
     * Counter.
     *
     * @param templateId the template id
     * @return the resp
     */
    @GetMapping(value = "counter")
    @ApiOperation(value = "获取当前可订阅的数量")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "templateId", value = "模板Id", paramType = "query", dataType = "string", required = true)
    })
    public Resp<Integer> counter(@RequestParam(value = "templateId") String templateId) {
        return wechatCommonSubscribeService.counter(templateId, getCurrentOpenId());
    }

    /**
     * Request subscribe message.
     *
     * @param subscribeReq the subscribe req
     * @return the resp
     */
    @PostMapping(value = "req")
    @ApiOperation(value = "订阅消息")
    public Resp<Integer> requestSubscribeMessage(@Validated @RequestBody SubscribeReq subscribeReq) {
        return wechatCommonSubscribeService.requestSubscribeMessage(subscribeReq, getCurrentOpenId());
    }

}
