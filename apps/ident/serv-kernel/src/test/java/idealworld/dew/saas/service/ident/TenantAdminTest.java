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

import com.ecfront.dew.common.StandardCode;
import idealworld.dew.saas.common.Constant;
import idealworld.dew.saas.common.service.dto.IdentOptInfo;
import idealworld.dew.saas.service.ident.dto.account.*;
import idealworld.dew.saas.service.ident.dto.app.*;
import idealworld.dew.saas.service.ident.dto.organization.AddOrganizationReq;
import idealworld.dew.saas.service.ident.dto.organization.ModifyOrganizationReq;
import idealworld.dew.saas.service.ident.dto.organization.OrganizationInfoResp;
import idealworld.dew.saas.service.ident.dto.permission.AddPermissionReq;
import idealworld.dew.saas.service.ident.dto.permission.PermissionInfoResp;
import idealworld.dew.saas.service.ident.dto.position.AddPositionReq;
import idealworld.dew.saas.service.ident.dto.position.ModifyPositionReq;
import idealworld.dew.saas.service.ident.dto.position.PositionInfoResp;
import idealworld.dew.saas.service.ident.dto.post.AddPostReq;
import idealworld.dew.saas.service.ident.dto.post.PostInfoResp;
import idealworld.dew.saas.service.ident.dto.resouce.AddResourceGroupReq;
import idealworld.dew.saas.service.ident.dto.resouce.AddResourceReq;
import idealworld.dew.saas.service.ident.dto.resouce.ModifyResourceReq;
import idealworld.dew.saas.service.ident.dto.resouce.ResourceInfoResp;
import idealworld.dew.saas.service.ident.dto.tenant.*;
import idealworld.dew.saas.service.ident.enumeration.AccountCertKind;
import idealworld.dew.saas.service.ident.enumeration.OrganizationKind;
import idealworld.dew.saas.service.ident.enumeration.ResourceKind;
import org.junit.Assert;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * The type tenant test.
 *
 * @author gudaoxuri
 */
@Component
public class TenantAdminTest extends BasicTest {

    public Long testAll() {
        var tenantId = testTenant();
        var appId = testApp();
        var orgId = testOrganization(appId);
        var positionCode = testPosition(appId);
        var postId = testPost(appId, positionCode);
        var resId = testResource(appId);
        var permissionId = testPermission(appId, postId, resId);
        testAccount(postId);
        return tenantId;
    }

    private Long testTenant() {
        // 租户注册
        var identOptInfo = postToEntity("/tenant/register", RegisterTenantReq.builder()
                .accountName("孤岛旭日")
                .certKind(globalTenantAdminCertKind)
                .ak(globalTenantAdminAk)
                .sk(globalTenantAdminSk)
                .tenantName("测试租户")
                .build(), IdentOptInfo.class).getBody();
        setIdentOptInfo(identOptInfo);
        // 修改当前租户信息
        patchToEntity("/console/tenant", ModifyTenantReq.builder()
                .name("测试租户1")
                .icon("/xx/xx")
                .build(), Void.class);
        // 获取当前租户信息
        var tenantInfoR = getToEntity("/console/tenant", TenantInfoResp.class);
        Assert.assertEquals("测试租户1", tenantInfoR.getBody().getName());
        // 注销当前租户
        Assert.assertTrue(delete("/console/tenant").ok());
        // 重新注册租户
        identOptInfo = postToEntity("/tenant/register", RegisterTenantReq.builder()
                .accountName("孤岛旭日")
                .certKind(AccountCertKind.USERNAME)
                .ak("gudaoxuri")
                .sk("pwd123")
                .tenantName("测试租户")
                .build(), IdentOptInfo.class).getBody();
        setIdentOptInfo(identOptInfo);

        // 添加当前租户的凭证配置
        var tenantCertConfigId = postToEntity("/console/tenant/cert-config", AddTenantCertConfigReq.builder()
                .kind(AccountCertKind.PHONE)
                .build(), Long.class).getBody();
        // 获取当前租户的凭证列表信息
        var tenantCertConfig = getToList("/console/tenant/cert-config", TenantCertConfigInfoResp.class).getBody();
        Assert.assertEquals(2, tenantCertConfig.size());
        Assert.assertEquals(AccountCertKind.USERNAME, tenantCertConfig.get(0).getKind());
        // 修改当前租户的某个凭证配置
        patchToEntity("/console/tenant/cert-config/" + tenantCertConfig.get(0).getId(), ModifyTenantCertConfigReq.builder()
                .validRuleNote("至少4个字符，至少1个大写字母、小写字母、数字、特殊字符")
                .validRule("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[^\\w\\s]).{4,}$")
                .build(), Void.class);
        // 删除当前租户的某个凭证配置
        delete("/console/tenant/cert-config/" + tenantCertConfigId);

        return identOptInfo.getRelTenantId();
    }

