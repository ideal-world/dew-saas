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

package idealworld.dew.saas.common.hwc.api.face;

import com.ecfront.dew.common.$;
import com.ecfront.dew.common.exception.RTException;
import com.fasterxml.jackson.databind.JsonNode;
import idealworld.dew.saas.common.hwc.api.common.BasicProcessor;
import idealworld.dew.saas.common.hwc.api.common.OptException;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 人脸识别操作.
 *
 * @author gudaoxuri
 * @link https ://support.huaweicloud.com/api-face/face_02_0052.html
 */
@Slf4j
public class Face extends BasicProcessor<Face> {

    @Override
    protected JsonNode respFilter(String response) {
        JsonNode json = $.json.toJson(response);
        if (json.has("error_code")) {
            throw new OptException(json.get("error_code").asText(), json.get("error_msg").asText(), response);
        }
        return json;
    }

    /**
     * 创建人脸库.
     *
     * @param faceSetName 人脸库名称 仅支持：英文、数字、中划线
     * @link https ://support.huaweicloud.com/api-face/face_02_0031.html
     */
    public void createFaceSet(String faceSetName) {
        respFilter(http.post(url + "/v1/" + pId + "/face-sets",
                "{\n"
                        + "  \"face_set_name\": \"" + faceSetName + "\"\n"
                        + "}"));
    }

    /**
     * 查找所有人脸库.
     *
     * @return 找到的人脸库
     * @link https://support.huaweicloud.com/api-face/face_02_0032.html
     */
    public List<FaceSetResult> findAllFaceSet() {
        JsonNode response = respFilter(http.get(url + "/v1/" + pId + "/face-sets"));
        return $.fun.stream(response.get("face_sets_info").iterator())
                .map(this::packageFaceSetResult)
                .collect(Collectors.toList());
    }

    /**
     * 获取人脸库.
     *
     * @param faceSetName 人脸库名称
     * @return 人脸库
     * @link https://support.huaweicloud.com/api-face/face_02_0033.html
     */
    public Optional<FaceSetResult> getFaceSet(String faceSetName) {
        JsonNode response = respFilter(http.get(url + "/v1/" + pId + "/face-sets/" + faceSetName));
        return response.get("face_set_info").has("face_set_id")
                ? Optional.of(packageFaceSetResult(response.get("face_set_info")))
                : Optional.empty();
    }

    /**
     * 删除人脸库.
     *
     * @param faceSetName 人脸库名称
     * @link https://support.huaweicloud.com/api-face/face_02_0034.html
     */
    public void deleteFaceSet(String faceSetName) {
        respFilter(http.delete(url + "/v1/" + pId + "/face-sets/" + faceSetName));
    }

    /**
     * 添加人脸.
     *
     * @param faceUrl     人脸URL
     * @param faceSetName 人脸库名称
     * @return 人脸添加信息
     * @link https://support.huaweicloud.com/api-face/face_02_0037.html
     */
    public List<FaceAddResult> addFace(String faceUrl, String faceSetName) {
        return addFace(faceUrl, $.field.createShortUUID(), faceSetName);
    }

    /**
     * 添加人脸.
     *
     * @param faceUrl     人脸URL
     * @param extId       自定义人脸Id（external_image_id）
     * @param faceSetName 人脸库名称
     * @return 人脸添加信息
     * @link https://support.huaweicloud.com/api-face/face_02_0037.html
     */
    public List<FaceAddResult> addFace(String faceUrl, String extId, String faceSetName) {
        try {
            JsonNode response = respFilter(http.post(url + "/v1/" + pId + "/face-sets/" + faceSetName + "/faces",
                    "{\n"
                            + "  \"image_url\":\"" + faceUrl + "\",\n"
                            + "  \"external_image_id\":\"" + extId + "\"\n"
                            + "}"));
            return $.fun.stream(response.get("faces").iterator())
                    .map(face -> new FaceAddResult()
                            .setFaceId(face.get("face_id").asText())
                            .setExtId(face.get("external_image_id").asText())
                            .setBoxTopX(face.get("bounding_box").get("top_left_x").asInt())
                            .setBoxTopY(face.get("bounding_box").get("top_left_y").asInt())
                            .setBoxWidth(face.get("bounding_box").get("width").asInt())
                            .setBoxHeight(face.get("bounding_box").get("height").asInt())
                    )
                    .collect(Collectors.toList());
        } catch (OptException e) {
            if (e.getCode().equalsIgnoreCase("FRS.0404")) {
                // 没有找到人脸
                return new ArrayList<>();
            }
            throw e;
        }
    }

