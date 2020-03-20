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

package idealworld.dew.saas.service.ident;

import idealworld.dew.saas.common.service.dto.IdentOptInfo;
import idealworld.dew.saas.service.ident.dto.account.LoginReq;
import idealworld.dew.saas.service.ident.dto.app.AddAppReq;
import idealworld.dew.saas.service.ident.enumeration.AccountCertKind;
import org.junit.Assert;
import org.springframework.stereotype.Component;

/**
 * The type auth test.
 *
 * @author gudaoxuri
 */
@Component
public class AuthTest extends BasicTest {

    public void testAll(Long tenantId, AccountCertKind certKind, String ak, String sk) {
        // 登录为普通用户
        var identOptInfo = postToEntity("/auth/" + tenantId + "/login", LoginReq.builder()
                .certKind(certKind)
                .ak(ak)
                .sk(sk)
                .build(), IdentOptInfo.class).getBody();
        setIdentOptInfo(identOptInfo);
        // 测试：添加当前租户的应用：失败
        var addAppR = postToEntity("/console/app", AddAppReq.builder()
                .name("测试app1")
                .icon("")
                .build(), Long.class);
        Assert.assertFalse(addAppR.ok());

        // 登录为租户管理员
         identOptInfo = postToEntity("/auth/" + tenantId + "/login", LoginReq.builder()
                .certKind(certKind)
                .ak("gudaoxuri")
                .sk("pwd123")
                .build(), IdentOptInfo.class).getBody();
        setIdentOptInfo(identOptInfo);
        // 测试：添加当前租户的应用：成功
         addAppR = postToEntity("/console/app", AddAppReq.builder()
                .name("测试app1")
                .icon("")
                .build(), Long.class);
        Assert.assertTrue(addAppR.ok());

        // 注销
        delete("/auth/" + tenantId + "/logout");
        // 测试：添加当前租户的应用：失败
        addAppR = postToEntity("/console/app", AddAppReq.builder()
                .name("测试app2")
                .icon("")
                .build(), Long.class);
        Assert.assertFalse(addAppR.ok());
    }

}