    private Long testApp() {
        // 添加当前租户的应用
        var appId = postToEntity("/console/app", AddAppReq.builder()
                .name("测试应用")
                .icon("")
                .build(), Long.class).getBody();
        // 添加当前租户的应用：失败：应用名重复
        var addAppR = postToEntity("/console/app", AddAppReq.builder()
                .name("测试应用   ")
                .icon("")
                .build(), Long.class);
        Assert.assertFalse(addAppR.ok());
        // 修改当前租户的某个应用信息
        patchToEntity("/console/app/" + appId, ModifyAppReq.builder()
                .name("测试应用1")
                .icon("xxx")
                .build(), Void.class);
        // 获取当前租户的应用列表信息
        var apps = getToList("/console/app", AppInfoResp.class).getBody();
        Assert.assertEquals(1, apps.size());
        Assert.assertEquals("测试应用1", apps.get(0).getName());
        // 删除当前租户的某个应用
        delete("/console/app/" + appId);
        // 重新添加当前租户的应用
        appId = postToEntity("/console/app", AddAppReq.builder()
                .name("测试应用")
                .icon("")
                .build(), Long.class).getBody();
        // 添加当前租户某个应用的凭证
        var appCertId = postToEntity("/console/app/" + appId + "/cert", AddAppCertReq.builder()
                .note("临时凭证")
                .build(), Long.class).getBody();
        // 修改当前租户某个应用的某个凭证
        patchToEntity("/console/app/" + appId + "/cert/" + appCertId, ModifyAppCertReq.builder()
                .note("临时凭证1")
                .build(), Void.class);
        // 获取当前租户某个应用的凭证列表信息
        var appCerts = getToList("/console/app/" + appId + "/cert", AppCertInfoResp.class).getBody();
        Assert.assertEquals(2, appCerts.size());
        Assert.assertEquals("临时凭证1", appCerts.get(1).getNote());
        // 删除当前租户某个应用的某个凭证
        delete("/console/app/" + appId + "/cert/" + appCertId);
        // 删除当前租户某个应用的所有凭证
        delete("/console/app/" + appId + "/cert/");
        // 重新添加当前租户某个应用的凭证
        postToEntity("/console/app/" + appId + "/cert", AddAppCertReq.builder()
                .note("默认凭证")
                .build(), Long.class).getBody();
        return appId;
    }

    private Long testOrganization(Long appId) {
        // 添加当前租户某个应用的机构
        var appRootOrgId = postToEntity("/console/organization/" + appId, AddOrganizationReq.builder()
                .kind(OrganizationKind.VIRTUAL)
                .code("org_x")
                .name("x应用")
                .parentId(Constant.OBJECT_UNDEFINED)
                .build(), Long.class).getBody();
        postToEntity("/console/organization/" + appId, AddOrganizationReq.builder()
                .kind(OrganizationKind.VIRTUAL)
                .code("org_a")
                .name("A团队")
                .parentId(appRootOrgId)
                .build(), Long.class);
        var leafOrgId = postToEntity("/console/organization/" + appId, AddOrganizationReq.builder()
                .kind(OrganizationKind.VIRTUAL)
                .code("org_b")
                .name("B团队")
                .parentId(appRootOrgId)
                .build(), Long.class);
        // 修改当前租户某个应用的某个机构
        patchToEntity("/console/organization/" + appId + "/" + appRootOrgId, ModifyOrganizationReq.builder()
                .kind(OrganizationKind.VIRTUAL)
                .name("测试应用")
                .build(), Void.class);
        // 获取当前租户某个应用的机构列表信息
        var orgs = getToList("/console/organization/" + appId, OrganizationInfoResp.class).getBody();
        Assert.assertEquals(3, orgs.size());
        Assert.assertTrue(orgs.stream().anyMatch(o -> o.getName().equals("测试应用")));
        // 删除当前租户某个应用的某个机构
        delete("/console/organization/" + appId + "/" + leafOrgId);
        return appRootOrgId;
    }

