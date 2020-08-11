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

import com.ecfront.dew.common.tuple.Tuple3;
import idealworld.dew.saas.common.Constant;
import idealworld.dew.saas.common.service.dto.IdentOptInfo;
import idealworld.dew.saas.service.ident.dto.account.*;
import idealworld.dew.saas.service.ident.dto.app.AddAppReq;
import idealworld.dew.saas.service.ident.dto.app.AppIdentInfoResp;
import idealworld.dew.saas.service.ident.dto.organization.AddOrganizationReq;
import idealworld.dew.saas.service.ident.dto.organization.ModifyOrganizationReq;
import idealworld.dew.saas.service.ident.dto.permission.AddPermissionReq;
import idealworld.dew.saas.service.ident.dto.position.AddPositionReq;
import idealworld.dew.saas.service.ident.dto.position.ModifyPositionReq;
import idealworld.dew.saas.service.ident.dto.post.AddPostReq;
import idealworld.dew.saas.service.ident.dto.resouce.AddResourceGroupReq;
import idealworld.dew.saas.service.ident.dto.resouce.AddResourceReq;
import idealworld.dew.saas.service.ident.dto.resouce.ModifyResourceReq;
import idealworld.dew.saas.service.ident.dto.tenant.RegisterTenantReq;
import idealworld.dew.saas.service.ident.enumeration.AccountIdentKind;
import idealworld.dew.saas.service.ident.enumeration.OrganizationKind;
import idealworld.dew.saas.service.ident.enumeration.ResourceKind;
import idealworld.dew.saas.service.ident.service.sdk.IdentProcessor;
import idealworld.dew.saas.service.ident.service.sdk.IdentSDK;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

