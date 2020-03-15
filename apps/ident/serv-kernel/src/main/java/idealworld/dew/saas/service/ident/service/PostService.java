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
import idealworld.dew.saas.service.ident.Constant;
import idealworld.dew.saas.service.ident.IdentConfig;
import idealworld.dew.saas.service.ident.domain.Post;
import idealworld.dew.saas.service.ident.domain.QPost;
import idealworld.dew.saas.service.ident.dto.post.AddPostReq;
import idealworld.dew.saas.service.ident.dto.post.PostInfoResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author gudaoxuri
 */
@Service
public class PostService extends BasicService {

    @Autowired
    private IdentConfig identConfig;
    @Autowired
    private AppService appService;

    @Cacheable("cache:post:getTenantPostId")
    public Long getTenantAdminPostId() {
        var qPost = QPost.post;
        return sqlBuilder.select(qPost.id)
                .from(qPost)
                .where(qPost.relPositionCode.eq(identConfig.getSecurity().getTenantAdminPositionCode()))
                .fetchOne();
    }

    @Transactional
    public Resp<Long> addPost(AddPostReq addPostReq, Long relAppId, Long relTenantId) {
        if (!appService.checkAppMembership(relAppId, relTenantId)) {
            return Constant.RESP.NOT_FOUNT();
        }
        var position = Post.builder()
                .relOrganizationId(addPostReq.getRelOrganizationId() != null ? addPostReq.getRelOrganizationId() : -1L)
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
                        qPost.relOrganizationId,
                        qPost.relPositionCode,
                        qPost.relAppId))
                .from(qPost)
                .where(qPost.relAppId.eq(relAppId))
                .where(qPost.relTenantId.eq(relTenantId));
        return findDTOs(postQuery);
    }

    @Transactional
    public Resp<Void> deletePost(Long postId, Long relAppId, Long relTenantId) {
        // TODO permission accountPost
        var qPost = QPost.post;
        return deleteEntity(sqlBuilder
                .delete(qPost)
                .where(qPost.id.eq(postId))
                .where(qPost.relAppId.eq(relAppId))
                .where(qPost.relTenantId.eq(relTenantId))
        );
    }

}
