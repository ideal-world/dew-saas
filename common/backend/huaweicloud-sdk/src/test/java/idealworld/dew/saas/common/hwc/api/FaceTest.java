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
import idealworld.dew.saas.common.hwc.api.face.Face;
import idealworld.dew.saas.common.hwc.api.face.FaceAddResp;
import idealworld.dew.saas.common.hwc.api.face.FaceSearchResp;
import idealworld.dew.saas.common.hwc.api.face.FaceSetResp;
import idealworld.dew.saas.common.hwc.api.obs.OBS;
import org.junit.Assert;

import java.io.File;
import java.util.List;

/**
 * The type face test.
 *
 * @author gudaoxuri
 */
public class FaceTest extends BasicTest {


    /**
     * Test face.
     *
     * @throws InterruptedException the interrupted exception
     */
    /*@Test*/
    public void testFace() throws InterruptedException {
        OBS obs = HWC.obs;
        obs.host(OBS_HOST).auth(AK, SK);
        obs.put("/test/example.jpg", new File(OBSTest.class.getResource("/").getPath() + "photo/example.jpg"));
        obs.put("/test/1-1.jpg", new File(OBSTest.class.getResource("/").getPath() + "photo/1-1.jpg"));
        obs.put("/test/1-2.jpg", new File(OBSTest.class.getResource("/").getPath() + "photo/1-2.jpg"));
        obs.put("/test/1-3.jpg", new File(OBSTest.class.getResource("/").getPath() + "photo/1-3.jpg"));
        obs.put("/test/2-1.jpg", new File(OBSTest.class.getResource("/").getPath() + "photo/2-1.jpg"));
        obs.put("/test/2-2.jpg", new File(OBSTest.class.getResource("/").getPath() + "photo/2-2.jpg"));
        obs.put("/test/0-1.jpg", new File(OBSTest.class.getResource("/").getPath() + "photo/0-1.jpg"));
        obs.put("/test/0-2.jpg", new File(OBSTest.class.getResource("/").getPath() + "photo/0-2.jpg"));

        Face face = HWC.face;
        face.host(FACE_HOST).projectId(PROJECT_ID).auth(AK, SK);

        // FaceSet
        face.findAllFaceSet().forEach(faceSet -> face.deleteFaceSet(faceSet.getFaceSetName()));
        face.createFaceSet("test-faceset");
        FaceSetResp faceSetResp = face.getFaceSet("test-faceset").get();
        Assert.assertEquals(0, faceSetResp.getFaceNumber().longValue());
        Assert.assertEquals(100000, faceSetResp.getFaceCapacity().longValue());
        List<FaceSetResp> faceSetResps = face.findAllFaceSet();
        Assert.assertEquals(1, faceSetResps.size());
        Assert.assertEquals(0, faceSetResps.get(0).getFaceNumber().longValue());

        // Face
        List<FaceAddResp> faces = face.addFace(obs.get("/test/1-1.jpg", 60 * 5), "test-faceset");
        Assert.assertEquals(1, faces.size());
        final String faceId1 = faces.get(0).getFaceId();
        faces = face.addFace(obs.get("/test/0-1.jpg", 60 * 5), "test-faceset");
        Assert.assertEquals(2, faces.size());
        Thread.sleep(1000);
        faceSetResp = face.getFaceSet("test-faceset").get();
        Assert.assertEquals(3, faceSetResp.getFaceNumber().longValue());

        face.deleteFaceByFaceId(faces.get(0).getFaceId(), "test-faceset");
        Thread.sleep(1000);
        faceSetResp = face.getFaceSet("test-faceset").get();
        Assert.assertEquals(2, faceSetResp.getFaceNumber().longValue());
        // Search
        List<FaceSearchResp> faceSearchResps = face.searchFaceByImageUrl(obs.get("/test/1-2.jpg", 60 * 5), "test-faceset");
        Assert.assertEquals(2, faceSearchResps.size());
        Assert.assertEquals(faceId1, faceSearchResps.get(0).getFaceId());

        // FaceSet
        face.deleteFaceSet("test-faceset");
    }
}
