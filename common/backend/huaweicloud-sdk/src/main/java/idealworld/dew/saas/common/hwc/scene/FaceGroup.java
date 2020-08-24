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

import com.ecfront.dew.common.$;
import com.ecfront.dew.common.exception.RTException;
import idealworld.dew.saas.common.hwc.api.common.util.Similar;
import idealworld.dew.saas.common.hwc.api.face.Face;
import idealworld.dew.saas.common.hwc.api.face.FaceAddResp;
import idealworld.dew.saas.common.hwc.api.face.FaceImageKind;
import idealworld.dew.saas.common.hwc.api.face.FaceSearchResp;
import idealworld.dew.saas.common.hwc.api.obs.OBS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

/**
 * 人脸分组场景.
 *
 * @author gudaoxuri
 */
public class FaceGroup {

    private static final Logger logger = LoggerFactory.getLogger(FaceGroup.class);
    private static final Random RANDOM = new Random();
    private static final float FACE_SIMILARITY = 0.93f;
    private static final float COLLECTION_SIMILARITY = 0.8f;
    private static final int FACE_SEARCH_NUMBER = 5;

    private Face face;
    private OBS obs;
    private String faceSetName;
    private DBProcessor processor;

    private FaceGroup() {
    }

    /**
     * Instantiates a new Face group.
     *
     * @param face        the face
     * @param obs         the obs
     * @param faceSetName the face set name
     * @param processor   the processor
     */
    public FaceGroup(Face face, OBS obs, String faceSetName, DBProcessor processor) {
        this.face = face;
        this.obs = obs;
        this.faceSetName = faceSetName;
        this.processor = processor;
        if (!face.getFaceSet(faceSetName).isPresent()) {
            face.createFaceSet(faceSetName);
        }
        autoCleanFaces();
    }

    private void autoCleanFaces() {
        // 每天清理未被使用的人脸
        $.timer.periodic(60 + RANDOM.nextInt(10), 60L * 60 * 24, false, () -> {
            if (processor.isLeader()) {
                logger.info("Auto clean unused faces");
                processor.getUnusedFaceIds().forEach(this::removeFaceId);
            }
        });
    }

    /**
     * 人脸分组.
     *
     * @param imagePaths OBS中的图片路径，图片路径及名称不要出现中文
     * @return 分组结果 map
     */
    public Map<String, List<FaceGroupResult>> grouping(List<String> imagePaths) {
        return grouping(imagePaths, FACE_SIMILARITY, COLLECTION_SIMILARITY);
    }

    /**
     * 人脸分组.
     *
     * @param imagePaths          OBS中的图片路径，图片路径及名称不要出现中文
     * @param faceThreshold       人脸比对阈值，(0-1),建议0.93
     * @param collectionThreshold 集合相似度匹配阈值，(0-1)
     * @return 分组结果 map
     */
    public Map<String, List<FaceGroupResult>> grouping(List<String> imagePaths, float faceThreshold, float collectionThreshold) {
        return grouping(imagePaths, faceThreshold, collectionThreshold, 2);
    }

