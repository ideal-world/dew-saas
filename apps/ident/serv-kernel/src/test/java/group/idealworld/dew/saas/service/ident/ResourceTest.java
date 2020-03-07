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

package group.idealworld.dew.saas.service.ident;

import group.idealworld.dew.saas.service.ident.domain.Resource;
import group.idealworld.dew.saas.service.ident.dto.resouce.AddResourceGroupReq;
import group.idealworld.dew.saas.service.ident.dto.resouce.AddResourceReq;
import group.idealworld.dew.saas.service.ident.dto.resouce.ModifyResourceReq;
import group.idealworld.dew.saas.service.ident.dto.resouce.ResourceInfoResp;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * The type tenant test.
 *
 * @author gudaoxuri
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = DewIdentApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ResourceTest extends BasicTest {

    @Test
    public void testAll() {
        Long groupId = postToEntity("/console/resource/group", AddResourceGroupReq.builder()
                .name("默认资源组")
                .build(), Long.class).getBody();

        postToEntity("/console/resource", AddResourceReq.builder()
                .kind(Resource.Kind.URI)
                .identifier("/mgr/account/**")
                .method("")
                .name("账号管理")
                .parentId(groupId)
                .build(), Long.class);

        Long resId = postToEntity("/console/resource", AddResourceReq.builder()
                .kind(Resource.Kind.URI)
                .identifier("/mgr/tenant")
                .method("GET")
                .name("租户列表")
                .parentId(groupId)
                .build(), Long.class).getBody();

        putToEntity("/console/resource/" + resId, ModifyResourceReq.builder()
                .id(resId)
                .name("获取租户列表")
                .build(), Long.class);

        List<ResourceInfoResp> resources = getToList("/console/resource/", ResourceInfoResp.class).getBody();
        Assert.assertEquals(3, resources.size());

        var fetchTenantListResId = resources.stream()
                .filter(res -> res.getName().equalsIgnoreCase("获取租户列表"))
                .findAny().get().getId();

        ResourceInfoResp resource = getToEntity("/console/resource/" + fetchTenantListResId, ResourceInfoResp.class).getBody();
        Assert.assertEquals("获取租户列表", resource.getName());

        deleteToEntity("/console/resource/" + fetchTenantListResId, Void.class);

        resources = getToList("/console/resource/", ResourceInfoResp.class).getBody();
        Assert.assertEquals(2, resources.size());
    }

}
