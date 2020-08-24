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

package idealworld.dew.saas.common.hwc.api.vod;

import com.ecfront.dew.common.$;
import com.ecfront.dew.common.HttpHelper;
import com.fasterxml.jackson.databind.JsonNode;
import idealworld.dew.saas.common.hwc.api.common.BasicProcessor;
import idealworld.dew.saas.common.hwc.api.common.OptException;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Vod.
 *
 * @author gudaoxuri
 */
public class VOD extends BasicProcessor<VOD> {

    private static final List<String> SUPPORT_TYPES = new ArrayList<>();

    static {
        SUPPORT_TYPES.add("MP4");
        SUPPORT_TYPES.add("TS");
        SUPPORT_TYPES.add("MOV");
        SUPPORT_TYPES.add("MXF");
        SUPPORT_TYPES.add("MPG");
        SUPPORT_TYPES.add("FLV");
        SUPPORT_TYPES.add("WMV");
        SUPPORT_TYPES.add("AVI");
        SUPPORT_TYPES.add("M4V");
        SUPPORT_TYPES.add("F4V");
        SUPPORT_TYPES.add("MPEG");
        SUPPORT_TYPES.add("3GP");
        SUPPORT_TYPES.add("ASF");
        SUPPORT_TYPES.add("MKV");
        SUPPORT_TYPES.add("HLS");
        SUPPORT_TYPES.add("MP3");
        SUPPORT_TYPES.add("OGG");
        SUPPORT_TYPES.add("WAV");
        SUPPORT_TYPES.add("WMA");
        SUPPORT_TYPES.add("APE");
        SUPPORT_TYPES.add("FLAC");
        SUPPORT_TYPES.add("AAC");
        SUPPORT_TYPES.add("AC3");
        SUPPORT_TYPES.add("MMF");
        SUPPORT_TYPES.add("AMR");
        SUPPORT_TYPES.add("M4A");
        SUPPORT_TYPES.add("M4R");
        SUPPORT_TYPES.add("WV");
        SUPPORT_TYPES.add("MP2");
    }

    @Override
    protected JsonNode respFilter(HttpHelper.ResponseWrap response) {
        if (response.statusCode != 200) {
            throw new OptException(response.statusCode + "", "VOD request error", response.result);
        }
        return $.json.toJson(response.result);
    }

    /**
     * Asset.
     *
     * @param title       the title
     * @param description the description
     * @param videoName   the video name
     * @return the asset resp
     */
    public AssetResp asset(String title, String description, String videoName) {
        String videoType = videoName.substring(videoName.lastIndexOf(".") + 1).toUpperCase();
        if (!SUPPORT_TYPES.contains(videoType)) {
            throw new OptException("", "The video type of " + videoType + " isn't supported.", "");
        }
        JsonNode assertResp = respFilter(http.postWrap(url + "/v1.0/" + projectId + "/asset",
                "{\n" +
                        "\t\"title\": \"" + title + "\",\n" +
                        "\t\"description\": \"" + description + "\",\n" +
                        "\t\"video_name\": \"" + videoName + "\",\n" +
                        "\t\"video_type\": \"" + videoType + "\"\n" +
                        "}"));
        return AssetResp.builder()
                .assertId(assertResp.get("asset_id").asText())
                .videoUploadUrl(assertResp.get("video_upload_url").asText())
                .build();
    }

    /**
     * Publish assert.
     *
     * @param assertId the assert id
     * @return play url
     */
    public String publish(String assertId) {
        // confirmAssetUpload
        respFilter(http.postWrap(url + "/v1.0/" + projectId + "/asset/status/uploaded",
                "{\n" +
                        "\t\"asset_id\": \"" + assertId + "\",\n" +
                        "\t\"status\": \"CREATED\"\n" +
                        "}"));
        // https://support.huaweicloud.com/api-vod/vod_04_0020.html
        JsonNode publishResp = respFilter(http.postWrap(url + "/v1.0/" + projectId + "/asset/status/publish",
                "{\n" +
                        "\t\"asset_id\": [\"" + assertId + "\"]\n" +
                        "}")).get("asset_info_array").get(0);
        if (!publishResp.get("status").asText().equalsIgnoreCase("PUBLISHED")) {
            throw new OptException(publishResp.get("status").asText(), "Assert pulish error.",
                    publishResp.get("description").asText());
        }
        return publishResp.get("play_info_array").get(0).get("url").asText();
    }

    /**
     * Fetch by obs string.
     *
     * @param title     the title
     * @param videoType the video type
     * @param bucket    the bucket
     * @param location  the location
     * @param object    the object
     * @return the string
     */
    public String fetchByOBS(String title, String videoType, String bucket, String location, String object) {
        // https://support.huaweicloud.com/api-vod/vod_04_0201.html
        videoType = videoType.toUpperCase();
        if (!SUPPORT_TYPES.contains(videoType)) {
            throw new OptException("", "The video type of " + videoType + " isn't supported.", "");
        }
        return respFilter(http.postWrap(url + "/v1.0/" + projectId + "/asset/reproduction",
                "{\n" +
                        "    \"input\": {\n" +
                        "        \"bucket\": \"" + bucket + "\",\n" +
                        "        \"location\": \"" + location + "4\",\n" +
                        "        \"object\": \"" + object + "\"\n" +
                        "    },\n" +
                        "    \"title\": \"" + title + "\",\n" +
                        "    \"video_type\": \"" + videoType + "\",\n" +
                        "    \"auto_publish\": 1\n" +
                        "}")).get("asset_id").asText();
    }

    /**
     * Fetch url string.
     *
     * @param assertId the assert id
     * @return the string
     */
    public String fetchUrl(String assertId) {
        return respFilter(http.getWrap(url + "/v1.0/" + projectId + "/asset/details?asset_id=" + assertId + "&categories=transcode_info"))
                .get("transcode_info")
                .get("output")
                .get(0)
                .get("url")
                .asText();
    }

}
