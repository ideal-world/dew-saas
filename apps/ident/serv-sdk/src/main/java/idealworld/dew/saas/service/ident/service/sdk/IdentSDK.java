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

package idealworld.dew.saas.service.ident.service.sdk;

import com.ecfront.dew.common.$;
import com.ecfront.dew.common.Page;
import com.ecfront.dew.common.Resp;
import idealworld.dew.saas.common.sdk.CommonSDK;
import idealworld.dew.saas.service.ident.dto.account.*;
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
import idealworld.dew.saas.service.ident.enumeration.AccountIdentKind;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;

/**
 * Ident sdk.
 *
 * @author gudaoxuri
 */
@Slf4j
public class IdentSDK extends CommonSDK<IdentConfig> {

    /**
     * The Auth.
     */
    public Auth auth = new Auth();
    /**
     * The Account.
     */
    public Account account = new Account();
    /**
     * The Organization.
     */
    public Organization organization = new Organization();
    /**
     * The Permission.
     */
    public Permission permission = new Permission();
    /**
     * The Position.
     */
    public Position position = new Position();
    /**
     * The Post.
     */
    public Post post = new Post();
    /**
     * The Resource.
     */
    public Resource resource = new Resource();

    /**
     * Builder ident sdk.
     *
     * @param config the config
     * @return the ident sdk
     */
    public static IdentSDK builder(IdentConfig config) {
        var identSDK = new IdentSDK();
        identSDK.setServiceUrl(config.getIdent().getUrl());
        identSDK.setConfig(config);
        return identSDK;
    }

    /**
     * Auth.
     */
    public class Auth {

        /**
         * 获取当前登录用户.
         *
         * @param token        the token
         * @param optInfoClazz the opt info clazz
         * @return 账号Id opt info
         */
        public <E> Resp<E> getOptInfo(String token, Class<E> optInfoClazz) {
            return getToEntity("app/auth/optinfo", new HashMap<>() {
                {
                    put(IdentSDK.super.config.getIdent().getTokenFlag(), token);
                }
            }, optInfoClazz);
        }

        /**
         * 获取OAuth的AccessToken.
         *
         * @param oauthKind the oauth kind
         * @return 账号Id resp
         */
        public Resp<String> oauthGetAccessToken(AccountIdentKind oauthKind) {
            return getToEntity("/app/auth/tenant/oauth/" + oauthKind.toString() + "/access-token", String.class);
        }

        /**
         * 获取OAuth的OpenId.
         *
         * @param accountOpenId the account open id
         * @param oauthKind     the oauth kind
         * @return 账号Id resp
         */
        public Resp<String> oauthGetOpenId(String accountOpenId, AccountIdentKind oauthKind) {
            return getToEntity("/app/auth/account/oauth/" + accountOpenId + "/" + oauthKind.toString() + "/ident-ak", String.class);
        }

        /**
         * 添加当前租户的账号.
         *
         * @param addAccountReq 添加账号请求
         * @return 账号Id resp
         */
        public Resp<Long> addAccount(AddAccountReq addAccountReq) {
            return post("app/account", addAccountReq);
        }


        /**
         * 订阅当前应用的权限信息.
         *
         * @return the resp
         */
        public Resp<String> subPermissions() {
            Resp<String> subResp = getToEntity("app/auth/permission/sub?heartbeatPeriodSec=" +
                    getConfig().getIdent().getAliveHeartbeatPeriodSec(), String.class);
            if (!subResp.ok()) {
                return subResp;
            }
            $.timer.periodic(getConfig().getIdent().getAliveHeartbeatPeriodSec().longValue(), true, () -> {
                Resp<Void> hbResp = getToEntity("app/auth/permission/heartbeat?heartbeatPeriodSec=" +
                        getConfig().getIdent().getAliveHeartbeatPeriodSec(), Void.class);
                if (!hbResp.ok()) {
                    log.error("Permission subscribe error [{}] : {}", hbResp.getCode(), hbResp.getMessage());
                }
            });
            return subResp;
        }

        /**
         * 取消当前应用的订阅权限信息.
         * <p>
         * 此操作会取消当前应用的所有实例订阅，在多实例场景下慎用
         *
         * @return the resp
         */
        public Resp<Void> unSubPermission() {
            return delete("app/auth/permission/sub");
        }
    }

    /**
     * Account.
     */
    public class Account {

        /**
         * 添加当前租户的账号.
         *
         * @param addAccountReq 添加账号请求
         * @return 账号Id resp
         */
        public Resp<Long> addAccount(AddAccountReq addAccountReq) {
            return post("app/account", addAccountReq);
        }

        /**
         * 获取当前租户的账号列表信息.
         *
         * @param pageNumber the page number
         * @param pageSize   the page size
         * @return resp resp
         */
        public Resp<Page<AccountInfoResp>> findAccountInfo(Long pageNumber, Integer pageSize) {
            return getToPage("app/account", pageNumber, pageSize, AccountInfoResp.class);
        }