    /**
     * 根据自定义人脸Id删除人脸.
     *
     * @param extId       自定义人脸Id（external_image_id）
     * @param faceSetName 人脸库名称
     * @link https://support.huaweicloud.com/api-face/face_02_0039.html
     */
    public void deleteFaceByExtId(String extId, String faceSetName) {
        respFilter(http.delete(url + "/v1/" + pId + "/face-sets/" + faceSetName + "/faces?external_image_id=" + extId));
    }

    /**
     * 根据人脸Id删除人脸.
     *
     * @param faceId      人脸Id（face_id）
     * @param faceSetName 人脸库名称
     * @link https://support.huaweicloud.com/api-face/face_02_0039.html
     */
    public void deleteFaceByFaceId(String faceId, String faceSetName) {
        respFilter(http.delete(url + "/v1/" + pId + "/face-sets/" + faceSetName + "/faces?face_id=" + faceId));
    }

    /**
     * 根据图片URL做人脸比对.
     *
     * @param imageUrl    图片URL
     * @param faceSetName 人脸库名称
     * @return 命中的人脸
     * @link https://support.huaweicloud.com/api-face/face_02_0035.html
     */
    public List<FaceSearchResult> searchFaceByImageUrl(String imageUrl, String faceSetName) {
        return searchFace(imageUrl, FaceImageKind.IMAGE_URL, 0, 10, faceSetName);
    }

    /**
     * 根据图片URL做人脸比对.
     *
     * @param faceId      导入人脸的FaceId
     * @param faceSetName 人脸库名称
     * @return 命中的人脸
     * @link https://support.huaweicloud.com/api-face/face_02_0035.html
     */
    public List<FaceSearchResult> searchFaceByFaceId(String faceId, String faceSetName) {
        return searchFace(faceId, FaceImageKind.FACE_ID, 0, 10, faceSetName);
    }

    /**
     * 人脸比对.
     *
     * @param image       图片信息
     * @param imageKind   图片信息类型
     * @param threshold   过滤阈值，默认为0，建议0.93
     * @param topN        返回数量
     * @param faceSetName 人脸库名称
     * @return 命中的人脸
     * @link https://support.huaweicloud.com/api-face/face_02_0035.html
     */
    public List<FaceSearchResult> searchFace(String image, FaceImageKind imageKind, double threshold, int topN, String faceSetName) {
        return searchFace(image, imageKind, threshold, topN, faceSetName, 0);
    }

    private List<FaceSearchResult> searchFace(String image, FaceImageKind imageKind, double threshold, int topN, String faceSetName,
                                              int tryTimes) {
        try {
            JsonNode response = respFilter(http.post(url + "/v1/" + pId + "/face-sets/" + faceSetName + "/search",
                    "{\n"
                            + "  \"" + imageKind.getKey() + "\":\"" + image + "\",\n"
                            + "  \"top_n\" : " + topN + ",\n"
                            + "  \"threshold\" : " + threshold + "\n"
                            + "}"));
            return $.fun.stream(response.get("faces").iterator())
                    .map(face -> new FaceSearchResult()
                            .setFaceId(face.get("face_id").asText())
                            .setExtId(face.get("external_image_id").asText())
                            .setSimilarity(face.get("similarity").asDouble()))
                    .filter(face ->
                            // 排除自己
                            imageKind != FaceImageKind.FACE_ID || !face.getFaceId().equalsIgnoreCase(image))
                    .collect(Collectors.toList());
        } catch (OptException e) {
            if (e.getCode().equalsIgnoreCase("FRS.0403") && tryTimes < 5) {
                try {
                    log.warn("[" + (tryTimes + 1) + "] The face id [" + image + "] is not exist, try again.");
                    Thread.sleep(100 * (tryTimes + 2));
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    throw new RTException(ex);
                }
                return searchFace(image, imageKind, threshold, topN, faceSetName, tryTimes + 1);
            } else {
                throw e;
            }
        }
    }

    private FaceSetResult packageFaceSetResult(JsonNode json) {
        return new FaceSetResult()
                .setFaceSetId(json.get("face_set_id").asText())
                .setFaceSetName(json.get("face_set_name").asText())
                .setFaceNumber(json.get("face_number").asLong())
                .setFaceCapacity(json.get("face_set_capacity").asLong())
                .setCreateTime(new Date(json.get("create_date").asLong()));
    }

}