    private String testPosition(Long appId) {
        // 添加当前租户某个应用的职位
        var positionId = postToEntity("/console/position/" + appId, AddPositionReq.builder()
                .code("USER")
                .name("普通用户")
                .build(), Long.class).getBody();
        // 修改当前租户某个应用应用的某个职位
        patchToEntity("/console/position/" + appId + "/" + positionId, ModifyPositionReq.builder()
                .name("用户角色")
                .build(), Void.class);
        // 获取当前租户某个应用的职位列表信息
        var positions = getToList("/console/position/" + appId, PositionInfoResp.class).getBody();
        Assert.assertEquals(1, positions.size());
        // 删除当前租户某个应用应用的某个职位
        delete("/console/position/" + appId + "/" + positionId);
        // 重新添加当前租户某个应用的职位
        postToEntity("/console/position/" + appId, AddPositionReq.builder()
                .code("USER")
                .name("普通用户")
                .build(), Long.class).getBody();
        return "USER";
    }

    private Long testPost(Long appId, String positionCode) {
        // 添加当前租户某个应用的岗位
        var postId = postToEntity("/console/post/" + appId, AddPostReq.builder()
                .relPositionCode(positionCode)
                .build(), Long.class).getBody();
        // 获取当前租户某个应用的岗位列表信息
        var posts = getToList("/console/post/" + appId, PostInfoResp.class).getBody();
        Assert.assertEquals(1, posts.size());
        // 删除当前租户某个应用的某个岗位
        delete("/console/post/" + appId + "/" + postId);
        // 重新添加当前租户某个应用的岗位
        postId = postToEntity("/console/post/" + appId, AddPostReq.builder()
                .relPositionCode(positionCode)
                .build(), Long.class).getBody();
        return postId;
    }

    private Long testResource(Long appId) {
        // 添加当前租户某个应用的资源组
        var groupId = postToEntity("/console/resource/" + appId + "/group", AddResourceGroupReq.builder()
                .name("默认资源组")
                .build(), Long.class).getBody();
        // 添加当前租户某个应用的资源
        var mgrResId = postToEntity("/console/resource/" + appId, AddResourceReq.builder()
                .kind(ResourceKind.URI)
                .identifier("/mgr/account/**")
                .method("*")
                .name("账号管理")
                .parentId(groupId)
                .build(), Long.class).getBody();
        var resId = postToEntity("/console/resource/" + appId, AddResourceReq.builder()
                .kind(ResourceKind.URI)
                .identifier("/mgr/tenant")
                .method("GET")
                .name("租户列表")
                .parentId(groupId)
                .build(), Long.class).getBody();
        // 修改当前租户某个应用的资源（组）
        patchToEntity("/console/resource/" + appId + "/" + resId, ModifyResourceReq.builder()
                .name("获取租户列表")
                .build(), Void.class);
        // 获取当前租户某个应用的资源（组）列表信息
        var resources = getToList("/console/resource/" + appId, ResourceInfoResp.class).getBody();
        Assert.assertEquals(3, resources.size());
        var fetchTenantListResId = resources.stream()
                .filter(res -> res.getName().equalsIgnoreCase("获取租户列表"))
                .findAny().get().getId();
        // 获取当前租户某个应用的资源（组）信息
        var resource = getToEntity("/console/resource/" + appId + "/" + fetchTenantListResId,
                ResourceInfoResp.class).getBody();
        Assert.assertEquals("获取租户列表", resource.getName());
        // 删除当前租户某个应用的资源（组）
        delete("/console/resource/" + appId + "/" + fetchTenantListResId);
        resources = getToList("/console/resource/" + appId, ResourceInfoResp.class).getBody();
        Assert.assertEquals(2, resources.size());
        return mgrResId;
    }

    private Long testPermission(Long appId, Long postId, Long resId) {
        // 添加当前租户某个应用的权限
        var permissionId = postToEntity("/console/permission/" + appId, AddPermissionReq.builder()
                .relPostId(postId)
                .relResourceId(resId)
                .build(), Long.class).getBody();
        // 获取当前租户某个应用的权限列表信息
        var permissions = getToList("/console/permission/" + appId, PermissionInfoResp.class).getBody();
        Assert.assertEquals(1, permissions.size());
        // 删除当前租户某个应用应用的某个权限
        delete("/console/permission/" + appId + "/" + permissionId);
        // 重新添加当前租户某个应用的权限
        permissionId = postToEntity("/console/permission/" + appId, AddPermissionReq.builder()
                .relPostId(postId)
                .relResourceId(resId)
                .build(), Long.class).getBody();
        return permissionId;
    }

