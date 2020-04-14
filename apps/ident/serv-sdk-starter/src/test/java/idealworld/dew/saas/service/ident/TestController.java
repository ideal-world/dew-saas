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

package idealworld.dew.saas.service.ident;

import com.ecfront.dew.common.Resp;
import idealworld.dew.saas.common.resp.StandardResp;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author gudaoxuri
 */
@RestController
@RequestMapping(value = "/mgr")
public class TestController {

    @GetMapping(value = "account")
    public Resp<Void> testPermission1() {
        return StandardResp.success(null);
    }

    @GetMapping(value = "account/{id}")
    public Resp<Void> testPermission2() {
        return StandardResp.success(null);
    }

}
