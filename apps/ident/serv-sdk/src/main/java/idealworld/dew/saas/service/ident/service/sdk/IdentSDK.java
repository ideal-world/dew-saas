package idealworld.dew.saas.service.ident.service.sdk;

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
import idealworld.dew.saas.service.ident.enumeration.AccountCertKind;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;

@Slf4j
public class IdentSDK extends CommonSDK<IdentConfig> {

    public Auth auth = new Auth();
    public Account account = new Account();
    public Organization organization = new Organization();
    public Permission permission = new Permission();
    public Position position = new Position();
    public Post post = new Post();
    public Resource resource = new Resource();

    public static IdentSDK builder(IdentConfig config) {
        var identSDK = new IdentSDK();
        identSDK.setServiceUrl(config.getIdent().getUrl());
        identSDK.setConfig(config);
        return identSDK;
    }

    public class Auth {

        /**
         * 获取当前登录用户.
         *
         * @return 账号Id
         */
        public <E> Resp<E> getOptInfo(String token, Class<E> optInfoClazz) {
            return getToEntity("app/auth/optinfo", new HashMap<>() {{
                put(IdentSDK.super.config.getIdent().getTokenFlag(), token);
            }}, optInfoClazz);
        }

        /**
         * 获取OAuth的AccessToken.
         *
         * @return 账号Id
         */
        public Resp<String> oauthGetAccessToken(AccountCertKind oauthKind) {
            return getToEntity("/app/auth/oauth/" + oauthKind.toString() + "/access-token", String.class);
        }

        /**
         * 获取OAuth的OpenId.
         *
         * @return 账号Id
         */
        public Resp<String> oauthGetOpenId(Long accountId, AccountCertKind oauthKind) {
            return getToEntity("/app/account/" + accountId + "cert-ak?accountCertKind=" + oauthKind.toString(), String.class);
        }

        /**
         * 添加当前租户的账号.
         *
         * @param addAccountReq 添加账号请求
         * @return 账号Id
         */
        public Resp<Long> addAccount(AddAccountReq addAccountReq) {
            return post("app/account", addAccountReq);
        }


        /**
         * 订阅当前应用的权限信息
         */
        public Resp<String> subPermissions() {
            return getToEntity("app/auth/permission/sub?expireSec=" + getConfig().getIdent().getFetchSec(), String.class);
        }

        /**
         * 取消当前应用的订阅权限信息
         * <p>
         * 此操作会取消当前应用的所有实例订阅，在多实例场景下慎用
         */
        public Resp<Void> unSubPermission() {
            return delete("app/auth/permission/sub");
        }
    }

    public class Account {

        /**
         * 添加当前租户的账号.
         *
         * @param addAccountReq 添加账号请求
         * @return 账号Id
         */
        public Resp<Long> addAccount(AddAccountReq addAccountReq) {
            return post("app/account", addAccountReq);
        }

        /**
         * 获取当前租户的账号列表信息
         *
         * @return
         */
        public Resp<Page<AccountInfoResp>> findAccountInfo(Long pageNumber, Integer pageSize) {
            return getToPage("app/account", pageNumber, pageSize, AccountInfoResp.class);
        }

        /**
         * 获取当前租户的某个账号信息
         */
        public Resp<AccountInfoResp> getAccountInfo(Long accountId) {
            return getToEntity("app/account/" + accountId, AccountInfoResp.class);
        }

        /**
         * 修改当前租户的某个账号
         *
         * @param accountId
         * @param modifyAccountReq
         * @return
         */
        public Resp<Void> modifyAccount(Long accountId,
                                        ModifyAccountReq modifyAccountReq) {
            return patch("app/account/" + accountId, modifyAccountReq);
        }

        /**
         * 删除当前租户的某个账号
         * <p>
         * 删除账号，关联的账号凭证、账号岗位
         */
        public Resp<Void> deleteAccount(Long accountId) {
            return delete("app/account/" + accountId);
        }

        // ========================== Cert ==============================

        /**
         * 添加当前租户某个账号的凭证
         */
        public Resp<Long> addAccountCert(Long accountId,
                                         AddAccountCertReq addAccountCertReq) {
            return post("app/account/" + accountId + "/cert", addAccountCertReq);
        }

        /**
         * 获取当前租户某个账号的凭证列表信息
         */
        public Resp<List<AccountCertInfoResp>> findAccountCertInfo(Long accountId) {
            return getToList("app/account/" + accountId + "/cert", AccountCertInfoResp.class);
        }

        /**
         * 修改当前租户某个账号的某个凭证
         */
        public Resp<Void> modifyAccountCert(Long accountId,
                                            Long accountCertId,
                                            ModifyAccountCertReq modifyAccountCertReq) {
            return patch("app/account/" + accountId + "/cert/" + accountCertId, modifyAccountCertReq);
        }

        /**
         * 删除当前租户某个账号的某个凭证
         */
        public Resp<Void> deleteAccountCert(Long accountId,
                                            Long accountCertId) {
            return delete("app/account/" + accountId + "/cert/" + accountCertId);
        }

        /**
         * 删除当前租户某个账号的所有凭证
         */
        public Resp<Void> deleteAccountCerts(Long accountId) {
            return delete("app/account/" + accountId + "/cert");
        }

        // ========================== Post ==============================

