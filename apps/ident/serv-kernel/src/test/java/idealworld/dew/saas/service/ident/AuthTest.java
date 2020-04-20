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

import com.ecfront.dew.common.$;
import idealworld.dew.saas.common.service.dto.IdentOptInfo;
import idealworld.dew.saas.service.ident.dto.account.LoginReq;
import idealworld.dew.saas.service.ident.dto.account.OAuthLoginReq;
import idealworld.dew.saas.service.ident.dto.app.AddAppReq;
import idealworld.dew.saas.service.ident.dto.tenant.AddTenantIdentReq;
import idealworld.dew.saas.service.ident.enumeration.AccountIdentKind;
import org.junit.Assert;
import org.springframework.stereotype.Component;

/**
 * auth test.
 *
 * @author gudaoxuri
 */
@Component
public class AuthTest extends BasicTest {

    public void testAll(Long tenantId) {
        testAuth(tenantId);
        // testOAuth(tenantId);
    }

    private void testAuth(Long tenantId) {
        // 登录为普通用户
        var identOptInfo = postToEntity("/auth/" + tenantId + "/login", LoginReq.builder()
                .identKind(globalUserIdentKind)
                .ak(globalUserAk)
                .sk(globalUserSk)
                .build(), IdentOptInfo.class).getBody();
        Assert.assertTrue(identOptInfo.getParameters().isEmpty());
        setIdentOptInfo(identOptInfo);
        // 测试：添加当前租户的应用：失败
        var addAppR = postToEntity("/console/app", AddAppReq.builder()
                .name("测试app1")
                .icon("")
                .build(), Long.class);
        Assert.assertFalse(addAppR.ok());

        // 登录为租户管理员
        identOptInfo = postToEntity("/auth/" + tenantId + "/login", LoginReq.builder()
                .identKind(globalTenantAdminIdentKind)
                .ak(globalTenantAdminAk)
                .sk(globalTenantAdminSk)
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

    private void testOAuth(Long tenantId) {
        var oauth = $.file.readAllByClassPath("oauth-info.secret", "UTF-8");
        var oauthJson = $.json.toJson(oauth);
        // 登录为租户管理员
        var identOptInfo = postToEntity("/auth/" + tenantId + "/login", LoginReq.builder()
                .identKind(globalTenantAdminIdentKind)
                .ak(globalTenantAdminAk)
                .sk(globalTenantAdminSk)
                .build(), IdentOptInfo.class).getBody();
        setIdentOptInfo(identOptInfo);
        // 添加当前租户的认证
        postToEntity("/console/tenant/ident", AddTenantIdentReq.builder()
                .kind(AccountIdentKind.WECHAT_MP)
                .oauthAk(oauthJson.get("wechat-mp").get("ak").asText())
                .oauthSk(oauthJson.get("wechat-mp").get("sk").asText())
                .build(), Long.class).getBody();

        // OAuth登录
        identOptInfo = postToEntity("/oauth/" + tenantId + "/login", OAuthLoginReq.builder()
                .identKind(AccountIdentKind.WECHAT_MP)
                .code(oauthJson.get("wechat-mp").get("code").asText())
                .build(), IdentOptInfo.class).getBody();
        setIdentOptInfo(identOptInfo);
        Assert.assertEquals("0-0-DEFAULT_ROLE", identOptInfo.getRoleInfo().iterator().next().getCode());
    }
}
