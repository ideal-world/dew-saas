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

import idealworld.dew.saas.common.hwc.BasicTest;
import idealworld.dew.saas.common.hwc.api.obs.OBS;
import org.junit.Test;

import java.io.File;

/**
 * The type Obs test.
 *
 * @author gudaoxuri
 */
public class OBSTest extends BasicTest {

    /**
     * Test obs.
     */
    @Test
    public void testOBS() {
        OBS obs = HWC.obs;
        obs.host(OBS_HOST).auth(AK, SK);
        obs.put("/test/example.jpg", new File(OBSTest.class.getResource("/").getPath() + "obs/test.jpg"));
        obs.put("/test/d.jpg", new File(OBSTest.class.getResource("/").getPath() + "obs/test.jpg"));
        System.out.println(obs.image("/test/example.jpg", 60 * 5));
        System.out.println(obs.image("/test/example.jpg", 60 * 5, 100, 200, 100));
        obs.delete("/test/d.jpg");

        System.out.println(obs.signByPostRequest("/a/a.mp4", 3600));
        System.out.println(obs.signUrl("GET", "", "/a/a.mp4", 3600));
    }
}
