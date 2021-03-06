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

package idealworld.dew.saas.service.ident.controller.app;

import com.ecfront.dew.common.Resp;
import idealworld.dew.saas.service.ident.controller.BasicController;
import idealworld.dew.saas.service.ident.dto.post.AddPostReq;
import idealworld.dew.saas.service.ident.dto.post.PostInfoResp;
import idealworld.dew.saas.service.ident.interceptor.AppHandlerInterceptor;
import idealworld.dew.saas.service.ident.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 应用控制台岗位管理操.
 *
 * @author gudaoxuri
 */
@RestController
@Tag(name = "app post", description = "应用控制台岗位管理操作")
@RequestMapping(value = "/app/post")
@Validated
public class AppPostController extends BasicController {

    @Autowired
    private PostService postService;
    @Autowired
    private AppHandlerInterceptor appHandlerInterceptor;

    /**
     * 添加当前应用的岗位.
     *
     * @param addPostReq the add post req
     * @return the resp
     */
    @PostMapping(value = "")
    @Operation(summary = "添加当前应用的岗位")
    public Resp<Long> addPost(@Validated @RequestBody AddPostReq addPostReq) {
        return postService.addPost(addPostReq,
                appHandlerInterceptor.getCurrentTenantAndAppId()._1,
                appHandlerInterceptor.getCurrentTenantAndAppId()._0);
    }

    /**
     * 获取当前应用的岗位列表信息.
     *
     * @return the resp
     */
    @GetMapping(value = "")
    @Operation(summary = "获取当前应用的岗位列表信息")
    public Resp<List<PostInfoResp>> findPostInfo() {
        return postService.findPostInfo(
                appHandlerInterceptor.getCurrentTenantAndAppId()._1,
                appHandlerInterceptor.getCurrentTenantAndAppId()._0);
    }

    /**
     * 删除当前应用的某个岗位.
     *
     * @param postId the post id
     * @return the resp
     */
    @DeleteMapping(value = "/{postId}")
    @Operation(summary = "删除当前应用的某个岗位、关联账号岗位、权限")
    public Resp<Void> deletePost(@PathVariable Long postId) {
        return postService.deletePost(postId,
                appHandlerInterceptor.getCurrentTenantAndAppId()._1,
                appHandlerInterceptor.getCurrentTenantAndAppId()._0);
    }

}