    /**
     * 人脸分组.
     *
     * @param imagePaths          OBS中的图片路径，图片路径及名称不要出现中文
     * @param faceThreshold       人脸比对阈值，(0-1),建议0.93
     * @param collectionThreshold 集合相似度匹配阈值，(0-1)
     * @param parallelism         并发数
     * @return 分组结果 map
     */
    public Map<String, List<FaceGroupResult>> grouping(List<String> imagePaths, float faceThreshold, float collectionThreshold, int parallelism) {
        try {
            // 相似的人脸Id集合：faceId -> 权重，使用similarity（0-10）
            List<Map<String, Double>> similarFaceIds = new ArrayList<>();
            // 所有的人脸信息
            List<FaceGroupResult> faceInfos = new ForkJoinPool(parallelism).submit(() -> imagePaths.stream()
                    .parallel()
                    .flatMap(path -> {
                        String url = obs.get(path, 60 * 20);
                        // 图片中找到的人脸集合
                        List<FaceAddResp> matchedFaceResult = face.addFace(url, faceSetName);
                        if (matchedFaceResult.size() == 0) {
                            // 没有匹配到的人脸
                            return new ArrayList<FaceGroupResult>() {
                                {
                                    add(new FaceGroupResult()
                                            .setImagePath(path)
                                            .setImageUrl(url)
                                            .setCurrentFaceId("")
                                            .setMatchedUserId(""));
                                }
                            }.stream();
                        } else {
                            // 匹配到的人脸
                            return matchedFaceResult.stream()
                                    .map(faceInfo -> {
                                        logger.debug("FaceId [" + faceInfo.getFaceId() + "] at image [" + path + "] add to " + faceSetName);
                                        try {
                                            // 添加人脸到可查询是异步过程，华为没有提供添加成功通知接口，间隔时间只能估算
                                            Thread.sleep(200);
                                        } catch (InterruptedException e) {
                                            throw new RTException(e);
                                        }
                                        List<FaceSearchResp> faceSearchResps =
                                                face.searchFace(
                                                        faceInfo.getFaceId(),
                                                        FaceImageKind.FACE_ID, faceThreshold, FACE_SEARCH_NUMBER, faceSetName);
                                        logger.debug("FaceId [" + faceInfo.getFaceId() + "] at image [" + path + "] "
                                                + "found [" + faceSearchResps.size() + "] similar faces");
                                        if (faceSearchResps.size() == 0) {
                                            return new FaceGroupResult()
                                                    .setImagePath(path)
                                                    .setImageUrl(url)
                                                    .setCurrentFaceId(faceInfo.getFaceId())
                                                    .setMatchedUserId("");
                                        }
                                        logger.debug("FaceId [" + faceInfo.getFaceId() + "] at image [" + path + "] "
                                                + "similar detail:" + $.json.toJsonString(faceSearchResps));
                                        // 添加到匹配到的人脸Id到集合
                                        Map<String, Double> similar = faceSearchResps.stream()
                                                .collect(Collectors.toMap(FaceSearchResp::getFaceId, r -> r.getSimilarity() * 10));
                                        // 添加当前的人脸Id，权重为10
                                        similar.put(faceInfo.getFaceId(), 10D);
                                        similarFaceIds.add(similar);
                                        // 自增人脸Id的命中次数
                                        faceSearchResps.forEach(faceSearchResp ->
                                                processor.addFaceIdHitTimes(faceSearchResp.getFaceId())
                                        );
                                        return new FaceGroupResult()
                                                .setImagePath(path)
                                                .setImageUrl(url)
                                                .setCurrentFaceId(faceInfo.getFaceId())
                                                // 根据人脸Id找用户Id
                                                .setMatchedUserId(processor.getUserIdByFaceId(faceSearchResps.get(0).getFaceId()).orElse(""))
                                                .setMatchedBoxTopX(faceInfo.getBoxTopX())
                                                .setMatchedBoxTopY(faceInfo.getBoxTopY())
                                                .setMatchedBoxWidth(faceInfo.getBoxWidth())
                                                .setMatchedBoxHeight(faceInfo.getBoxHeight());
                                    });
                        }
                    })
                    .collect(Collectors.toList())).get();
            // 合并相似的人脸Id集合
            List<Set<String>> mergedSimilarCollection = Similar.merge(similarFaceIds, collectionThreshold);

            // 对已匹配用户Id的集合作相似度匹配，根据相似人脸Id集合关联到已匹配的用户Id集合中
            List<FaceGroupResult> namedGroupResult = findFaceInfoByNamed(faceInfos, true);
            mergedSimilarCollection.forEach(
                    // 相似的人脸Id集合
                    faceIds ->
                            namedGroupResult.stream()
                                    .filter(result ->
                                            faceIds.stream()
                                                    .anyMatch(faceId ->
                                                            result.getCurrentFaceId().equalsIgnoreCase(faceId)))
                                    .map(FaceGroupResult::getMatchedUserId)
                                    .findAny()
                                    .ifPresent(s ->
                                            // 当前人脸Id集合中存在已匹配到用户Id
                                            // 那么找到当前人脸Id中所有未匹配到用户Id的子集
                                            findFaceInfoByNamed(faceInfos, false).stream()
                                                    .filter(result ->
                                                            faceIds.stream()
                                                                    .anyMatch(faceId ->
                                                                            result.getCurrentFaceId().equalsIgnoreCase(faceId)))
                                                    // 将这些子集都归到对应的用户Id中
                                                    .forEach(result -> result.setMatchedUserId(s))
                                    ));

            // 根据相似人脸Id集合对未匹配用户Id的集合作分组
            mergedSimilarCollection.forEach(
                    // 相似的人脸Id集合
                    faceIds -> {
                        // 虚拟用户Id
                        String dummyUserId = processor.getdummyUserIdPrefix() + $.field.createShortUUID();
                        // 从未匹配到用户Id的集合中找到所有当前人脸Id的子集
                        findFaceInfoByNamed(faceInfos, false).stream()
                                .filter(result -> faceIds.stream().anyMatch(faceId -> result.getCurrentFaceId().equalsIgnoreCase(faceId)))
                                // 为这些子集赋值一个虚拟用户Id
                                .forEach(result -> result.setMatchedUserId(dummyUserId));
                    });

            // 为没有在相似人脸Id集合 & 未匹配用户Id的集合添加虚拟用户Id
            findFaceInfoByNamed(faceInfos, false)
                    .forEach(result -> result.setMatchedUserId(processor.getdummyUserIdPrefix() + $.field.createShortUUID()));
            // 返回根据用户Id分组的结果
            return faceInfos.stream().collect(Collectors.groupingBy(FaceGroupResult::getMatchedUserId));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RTException(e);
        } catch (ExecutionException e) {
            throw new RTException(e);
        }
    }

