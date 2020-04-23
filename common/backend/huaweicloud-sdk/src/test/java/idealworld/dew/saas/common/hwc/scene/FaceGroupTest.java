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

package idealworld.dew.saas.common.hwc.scene;

import com.ecfront.dew.common.tuple.Tuple4;
import idealworld.dew.saas.common.hwc.BasicTest;
import idealworld.dew.saas.common.hwc.api.HWC;
import idealworld.dew.saas.common.hwc.api.OBSTest;
import org.junit.Assert;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The type Face group test.
 *
 * @author gudaoxuri
 */
public class FaceGroupTest extends BasicTest {

    /**
     * Test group.
     */
    /*@Test*/
    public void testGroup() {
        HWC.obs.host(OBS_HOST).auth(AK, SK);
        HWC.face.host(FACE_HOST).projectId(PROJECT_ID).auth(AK, SK);

        List<String> imagePaths = Stream.of(new File(OBSTest.class.getResource("/").getPath() + "facegroup").listFiles())
                .map(f -> {
                    //  HWC.obs.put("/group/" + f.getName(), f);
                    return "/group/" + f.getName();
                })
                .collect(Collectors.toList());

        if (HWC.face.getFaceSet("group-set").isPresent()) {
            HWC.face.deleteFaceSet("group-set");
        }

        FaceGroup faceGroup = HWC.faceGroup(HWC.face, HWC.obs, "group-set", new FaceGroup.DBProcessor() {

            // FaceId,UserId,HitTimes,UpdateTime
            private final List<Tuple4<String, String, Integer, Long>> faceInfoTable = new ArrayList<>();

            @Override
            public void addFaceIdHitTimes(String faceId) {
                Optional<Tuple4<String, String, Integer, Long>> storageOpt = faceInfoTable.stream()
                        .filter(f -> f._0.equalsIgnoreCase(faceId))
                        .findAny();
                if (storageOpt.isPresent()) {
                    storageOpt.get()._2 = storageOpt.get()._2 + 1;
                    storageOpt.get()._3 = System.currentTimeMillis();
                } else {
                    faceInfoTable.add(new Tuple4<>(faceId, "", 1, System.currentTimeMillis()));
                }
            }

            @Override
            public void addFaceIdAndUserIdRel(String faceId, String userId) {
                Optional<Tuple4<String, String, Integer, Long>> storageOpt = faceInfoTable.stream()
                        .filter(f -> f._0.equalsIgnoreCase(faceId))
                        .findAny();
                if (storageOpt.isPresent()) {
                    storageOpt.get()._1 = userId;
                } else {
                    faceInfoTable.add(new Tuple4<>(faceId, userId, 1, System.currentTimeMillis()));
                }
            }

            @Override
            public boolean isLeader() {
                return true;
            }

            @Override
            public void deleteFaceId(String faceId) {
                Optional<Tuple4<String, String, Integer, Long>> storageOpt = faceInfoTable.stream()
                        .filter(f -> f._0.equalsIgnoreCase(faceId))
                        .findAny();
                storageOpt.ifPresent(faceInfoTable::remove);
            }

            @Override
            public Optional<String> getUserIdByFaceId(String faceId) {
                return faceInfoTable.stream()
                        .filter(f -> f._0.equalsIgnoreCase(faceId))
                        .findAny()
                        .map(f -> f._1);
            }

            @Override
            public List<String> getUnusedFaceIds() {
                List<String> unusedFaceIds = faceInfoTable.stream()
                        // 1个月前
                        .filter(f -> f._3 < System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 30)
                        // 命中1次
                        .filter(f -> f._2 < 2)
                        // 的人脸Id
                        .map(f -> f._0)
                        .collect(Collectors.toList());
                return unusedFaceIds;
            }
        });

        Map<String, List<FaceGroupResult>> faceGroupResult = faceGroup.grouping(imagePaths, 0.91f, 0.8f);
        // 第一次，都没有匹配到
        // Assert.assertEquals(4, faceGroupResult.size());
        // 都为虚拟用户Id
        Assert.assertEquals(0, faceGroupResult.values().stream()
                .flatMap(Collection::stream).filter(f -> !f.getMatchedUserId().startsWith(".")).count());
        // 给星航设置用户标签
        faceGroupResult.values().stream()
                .flatMap(Collection::stream)
                .filter(faceResult -> faceResult.getImagePath().contains("jxh1") || faceResult.getImagePath().contains("jxh2"))
                .forEach(faceResult -> faceGroup.addFaceIdAndUserIdRel(faceResult.getCurrentFaceId(), "jxh"));

        faceGroupResult = faceGroup.grouping(imagePaths, 0.91f, 0.8f);
        // 第二次
        Assert.assertEquals(4, faceGroupResult.size());
    }
}