    private void testAccount(Long postId) {
        // 添加当前租户的账号
        var addAccountR = postToEntity("/console/account", AddAccountReq.builder()
                .name("测试用户")
                .certReq(AddAccountCertReq.builder()
                        .kind(AccountCertKind.USERNAME)
                        .ak("test1")
                        .sk("123")
                        .build())
                .postReq(AddAccountPostReq.builder()
                        .relPostId(postId)
                        .build())
                .build(), Long.class);
        Assert.assertEquals(StandardCode.BAD_REQUEST.toString(), addAccountR.getCode());
        var accountId = postToEntity("/console/account", AddAccountReq.builder()
                .name("测试用户")
                .certReq(AddAccountCertReq.builder()
                        .kind(AccountCertKind.USERNAME)
                        .ak("test1")
                        .sk("Aa10#")
                        .build())
                .postReq(AddAccountPostReq.builder()
                        .relPostId(postId)
                        .build())
                .build(), Long.class).getBody();
        postToEntity("/console/account", AddAccountReq.builder()
                .name("测试用户2")
                .certReq(AddAccountCertReq.builder()
                        .kind(AccountCertKind.USERNAME)
                        .ak("test2")
                        .sk("Aa10#")
                        .build())
                .postReq(AddAccountPostReq.builder()
                        .relPostId(postId)
                        .build())
                .build(), Long.class).getBody();
        postToEntity("/console/account", AddAccountReq.builder()
                .name("测试用户3")
                .certReq(AddAccountCertReq.builder()
                        .kind(AccountCertKind.USERNAME)
                        .ak("test3")
                        .sk("Aa10#")
                        .build())
                .postReq(AddAccountPostReq.builder()
                        .relPostId(postId)
                        .build())
                .build(), Long.class).getBody();
        // 修改当前租户的某个账号
        patchToEntity("/console/account/" + accountId, ModifyAccountReq.builder()
                .name("测试用户1")
                .build(), Void.class);
        // 获取当前租户的账号列表信息
        var accounts = getToPage("/console/account", 1L, 2, AccountInfoResp.class).getBody();
        Assert.assertEquals(4, accounts.getRecordTotal());
        Assert.assertEquals(2, accounts.getPageTotal());
        Assert.assertEquals("测试用户1", accounts.getObjects().get(1).getName());
        // 删除当前租户的某个账号
        delete("/console/account/" + accountId);
        // 重新添加当前租户的账号
        accountId = postToEntity("/console/account", AddAccountReq.builder()
                .name("测试用户")
                .certReq(AddAccountCertReq.builder()
                        .kind(AccountCertKind.USERNAME)
                        .ak("test1")
                        .sk("Aa10#")
                        .build())
                .postReq(AddAccountPostReq.builder()
                        .relPostId(postId)
                        .build())
                .build(), Long.class).getBody();
        // 获取当前租户的某个账号信息
        var account = getToEntity("/console/account/" + accountId, AccountInfoResp.class).getBody();
        Assert.assertEquals("测试用户", account.getName());
        // --------------------------------------------------------------
        // 添加当前租户某个账号的凭证
        var accountCertId = postToEntity("/console/account/" + accountId + "/cert", AddAccountCertReq.builder()
                .kind(AccountCertKind.USERNAME)
                .ak("test")
                .sk("Aa10#")
                .build(), Long.class).getBody();
        // 修改当前租户某个账号的某个凭证
        patchToEntity("/console/account/" + accountId + "/cert/" + accountCertId, ModifyAccountCertReq.builder()
                .validTime(new Date(System.currentTimeMillis() + 100000L))
                .build(), Void.class);
        // 获取当前租户某个账号的凭证列表信息
        var accountCerts = getToList("/console/account/" + accountId + "/cert", AccountCertInfoResp.class).getBody();
        Assert.assertEquals(2, accountCerts.size());
        // 删除当前租户某个账号的某个凭证
        delete("/console/account/" + accountId + "/cert/" + accountCertId);
        // 删除当前租户某个账号的所有凭证
        delete("/console/account/" + accountId + "/cert");
        // 重新添加当前租户某个账号的凭证
        accountCertId = postToEntity("/console/account/" + accountId + "/cert", AddAccountCertReq.builder()
                .kind(AccountCertKind.USERNAME)
                .ak(globalUserAk)
                .sk(globalUserSk)
                .build(), Long.class).getBody();
        // 获取当前租户某个账号的岗位列表信息
        var accountPosts = getToList("/console/account/" + accountId + "/post", AccountPostInfoResp.class).getBody();
        // 删除当前租户某个账号的某个岗位
        delete("/console/account/" + accountId + "/post/" + accountPosts.get(0).getId());
        // 添加当前租户某个账号的岗位
        var accountPostId = postToEntity("/console/account/" + accountId + "/post", AddAccountPostReq.builder()
                .relPostId(postId)
                .build(), Long.class).getBody();
        // 获取当前租户某个账号的岗位列表信息
        accountPosts = getToList("/console/account/" + accountId + "/post", AccountPostInfoResp.class).getBody();
        Assert.assertEquals(postId, accountPosts.get(0).getRelPostId());
    }
}