/**
 * tenant test.
 *
 * @author gudaoxuri
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestIdentSDKApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class SDKTest extends BasicTest {

    @Autowired
    private IdentSDK sdk;

    @Autowired
    private IdentProcessor identProcessor;

    /**
     * Test sdk.
     *
     * @throws InterruptedException the interrupted exception
     */
    @Test
    public void testSDK() throws InterruptedException {
        // 租户注册
        var identOptInfo = postToEntity(sdk.getConfig().getIdent().getUrl() + "/tenant/register", RegisterTenantReq.builder()
                .accountName("孤岛旭日")
                .identKind(AccountIdentKind.USERNAME)
                .ak("gudaoxuri")
                .sk("pwd123")
                .tenantName("测试租户")
                .build(), IdentOptInfo.class).getBody();
        setIdentOptInfo(identOptInfo);
        var appId = postToEntity(sdk.getConfig().getIdent().getUrl() + "/console/app", AddAppReq.builder()
                .name("测试应用")
                .icon("")
                .build(), Long.class).getBody();
        var appIdent = getToList(sdk.getConfig().getIdent().getUrl() + "/console/app/" + appId + "/ident", AppIdentInfoResp.class).getBody().get(0);
        System.out.println("=====================\nAK:" + appIdent.getAk() + "\nSK:" + appIdent.getSk() + "\n=====================");

        sdk.getConfig().getBasic().setAppAk(appIdent.getAk());
        sdk.getConfig().getBasic().setAppSk(appIdent.getSk());
        sdk.init();
        identProcessor.doSub();
        // 等待2s用于建立mq连接
        Thread.sleep(2000);
        var orgId = testOrganization();
        var positionCode = testPosition();
        var postId = testPost(positionCode);
        var resId = testResource();
        var permissionId = testPermission(postId, resId);
        var identInfo = testAccount(postId);
        Thread.sleep(1000);
        testAuth(identOptInfo.getRelTenantId(), identInfo._0, identInfo._1, identInfo._2);
    }

    private Long testOrganization() {
        // 添加当前租户某个应用的机构
        var appRootOrgId = sdk.organization.addOrganization(AddOrganizationReq.builder()
                .kind(OrganizationKind.VIRTUAL)
                .code("org_x")
                .name("x应用")
                .parentId(Constant.OBJECT_UNDEFINED)
                .build()).getBody();
        sdk.organization.addOrganization(AddOrganizationReq.builder()
                .kind(OrganizationKind.VIRTUAL)
                .code("org_a")
                .name("A团队")
                .parentId(appRootOrgId)
                .build());
        var leafOrgId = sdk.organization.addOrganization(AddOrganizationReq.builder()
                .kind(OrganizationKind.VIRTUAL)
                .code("org_b")
                .name("B团队")
                .parentId(appRootOrgId)
                .build()).getBody();
        // 修改当前租户某个应用的某个机构
        sdk.organization.modifyOrganization(appRootOrgId, ModifyOrganizationReq.builder()
                .kind(OrganizationKind.VIRTUAL)
                .name("测试应用")
                .build());
        // 获取当前租户某个应用的机构列表信息
        var orgs = sdk.organization.findOrganizationInfo().getBody();
        Assert.assertEquals(3, orgs.size());
        Assert.assertTrue(orgs.stream().anyMatch(o -> o.getName().equals("测试应用")));
        // 删除当前租户某个应用的某个机构
        sdk.organization.deleteOrganization(leafOrgId);
        return appRootOrgId;
    }

    private String testPosition() {
        // 添加当前租户某个应用的职位
        var positionId = sdk.position.addPosition(AddPositionReq.builder()
                .code("USER")
                .name("普通用户")
                .build()).getBody();
        // 修改当前租户某个应用应用的某个职位
        sdk.position.modifyPosition(positionId, ModifyPositionReq.builder()
                .name("用户角色")
                .build());
        // 获取当前租户某个应用的职位列表信息
        var positions = sdk.position.findPositionInfo().getBody();
        Assert.assertEquals(1, positions.size());
        // 删除当前租户某个应用应用的某个职位
        sdk.position.deletePosition(positionId);
        // 重新添加当前租户某个应用的职位
        sdk.position.addPosition(AddPositionReq.builder()
                .code("USER")
                .name("普通用户")
                .build());
        return "USER";
    }

    private Long testPost(String positionCode) {
        // 添加当前租户某个应用的岗位
        var postId = sdk.post.addPost(AddPostReq.builder()
                .relPositionCode(positionCode)
                .build()).getBody();
        // 获取当前租户某个应用的岗位列表信息
        var posts = sdk.post.findPostInfo().getBody();
        Assert.assertEquals(1, posts.size());
        // 删除当前租户某个应用的某个岗位
        sdk.post.deletePost(postId);
        // 重新添加当前租户某个应用的岗位
        postId = sdk.post.addPost(AddPostReq.builder()
                .relPositionCode(positionCode)
                .build()).getBody();
        return postId;
    }

    private Long testResource() {
        // 添加当前租户某个应用的资源组
        var groupId = sdk.resource.addResourceGroup(AddResourceGroupReq.builder()
                .name("默认资源组")
                .build()).getBody();
        // 添加当前租户某个应用的资源
        var mgrResId = sdk.resource.addResource(AddResourceReq.builder()
                .kind(ResourceKind.URI)
                .identifier("/mgr/account/**")
                .method("*")
                .name("账号管理")
                .parentId(groupId)
                .build()).getBody();
        var resId = sdk.resource.addResource(AddResourceReq.builder()
                .kind(ResourceKind.URI)
                .identifier("/mgr/tenant/**")
                .method("GET")
                .name("租户列表")
                .parentId(groupId)
                .build()).getBody();
        // 修改当前租户某个应用的资源（组）
        sdk.resource.modifyResource(resId, ModifyResourceReq.builder()
                .name("获取租户列表")
                .build());
        // 获取当前租户某个应用的资源（组）列表信息
        var resources = sdk.resource.findResources().getBody();
        Assert.assertEquals(3, resources.size());
        var fetchTenantListResId = resources.stream()
                .filter(res -> res.getName().equalsIgnoreCase("获取租户列表"))
                .findAny().get().getId();
        // 获取当前租户某个应用的资源（组）信息
        var resource = sdk.resource.getResource(fetchTenantListResId).getBody();
        Assert.assertEquals("获取租户列表", resource.getName());
        // 删除当前租户某个应用的资源（组）
        sdk.resource.deleteResource(fetchTenantListResId);
        resources = sdk.resource.findResources().getBody();
        Assert.assertEquals(2, resources.size());
        return mgrResId;
    }

    private Long testPermission(Long postId, Long resId) {
        // 添加当前租户某个应用的权限
        var permissionId = sdk.permission.addPermission(AddPermissionReq.builder()
                .relPostId(postId)
                .relResourceId(resId)
                .build()).getBody();
        // 获取当前租户某个应用的权限列表信息
        var permissions = sdk.permission.findPermissionInfo().getBody();
        Assert.assertEquals(1, permissions.size());
        // 删除当前租户某个应用应用的某个权限
        sdk.permission.deletePermission(permissionId);
        // 重新添加当前租户某个应用的权限
        permissionId = sdk.permission.addPermission(AddPermissionReq.builder()
                .relPostId(postId)
                .relResourceId(resId)
                .build()).getBody();
        return permissionId;
    }

    private Tuple3<AccountIdentKind, String, String> testAccount(Long postId) {
        // 添加当前租户的账号
        var accountId = sdk.account.addAccount(AddAccountReq.builder()
                .name("测试用户")
                .identReq(AddAccountIdentReq.builder()
                        .kind(AccountIdentKind.USERNAME)
                        .ak("test1")
                        .sk("123")
                        .build())
                .postReq(AddAccountPostReq.builder()
                        .relPostId(postId)
                        .build())
                .build()).getBody();
        sdk.account.addAccount(AddAccountReq.builder()
                .name("测试用户2")
                .identReq(AddAccountIdentReq.builder()
                        .kind(AccountIdentKind.USERNAME)
                        .ak("test2")
                        .sk("123")
                        .build())
                .postReq(AddAccountPostReq.builder()
                        .relPostId(postId)
                        .build())
                .build());
        sdk.account.addAccount(AddAccountReq.builder()
                .name("测试用户3")
                .identReq(AddAccountIdentReq.builder()
                        .kind(AccountIdentKind.USERNAME)
                        .ak("test3")
                        .sk("123")
                        .build())
                .postReq(AddAccountPostReq.builder()
                        .relPostId(postId)
                        .build())
                .build());
        // 修改当前租户的某个账号
        sdk.account.modifyAccount(accountId, ModifyAccountReq.builder()
                .name("测试用户1")
                .build());
        // 获取当前租户的账号列表信息
        var accounts = sdk.account.findAccountInfo(1L, 2).getBody();
        Assert.assertEquals(4, accounts.getRecordTotal());
        Assert.assertEquals(2, accounts.getPageTotal());
        Assert.assertEquals("测试用户1", accounts.getObjects().get(1).getName());
        // 删除当前租户的某个账号
        sdk.account.deleteAccount(accountId);
        // 重新添加当前租户的账号
        accountId = sdk.account.addAccount(AddAccountReq.builder()
                .name("测试用户")
                .identReq(AddAccountIdentReq.builder()
                        .kind(AccountIdentKind.USERNAME)
                        .ak("test1")
                        .sk("123")
                        .build())
                .postReq(AddAccountPostReq.builder()
                        .relPostId(postId)
                        .build())
                .build()).getBody();
        // 获取当前租户的某个账号信息
        var account = sdk.account.getAccountInfo(accountId).getBody();
        Assert.assertEquals("测试用户", account.getName());
        // --------------------------------------------------------------
        // 添加当前租户某个账号的认证
        var accountIdentId = sdk.account.addAccountIdent(accountId, AddAccountIdentReq.builder()
                .kind(AccountIdentKind.USERNAME)
                .ak("test")
                .sk("123")
                .build()).getBody();
        // 修改当前租户某个账号的某个认证
        sdk.account.modifyAccountIdent(accountId, accountIdentId, ModifyAccountIdentReq.builder()
                .validEndTime(new Date(System.currentTimeMillis() + 100000L))
                .build());
        // 获取当前租户某个账号的认证列表信息
        var accountIdents = sdk.account.findAccountIdentInfo(accountId).getBody();
        Assert.assertEquals(2, accountIdents.size());
        // 删除当前租户某个账号的某个认证
        sdk.account.deleteAccountIdent(accountId, accountIdentId);
        // 删除当前租户某个账号的所有认证
        sdk.account.deleteAccountIdents(accountId);
        // 重新添加当前租户某个账号的认证
        accountIdentId = sdk.account.addAccountIdent(accountId, AddAccountIdentReq.builder()
                .kind(AccountIdentKind.USERNAME)
                .ak("test")
                .sk("123")
                .build()).getBody();
        // 获取当前租户某个账号的岗位列表信息
        var accountPosts = sdk.account.findAccountPostInfo(accountId).getBody();
        // 删除当前租户某个账号的某个岗位
        sdk.account.deleteAccountPost(accountId, accountPosts.get(0).getId());
        // 添加当前租户某个账号的岗位
        var accountPostId = sdk.account.addAccountPost(accountId, AddAccountPostReq.builder()
                .relPostId(postId)
                .build()).getBody();
        // 获取当前租户某个账号的岗位列表信息
        accountPosts = sdk.account.findAccountPostInfo(accountId).getBody();
        Assert.assertEquals(postId, accountPosts.get(0).getRelPostId());

        return new Tuple3<>(AccountIdentKind.USERNAME, "test", "123");
    }

    /**
     * Test auth.
     *
     * @param tenantId  the tenant id
     * @param identKind the ident kind
     * @param ak        the ak
     * @param sk        the sk
     */
    public void testAuth(Long tenantId, AccountIdentKind identKind, String ak, String sk) {
        var requestR = getToEntity("/mgr/account", Void.class);
        Assert.assertFalse(requestR.ok());
        // 登录
        var identOptInfo = postToEntity(sdk.getConfig().getIdent().getUrl() + "/auth/" + tenantId + "/login", LoginReq.builder()
                .identKind(identKind)
                .ak(ak)
                .sk(sk)
                .build(), IdentOptInfo.class).getBody();
        setIdentOptInfo(identOptInfo);

        requestR = getToEntity("/mgr/account", Void.class);
        Assert.assertTrue(requestR.ok());
        requestR = getToEntity("/mgr/account/11", Void.class);
        Assert.assertTrue(requestR.ok());

    }

}