        /**
         * 获取当前租户的某个账号信息.
         *
         * @param accountId the account id
         * @return the account info
         */
        public Resp<AccountInfoResp> getAccountInfo(Long accountId) {
            return getToEntity("app/account/" + accountId, AccountInfoResp.class);
        }

        /**
         * 修改当前租户的某个账号.
         *
         * @param accountId        the account id
         * @param modifyAccountReq the modify account req
         * @return resp resp
         */
        public Resp<Void> modifyAccount(Long accountId,
                                        ModifyAccountReq modifyAccountReq) {
            return patch("app/account/" + accountId, modifyAccountReq);
        }

        /**
         * 删除当前租户的某个账号.
         * <p>
         * 删除账号，关联的账号认证、账号岗位
         *
         * @param accountId the account id
         * @return the resp
         */
        public Resp<Void> deleteAccount(Long accountId) {
            return delete("app/account/" + accountId);
        }

        // ========================== Ident ==============================

        /**
         * 添加当前租户某个账号的认证.
         *
         * @param accountId          the account id
         * @param addAccountIdentReq the add account ident req
         * @return the resp
         */
        public Resp<Long> addAccountIdent(Long accountId,
                                          AddAccountIdentReq addAccountIdentReq) {
            return post("app/account/" + accountId + "/ident", addAccountIdentReq);
        }

        /**
         * 获取当前租户某个账号的认证列表信息.
         *
         * @param accountId the account id
         * @return the resp
         */
        public Resp<List<AccountIdentInfoResp>> findAccountIdentInfo(Long accountId) {
            return getToList("app/account/" + accountId + "/ident", AccountIdentInfoResp.class);
        }

        /**
         * 修改当前租户某个账号的某个认证.
         *
         * @param accountId             the account id
         * @param accountIdentId        the account ident id
         * @param modifyAccountIdentReq the modify account ident req
         * @return the resp
         */
        public Resp<Void> modifyAccountIdent(Long accountId,
                                             Long accountIdentId,
                                             ModifyAccountIdentReq modifyAccountIdentReq) {
            return patch("app/account/" + accountId + "/ident/" + accountIdentId, modifyAccountIdentReq);
        }

        /**
         * 删除当前租户某个账号的某个认证.
         *
         * @param accountId      the account id
         * @param accountIdentId the account ident id
         * @return the resp
         */
        public Resp<Void> deleteAccountIdent(Long accountId,
                                             Long accountIdentId) {
            return delete("app/account/" + accountId + "/ident/" + accountIdentId);
        }

        /**
         * 删除当前租户某个账号的所有认证.
         *
         * @param accountId the account id
         * @return the resp
         */
        public Resp<Void> deleteAccountIdents(Long accountId) {
            return delete("app/account/" + accountId + "/ident");
        }

        // ========================== Post ==============================

        /**
         * 添加当前租户某个账号的岗位.
         *
         * @param accountId         the account id
         * @param addAccountPostReq the add account post req
         * @return the resp
         */
        public Resp<Long> addAccountPost(Long accountId,
                                         AddAccountPostReq addAccountPostReq) {
            return post("app/account/" + accountId + "/post", addAccountPostReq);
        }

        /**
         * 获取当前租户某个账号的岗位列表信息.
         *
         * @param accountId the account id
         * @return the resp
         */
        public Resp<List<AccountPostInfoResp>> findAccountPostInfo(Long accountId) {
            return getToList("app/account/" + accountId + "/post", AccountPostInfoResp.class);
        }

        /**
         * 删除当前租户某个账号的某个岗位.
         *
         * @param accountId     the account id
         * @param accountPostId the account post id
         * @return the resp
         */
        public Resp<Void> deleteAccountPost(Long accountId,
                                            Long accountPostId) {
            return delete("app/account/" + accountId + "/post/" + accountPostId);
        }

    }


    /**
     * Organization.
     */
    public class Organization {
        /**
         * 添加当前应用的机构.
         *
         * @param addOrganizationReq the add organization req
         * @return resp resp
         */
        public Resp<Long> addOrganization(AddOrganizationReq addOrganizationReq) {
            return post("/app/organization", addOrganizationReq);
        }

        /**
         * 获取当前应用的机构列表信息.
         *
         * @return resp resp
         */
        public Resp<List<OrganizationInfoResp>> findOrganizationInfo() {
            return getToList("/app/organization", OrganizationInfoResp.class);
        }

        /**
         * 修改当前应用的某个机构.
         *
         * @param organizationId        the organization id
         * @param modifyOrganizationReq the modify organization req
         * @return the resp
         */
        public Resp<Void> modifyOrganization(Long organizationId,
                                             ModifyOrganizationReq modifyOrganizationReq) {
            return patch("/app/organization/" + organizationId, modifyOrganizationReq);
        }

