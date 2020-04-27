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

package idealworld.dew.saas.service.ident.service;

import com.ecfront.dew.common.Resp;
import com.querydsl.core.types.Projections;
import idealworld.dew.saas.common.resp.StandardResp;
import idealworld.dew.saas.service.ident.IdentConfig;
import idealworld.dew.saas.service.ident.domain.Post;
import idealworld.dew.saas.service.ident.domain.QPost;
import idealworld.dew.saas.service.ident.dto.post.AddPostReq;
import idealworld.dew.saas.service.ident.dto.post.PostInfoResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Post service.
 *
 * @author gudaoxuri
 */
@Service
public class PostService extends IdentBasicService {

    private static final String BUSINESS_POST = "POST";

    @Autowired
    private IdentConfig identConfig;
    @Autowired
    private AppService appService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private PermissionService permissionService;

    /**
     * Gets tenant admin post id.
     *
     * @return the tenant admin post id
     */
    public Long getTenantAdminPostId() {
        var qPost = QPost.post;
        return sqlBuilder.select(qPost.id)
                .from(qPost)
                .where(qPost.relPositionCode.eq(identConfig.getSecurity().getTenantAdminPositionCode()))
                .fetchOne();
    }

    /**
     * Gets default post id.
     *
     * @return the default post id
     */
    public Long getDefaultPostId() {
        var qPost = QPost.post;
        return sqlBuilder.select(qPost.id)
                .from(qPost)
                .where(qPost.relPositionCode.eq(identConfig.getSecurity().getDefaultPositionCode()))
                .fetchOne();
    }

    /**
     * Add post.
     *
     * @param addPostReq  the add post req
     * @param relAppId    the rel app id
     * @param relTenantId the rel tenant id
     * @return the resp
     */
    @Transactional
    public Resp<Long> addPost(AddPostReq addPostReq, Long relAppId, Long relTenantId) {
        var membershipCheckR = appService.checkAppMembership(relAppId, relTenantId);
        if (!membershipCheckR.ok()) {
            return StandardResp.error(membershipCheckR);
        }
        var qPost = QPost.post;
        if (sqlBuilder.select(qPost.id)
                .from(qPost)
                .where(qPost.relTenantId.eq(relTenantId))
                .where(qPost.relAppId.eq(relAppId))
                .where(qPost.relOrganizationCode.eq(addPostReq.getRelOrganizationCode() != null ? addPostReq.getRelOrganizationCode() : ""))
                .where(qPost.relPositionCode.eq(addPostReq.getRelPositionCode()))
                .fetchCount() != 0) {
            return StandardResp.conflict(BUSINESS_POST, "此岗位已存在");
        }
        var position = Post.builder()
                .relOrganizationCode(addPostReq.getRelOrganizationCode() != null ? addPostReq.getRelOrganizationCode() : "")
                .relPositionCode(addPostReq.getRelPositionCode())
                .sort(addPostReq.getSort() != null ? addPostReq.getSort() : 0)
                .relAppId(relAppId)
                .relTenantId(relTenantId)
                .build();
        return saveEntity(position);
    }

    /**
     * Find post info.
     *
     * @param relAppId    the rel app id
     * @param relTenantId the rel tenant id
     * @return the resp
     */
    public Resp<List<PostInfoResp>> findPostInfo(Long relAppId, Long relTenantId) {
        var qPost = QPost.post;
        var postQuery = sqlBuilder
                .select(Projections.bean(PostInfoResp.class,
                        qPost.id,
                        qPost.relOrganizationCode,
                        qPost.relPositionCode,
                        qPost.sort,
                        qPost.relAppId))
                .from(qPost)
                .where(qPost.relAppId.eq(relAppId))
                .where(qPost.relTenantId.eq(relTenantId));
        return findDTOs(postQuery);
    }

    /**
     * Delete post.
     *
     * @param postId      the post id
     * @param relAppId    the rel app id
     * @param relTenantId the rel tenant id
     * @return the resp
     */
    @Transactional
    public Resp<Void> deletePost(Long postId, Long relAppId, Long relTenantId) {
        doDeletePosts(new ArrayList<>() {
            {
                add(postId);
            }
        }, relAppId, relTenantId);
        return StandardResp.success(null);
    }

    /**
     * Delete post.
     *
     * @param relAppId    the rel app id
     * @param relTenantId the rel tenant id
     * @return the resp
     */
    @Transactional
    protected Resp<Long> deletePost(Long relAppId, Long relTenantId) {
        var qPost = QPost.post;
        var deletePostIds = sqlBuilder.select(qPost.id)
                .from(qPost)
                .where(qPost.relTenantId.eq(relTenantId))
                .where(qPost.relAppId.eq(relAppId))
                .fetch();
        return doDeletePosts(deletePostIds, relAppId, relTenantId);
    }

    /**
     * Delete post by org codes.
     *
     * @param deleteOrgCodes the delete org codes
     * @param relAppId       the rel app id
     * @param relTenantId    the rel tenant id
     * @return the resp
     */
    @Transactional
    protected Resp<Long> deletePostByOrgCodes(List<String> deleteOrgCodes, Long relAppId, Long relTenantId) {
        if (deleteOrgCodes.isEmpty()) {
            return StandardResp.notFound(BUSINESS_POST, "没有要删除的机构");
        }
        var qPost = QPost.post;
        var deletePostIds = sqlBuilder.select(qPost.id)
                .from(qPost)
                .where(qPost.relOrganizationCode.in(deleteOrgCodes))
                .where(qPost.relTenantId.eq(relTenantId))
                .where(qPost.relAppId.eq(relAppId))
                .fetch();
        return doDeletePosts(deletePostIds, relAppId, relTenantId);
    }

    /**
     * Delete post by position codes.
     *
     * @param deletePositionCodes the delete position codes
     * @param relAppId            the rel app id
     * @param relTenantId         the rel tenant id
     * @return the resp
     */
    @Transactional
    protected Resp<Long> deletePostByPositionCodes(List<String> deletePositionCodes, Long relAppId, Long relTenantId) {
        if (deletePositionCodes.isEmpty()) {
            return StandardResp.notFound(BUSINESS_POST, "没有要删除的职位");
        }
        var qPost = QPost.post;
        var deletePostIds = sqlBuilder.select(qPost.id)
                .from(qPost)
                .where(qPost.relPositionCode.in(deletePositionCodes))
                .where(qPost.relTenantId.eq(relTenantId))
                .where(qPost.relAppId.eq(relAppId))
                .fetch();
        return doDeletePosts(deletePostIds, relAppId, relTenantId);
    }

    private Resp<Long> doDeletePosts(List<Long> postIds, Long relAppId, Long relTenantId) {
        // 删除账号岗位
        accountService.deleteAccountPosts(postIds);
        // 删除权限
        permissionService.deletePermissionByPostIds(postIds, relAppId, relTenantId);
        // 删除岗位
        var qPost = QPost.post;
        return deleteEntities(sqlBuilder
                .delete(qPost)
                .where(qPost.id.in(postIds))
                .where(qPost.relAppId.eq(relAppId))
                .where(qPost.relTenantId.eq(relTenantId)));
    }

}
