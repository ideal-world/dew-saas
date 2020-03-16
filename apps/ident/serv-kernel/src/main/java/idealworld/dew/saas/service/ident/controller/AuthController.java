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

package idealworld.dew.saas.service.ident.controller;

import com.ecfront.dew.common.Resp;
import group.idealworld.dew.Dew;
import idealworld.dew.saas.common.service.dto.IdentOptInfo;
import idealworld.dew.saas.service.ident.dto.account.LoginReq;
import idealworld.dew.saas.service.ident.service.AccountService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author gudaoxuri
 */
@RestController
@Api(value = "认证操作", description = "认证操作")
@RequestMapping(value = "/auth")
@Validated
public class AuthController extends BasicController {

    @Autowired
    private AccountService accountService;

    @PostMapping(value = "{tenantId}/login")
    @ApiOperation(value = "用户登录")
    public Resp<IdentOptInfo> login(@PathVariable Long tenantId,
                                    @RequestBody LoginReq loginReq) {
        return accountService.login(loginReq, tenantId);
    }

    @DeleteMapping(value = "{tenantId}/logout")
    @ApiOperation(value = "注销登录")
    public Resp<Void> logout() {
        Dew.auth.getOptInfo().ifPresent(info -> {
            var accountId = Long.valueOf((String) info.getAccountCode());
            var token = info.getToken();
            accountService.logout(accountId, token);
        });
        return Resp.success(null);
    }


}