    private List<FaceGroupResult> findFaceInfoByNamed(List<FaceGroupResult> groups, boolean named) {
        return groups.stream()
                .filter(r -> named != r.getMatchedUserId().equalsIgnoreCase(""))
                .collect(Collectors.toList());
    }


    /**
     * 添加人脸Id与用户Id映射关系.
     *
     * @param faceId the face id
     * @param userId the user id
     */
    public void addFaceIdAndUserIdRel(String faceId, String userId) {
        processor.addFaceIdAndUserIdRel(faceId, userId);
    }

    /**
     * 删除人脸Id.
     *
     * @param faceId the face id
     */
    public void removeFaceId(String faceId) {
        face.deleteFaceByFaceId(faceId, faceSetName);
        processor.deleteFaceId(faceId);
    }

    /**
     * 数据库操作.
     */
    public interface DBProcessor {

        /**
         * Get dummy user id prefix.
         *
         * @return the dummy user id prefix
         */
        default String getdummyUserIdPrefix() {
            return ".";
        }

        /**
         * Add face id hit times.
         *
         * @param faceId the face id
         */
        void addFaceIdHitTimes(String faceId);

        /**
         * Add face id and user id rel.
         *
         * @param faceId the face id
         * @param userId the user id
         */
        void addFaceIdAndUserIdRel(String faceId, String userId);

        /**
         * Is leader.
         *
         * @return the boolean
         */
        boolean isLeader();

        /**
         * Delete face id.
         *
         * @param faceId the face id
         */
        void deleteFaceId(String faceId);

        /**
         * Gets user id by face id.
         *
         * @param faceId the face id
         * @return the user id by face id
         */
        Optional<String> getUserIdByFaceId(String faceId);

        /**
         * Gets unused face ids.
         *
         * @return the unused face ids
         */
        List<String> getUnusedFaceIds();

    }

}
