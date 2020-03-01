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

package group.idealworld.dew.saas.service.ident;

import group.idealworld.dew.saas.service.ident.domain.CertAccount;
import group.idealworld.dew.saas.service.ident.dto.ModifyTenantReq;
import group.idealworld.dew.saas.service.ident.dto.RegisterTenantReq;
import group.idealworld.dew.saas.service.ident.dto.TenantInfoResp;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * The type tenant test.
 *
 * @author gudaoxuri
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = DewIdentApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TenantTest extends BasicTest {

    @Test
    public void testRegisterTenant() {
        var registerTenantReq = RegisterTenantReq.builder()
                .accountName("孤岛旭日")
                .certKind(CertAccount.Kind.USERNAME)
                .ak("gudaoxuri")
                .sk("pwd123")
                .tenantName("测试租户")
                .build();
        var newTenantId = postToEntity("/console/tenant", registerTenantReq, String.class).getBody();
        var modifyTenantReq = ModifyTenantReq.builder()
                .tenantName("测试租户1")
                .tenantIcon("/xx/xx")
                .build();
        putToEntity("/console/tenant/" + newTenantId, modifyTenantReq, Void.class);
        var tenantInfoResp = getToEntity("/console/tenant/" + newTenantId, TenantInfoResp.class);
        Assert.assertEquals("测试租户1", tenantInfoResp.getBody().getName());
    }

}
