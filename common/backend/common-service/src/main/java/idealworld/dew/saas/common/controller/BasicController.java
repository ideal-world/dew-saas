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

package idealworld.dew.saas.common.controller;

import group.idealworld.dew.Dew;
import idealworld.dew.saas.common.resp.StandardResp;

/**
 * Basic controller.
 *
 * @author gudaoxuri
 */
public abstract class BasicController {

    /**
     * Gets current account id.
     *
     * @return the current account id
     */
    protected String getCurrentOpenId() {
        return Dew.auth.getOptInfo()
                .map(info -> (String) info.getAccountCode())
                .orElseThrow(() -> StandardResp.e(
                        StandardResp.unAuthorized("BASIC", "用户未登录")
                ));
    }

}