        /**
         * 删除当前应用的某个机构.
         * <p>
         * 级联删除机构，关联的岗位、账号岗位、权限
         *
         * @param organizationId the organization id
         * @return the resp
         */
        public Resp<Void> deleteOrganization(Long organizationId) {
            return delete("/app/organization/" + organizationId);
        }
    }

    /**
     * Permission.
     */
    public class Permission {

        /**
         * 添加当前应用的权限.
         *
         * @param addPermissionReq the add permission req
         * @return resp resp
         */
        public Resp<Long> addPermission(AddPermissionReq addPermissionReq) {
            return post("/app/permission", addPermissionReq);
        }

        /**
         * 获取当前应用的权限列表信息.
         *
         * @return resp resp
         */
        public Resp<List<PermissionInfoResp>> findPermissionInfo() {
            return getToList("/app/permission", PermissionInfoResp.class);
        }

        /**
         * 删除当前应用的某个权限.
         *
         * @param permissionId the permission id
         * @return resp resp
         */
        public Resp<Void> deletePermission(Long permissionId) {
            return delete("/app/permission/" + permissionId);
        }
    }

    /**
     * Position.
     */
    public class Position {

        /**
         * 添加当前应用的职位.
         *
         * @param addPositionReq the add position req
         * @return resp resp
         */
        public Resp<Long> addPosition(AddPositionReq addPositionReq) {
            return post("/app/position", addPositionReq);
        }

        /**
         * 获取当前应用的职位列表信息.
         *
         * @return resp resp
         */
        public Resp<List<PositionInfoResp>> findPositionInfo() {
            return getToList("/app/position", PositionInfoResp.class);
        }

        /**
         * 修改当前应用的某个职位.
         *
         * @param positionId        the position id
         * @param modifyPositionReq the modify position req
         * @return the resp
         */
        public Resp<Void> modifyPosition(Long positionId,
                                         ModifyPositionReq modifyPositionReq) {
            return patch("/app/position/" + positionId, modifyPositionReq);
        }

        /**
         * 删除当前应用的某个职位.
         * <p>
         * 删除职位，关联的岗位、账号岗位、权限
         *
         * @param positionId the position id
         * @return the resp
         */
        public Resp<Void> deletePosition(Long positionId) {
            return delete("/app/position/" + positionId);
        }
    }

    /**
     * Post.
     */
    public class Post {

        /**
         * 添加当前应用的岗位.
         *
         * @param addPostReq the add post req
         * @return resp resp
         */
        public Resp<Long> addPost(AddPostReq addPostReq) {
            return post("/app/post", addPostReq);
        }

        /**
         * 获取当前应用的岗位列表信息.
         *
         * @return the resp
         */
        public Resp<List<PostInfoResp>> findPostInfo() {
            return getToList("/app/post", PostInfoResp.class);
        }

        /**
         * 删除当前应用的某个岗位.
         * <p>
         * 删除岗位，关联账号岗位、权限
         *
         * @param postId the post id
         * @return the resp
         */
        public Resp<Void> deletePost(Long postId) {
            return delete("/app/post/" + postId);
        }
    }

    /**
     * Resource.
     */
    public class Resource {

        /**
         * 添加当前应用的资源组.
         *
         * @param addResourceGroupReq the add resource group req
         * @return the resp
         */
        public Resp<Long> addResourceGroup(AddResourceGroupReq addResourceGroupReq) {
            return post("/app/resource/group", addResourceGroupReq);
        }

        /**
         * 添加当前应用的资源.
         *
         * @param addResourceReq the add resource req
         * @return the resp
         */
        public Resp<Long> addResource(AddResourceReq addResourceReq) {
            return post("/app/resource", addResourceReq);
        }

        /**
         * 修改当前应用的某个资源（组）.
         *
         * @param resourceId        the resource id
         * @param modifyResourceReq the modify resource req
         * @return the resp
         */
        public Resp<Void> modifyResource(Long resourceId,
                                         ModifyResourceReq modifyResourceReq) {
            return patch("/app/resource/" + resourceId, modifyResourceReq);
        }

        /**
         * 获取当前应用的某个资源（组）信息.
         *
         * @param resourceId the resource id
         * @return the resource
         */
        public Resp<ResourceInfoResp> getResource(Long resourceId) {
            return getToEntity("/app/resource/" + resourceId, ResourceInfoResp.class);
        }

        /**
         * 获取当前应用的资源（组）列表信息.
         *
         * @return the resp
         */
        public Resp<List<ResourceInfoResp>> findResources() {
            return getToList("/app/resource", ResourceInfoResp.class);
        }

        /**
         * 删除当前应用的某个资源（组）及关联的权限.
         *
         * @param resourceId the resource id
         * @return resp resp
         */
        public Resp<Void> deleteResource(Long resourceId) {
            return delete("/app/resource/" + resourceId);
        }

    }
}
