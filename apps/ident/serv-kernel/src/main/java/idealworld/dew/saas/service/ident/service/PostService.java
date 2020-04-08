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
import idealworld.dew.saas.common.Constant;
import idealworld.dew.saas.service.ident.IdentConfig;
import idealworld.dew.saas.service.ident.domain.Post;
import idealworld.dew.saas.service.ident.domain.QPost;
import idealworld.dew.saas.service.ident.dto.post.AddPostReq;
import idealworld.dew.saas.service.ident.dto.post.PostInfoResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gudaoxuri
 */
@Service
public class PostService extends IdentBasicService {

    @Autowired
    private IdentConfig identConfig;
    @Autowired
    private AppService appService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private PermissionService permissionService;

    @Cacheable("cache:post:getTenantAdminPostId")
    public Long getTenantAdminPostId() {
        var qPost = QPost.post;
        return sqlBuilder.select(qPost.id)
                .from(qPost)
                .where(qPost.relPositionCode.eq(identConfig.getSecurity().getTenantAdminPositionCode()))
                .where(qPost.delFlag.eq(false))
                .fetchOne();
    }

    @Cacheable("cache:post:getDefaultPostId")
    public Long getDefaultPostId() {
        var qPost = QPost.post;
        return sqlBuilder.select(qPost.id)
                .from(qPost)
                .where(qPost.relPositionCode.eq(identConfig.getSecurity().getDefaultPositionCode()))
                .where(qPost.delFlag.eq(false))
                .fetchOne();
    }

    @Transactional
    public Resp<Long> addPost(AddPostReq addPostReq, Long relAppId, Long relTenantId) {
        if (!appService.checkAppMembership(relAppId, relTenantId)) {
            return Constant.RESP.NOT_FOUNT();
        }
        var qPost = QPost.post;
        if (sqlBuilder.select(qPost.id)
                .from(qPost)
                .where(qPost.delFlag.eq(false))
                .where(qPost.relTenantId.eq(relTenantId))
                .where(qPost.relAppId.eq(relAppId))
                .where(qPost.relOrganizationCode.eq(addPostReq.getRelOrganizationCode() != null ? addPostReq.getRelOrganizationCode() : ""))
                .where(qPost.relPositionCode.eq(addPostReq.getRelPositionCode()))
                .fetchCount() != 0) {
            return Resp.conflict("此岗位已存在");
        }
        var position = Post.builder()
                .relOrganizationCode(addPostReq.getRelOrganizationCode() != null ? addPostReq.getRelOrganizationCode() : "")
                .relPositionCode(addPostReq.getRelPositionCode())
                .relAppId(relAppId)
                .relTenantId(relTenantId)
                .build();
        return saveEntity(position);
    }

    public Resp<List<PostInfoResp>> findPostInfo(Long relAppId, Long relTenantId) {
        var qPost = QPost.post;
        var postQuery = sqlBuilder
                .select(Projections.bean(PostInfoResp.class,
                        qPost.id,
                        qPost.relOrganizationCode,
                        qPost.relPositionCode,
                        qPost.relAppId))
                .from(qPost)
                .where(qPost.relAppId.eq(relAppId))
                .where(qPost.relTenantId.eq(relTenantId))
                .where(qPost.delFlag.eq(false));
        return findDTOs(postQuery);
    }

    @Transactional
    public Resp<Void> deletePost(Long postId, Long relAppId, Long relTenantId) {
        return doDeletePosts(new ArrayList<>() {{
            add(postId);
        }}, relAppId, relTenantId);
    }

    @Transactional
    protected Resp<Void> deletePostByOrgCodes(List<String> deleteOrgCodes, Long relAppId, Long relTenantId) {
        var qPost = QPost.post;
        var deletePostIds = sqlBuilder.select(qPost.id)
                .from(qPost)
                .where(qPost.relOrganizationCode.in(deleteOrgCodes))
                .where(qPost.delFlag.eq(false))
                .fetch();
        return doDeletePosts(deletePostIds, relAppId, relTenantId);
    }

    @Transactional
    protected Resp<Void> deletePostByPositionCode(String deletePositionCode, Long relAppId, Long relTenantId) {
        var qPost = QPost.post;
        var deletePostIds = sqlBuilder.select(qPost.id)
                .from(qPost)
                .where(qPost.relPositionCode.eq(deletePositionCode))
                .where(qPost.delFlag.eq(false))
                .where(qPost.relTenantId.eq(relTenantId))
                .where(qPost.relAppId.eq(relAppId))
                .fetch();
        return doDeletePosts(deletePostIds, relAppId, relTenantId);
    }

    private Resp<Void> doDeletePosts(List<Long> postIds, Long relAppId, Long relTenantId) {
        // 删除岗位
        var qPost = QPost.post;
        sqlBuilder
                .update(qPost)
                .set(qPost.delFlag, true)
                .where(qPost.id.in(postIds))
                .where(qPost.relAppId.eq(relAppId))
                .where(qPost.relTenantId.eq(relTenantId))
                .execute();
        // 删除账号岗位
        accountService.deleteAccountPostByPostIds(postIds);
        // 删除权限
        permissionService.deletePermissionByPostIds(postIds, relAppId, relTenantId);
        return Resp.success(null);
    }
}