        /**
         * 添加当前租户某个账号的岗位
         */
        public Resp<Long> addAccountPost(Long accountId,
                                         AddAccountPostReq addAccountPostReq) {
            return post("app/account/" + accountId + "/post", addAccountPostReq);
        }

        /**
         * 获取当前租户某个账号的岗位列表信息
         */
        public Resp<List<AccountPostInfoResp>> findAccountPostInfo(Long accountId) {
            return getToList("app/account/" + accountId + "/post", AccountPostInfoResp.class);
        }

        /**
         * 删除当前租户某个账号的某个岗位
         */
        public Resp<Void> deleteAccountPost(Long accountId,
                                            Long accountPostId) {
            return delete("app/account/" + accountId + "/post/" + accountPostId);
        }

    }


    public class Organization {
        /**
         * 添加当前应用的机构
         *
         * @param addOrganizationReq
         * @return
         */
        public Resp<Long> addOrganization(AddOrganizationReq addOrganizationReq) {
            return post("/app/organization", addOrganizationReq);
        }

        /**
         * 获取当前应用的机构列表信息
         *
         * @return
         */
        public Resp<List<OrganizationInfoResp>> findOrganizationInfo() {
            return getToList("/app/organization", OrganizationInfoResp.class);
        }

        /**
         * 修改当前应用的某个机构
         */
        public Resp<Void> modifyOrganization(Long organizationId,
                                             ModifyOrganizationReq modifyOrganizationReq) {
            return patch("/app/organization/" + organizationId, modifyOrganizationReq);
        }

        /**
         * 删除当前应用的某个机构
         * <p>
         * 级联删除机构，关联的岗位、账号岗位、权限
         */
        public Resp<Void> deleteOrganization(Long organizationId) {
            return delete("/app/organization/" + organizationId);
        }
    }

    public class Permission {

        /**
         * 添加当前应用的权限
         *
         * @param addPermissionReq
         * @return
         */
        public Resp<Long> addPermission(AddPermissionReq addPermissionReq) {
            return post("/app/permission", addPermissionReq);
        }

        /**
         * 获取当前应用的权限列表信息
         *
         * @return
         */
        public Resp<List<PermissionInfoResp>> findPermissionInfo() {
            return getToList("/app/permission", PermissionInfoResp.class);
        }

        /**
         * 删除当前应用的某个权限
         *
         * @param permissionId
         * @return
         */
        public Resp<Void> deletePermission(Long permissionId) {
            return delete("/app/permission/" + permissionId);
        }
    }

    public class Position {

        /**
         * 添加当前应用的职位
         *
         * @param addPositionReq
         * @return
         */
        public Resp<Long> addPosition(AddPositionReq addPositionReq) {
            return post("/app/position", addPositionReq);
        }

        /**
         * 获取当前应用的职位列表信息
         *
         * @return
         */
        public Resp<List<PositionInfoResp>> findPositionInfo() {
            return getToList("/app/position", PositionInfoResp.class);
        }

        /**
         * 修改当前应用的某个职位
         */
        public Resp<Void> modifyPosition(Long positionId,
                                         ModifyPositionReq modifyPositionReq) {
            return patch("/app/position/" + positionId, modifyPositionReq);
        }

        /**
         * 删除当前应用的某个职位
         * <p>
         * 删除职位，关联的岗位、账号岗位、权限
         */
        public Resp<Void> deletePosition(Long positionId) {
            return delete("/app/position/" + positionId);
        }
    }

    public class Post {

        /**
         * 添加当前应用的岗位
         *
         * @param addPostReq
         * @return
         */
        public Resp<Long> addPost(AddPostReq addPostReq) {
            return post("/app/post", addPostReq);
        }

        /**
         * 获取当前应用的岗位列表信息
         */
        public Resp<List<PostInfoResp>> findPostInfo() {
            return getToList("/app/post", PostInfoResp.class);
        }

        /**
         * 删除当前应用的某个岗位
         * <p>
         * 删除岗位，关联账号岗位、权限
         */
        public Resp<Void> deletePost(Long postId) {
            return delete("/app/post/" + postId);
        }
    }

    public class Resource {

        /**
         * 添加当前应用的资源组
         */
        public Resp<Long> addResourceGroup(AddResourceGroupReq addResourceGroupReq) {
            return post("/app/resource/group", addResourceGroupReq);
        }

        /**
         * 添加当前应用的资源
         */
        public Resp<Long> addResource(AddResourceReq addResourceReq) {
            return post("/app/resource", addResourceReq);
        }

        /**
         * 修改当前应用的某个资源（组）
         */
        public Resp<Void> modifyResource(Long resourceId,
                                         ModifyResourceReq modifyResourceReq) {
            return patch("/app/resource/" + resourceId, modifyResourceReq);
        }

        /**
         * 获取当前应用的某个资源（组）信息
         */
        public Resp<ResourceInfoResp> getResource(Long resourceId) {
            return getToEntity("/app/resource/" + resourceId, ResourceInfoResp.class);
        }

        /**
         * 获取当前应用的资源（组）列表信息
         */
        public Resp<List<ResourceInfoResp>> findResources() {
            return getToList("/app/resource", ResourceInfoResp.class);
        }

        /**
         * 删除当前应用的某个资源（组）及关联的权限
         *
         * @param resourceId
         * @return
         */
        public Resp<Void> deleteResource(Long resourceId) {
            return delete("/app/resource/" + resourceId);
        }

    }
}
