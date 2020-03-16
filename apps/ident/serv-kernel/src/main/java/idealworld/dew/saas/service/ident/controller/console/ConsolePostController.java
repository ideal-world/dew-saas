/*
 * Copyright 2019. the original author or authors.
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

package idealworld.dew.saas.service.ident.controller.console;

import com.ecfront.dew.common.Resp;
import idealworld.dew.saas.service.ident.controller.BasicController;
import idealworld.dew.saas.service.ident.dto.post.AddPostReq;
import idealworld.dew.saas.service.ident.dto.post.PostInfoResp;
import idealworld.dew.saas.service.ident.service.PostService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author gudaoxuri
 */
@RestController
@Api(value = "租户控制台岗位管理操作")
@RequestMapping(value = "/console/post")
@Validated
public class ConsolePostController extends BasicController {

    @Autowired
    private PostService postService;

    @PostMapping(value = "{appId}")
    @ApiOperation(value = "添加当前租户某个应用的岗位")
    public Resp<Long> addPost(@PathVariable Long appId, @RequestBody AddPostReq addPostReq) {
        return postService.addPost(addPostReq, appId, getCurrentTenantId());
    }

    @GetMapping(value = "{appId}")
    @ApiOperation(value = "获取当前租户某个应用的岗位列表信息")
    public Resp<List<PostInfoResp>> findPostInfo(@PathVariable Long appId) {
        return postService.findPostInfo(appId, getCurrentTenantId());
    }

    @DeleteMapping(value = "{appId}/{postId}")
    @ApiOperation(value = "删除当前租户某个应用的某个岗位")
    public Resp<Void> deletePost(@PathVariable Long appId, @PathVariable Long postId) {
        return postService.deletePost(postId, appId, getCurrentTenantId());
    }

}
