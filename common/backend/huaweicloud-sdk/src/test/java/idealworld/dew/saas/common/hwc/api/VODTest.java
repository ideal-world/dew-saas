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

package idealworld.dew.saas.common.hwc.api;

import com.ecfront.dew.common.$;
import idealworld.dew.saas.common.hwc.BasicTest;
import idealworld.dew.saas.common.hwc.api.obs.OBS;
import idealworld.dew.saas.common.hwc.api.vod.AssetResp;
import idealworld.dew.saas.common.hwc.api.vod.VOD;
import org.junit.Test;

import java.io.File;
import java.net.URISyntaxException;

/**
 * The type Vod test.
 *
 * @author gudaoxuri
 */
public class VODTest extends BasicTest {

    /**
     * Test obs.
     */
    @Test
    public void testVOD() throws URISyntaxException {
        VOD vod = HWC.vod;
        vod.host(VOD_HOST).accountName(ACCOUNT_NAME).projectId(PROJECT_ID).auth(AK_IAM, SK_IAM);
        AssetResp assetResp = vod.asset("测试", "", "test.mp4");
        System.out.println("AssertId:" + assetResp.getAssertId());
        System.out.println("videoUploadUrl:" + assetResp.getVideoUploadUrl());
        $.http.post(assetResp.getVideoUploadUrl(), new File(this.getClass().getResource("/").getPath() + "test.mp4"));
        String playUrl = vod.publish(assetResp.getAssertId());
        System.out.println("playUrl:" + playUrl);

        // OBS转存
        OBS obs = HWC.obs;
        obs.host(OBS_HOST).auth(AK, SK);
        obs.put("/vod/test.mp4", new File(this.getClass().getResource("/").getPath() + "test.mp4"));
        String assertId = vod.fetchByOBS("测试", "MP4", "iw-test", "cn-north-4", "test.mp4");
        playUrl = vod.fetchUrl(assertId);
        System.out.println("playUrl:" + playUrl);
    }
}
