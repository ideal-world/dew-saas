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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Subscribe controller.
 *
 * @author gudaoxuri
 */
@RestController
@Schema(name = "subscribe", description = "订阅操作")
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
    @Operation(summary = "获取当前可订阅的数量")
    public Resp<Integer> counter(
            @Parameter(name = "templateId", description = "模板Id", in = ParameterIn.QUERY, required = true)
            @RequestParam(value = "templateId") String templateId) {
        return wechatCommonSubscribeService.counter(templateId, getCurrentOpenId());
    }

    /**
     * Request subscribe message.
     *
     * @param subscribeReq the subscribe req
     * @return the resp
     */
    @PostMapping(value = "req")
    @Operation(summary = "订阅消息")
    public Resp<Integer> requestSubscribeMessage(@Validated @RequestBody SubscribeReq subscribeReq) {
        return wechatCommonSubscribeService.requestSubscribeMessage(subscribeReq, getCurrentOpenId());
    }

}
