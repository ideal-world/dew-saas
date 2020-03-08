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

package group.idealworld.dew.saas.service.ident.service;

import com.ecfront.dew.common.Resp;
import group.idealworld.dew.saas.service.ident.IdentConfig;
import group.idealworld.dew.saas.service.ident.domain.QPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * @author gudaoxuri
 */
@Service
public class PostService extends BasicService {

    @Autowired
    private IdentConfig identConfig;

    @Cacheable("cache:post:getTenantPostId")
    public Resp<Long> getTenantPostId() {
        var qPost = QPost.post;
        var tenantId = queryFactory.select(qPost.id)
                .from(qPost)
                .where(qPost.positionCode.eq(identConfig.getSecurity().getTenantAdminPositionCode()))
                .fetchOne();
        return Resp.success(tenantId);
    }

}
