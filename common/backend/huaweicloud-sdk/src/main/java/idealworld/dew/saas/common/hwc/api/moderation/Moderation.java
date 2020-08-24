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

package idealworld.dew.saas.common.hwc.api.moderation;

import com.ecfront.dew.common.$;
import com.ecfront.dew.common.HttpHelper;
import com.fasterxml.jackson.databind.JsonNode;
import idealworld.dew.saas.common.hwc.api.common.BasicProcessor;
import idealworld.dew.saas.common.hwc.api.common.OptException;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 内容审核操作.
 *
 * @author gudaoxuri
 * @link https ://support.huaweicloud.com/api-moderation/moderation_03_0022.html
 */
public class Moderation extends BasicProcessor<Moderation> {

    @Override
    protected JsonNode respFilter(HttpHelper.ResponseWrap response) {
        JsonNode json = $.json.toJson(response.result);
        if (json.has("error_code")) {
            throw new OptException(json.get("error_code").asText(), json.get("error_msg").asText(), response.result);
        }
        return json;
    }

    /**
     * 文本审核.
     *
     * @param text       文本内容
     * @param categories 审核类型
     * @return 审核结果 text moderation result
     * @link https ://support.huaweicloud.com/api-moderation/moderation_03_0018.html
     */
    public TextModerationResp text(String text, TextCategory... categories) {
        String cate = Stream.of(categories).map(c -> "\"" + c.getKey() + "\"").collect(Collectors.joining(","));
        JsonNode response = respFilter(http.postWrap(url + "/v1.0/moderation/text",
                "{\n"
                        + "   \"categories\":[" + cate + "],\n"
                        + "   \"items\":[\n"
                        + "      {\n"
                        + "          \"text\": \"" + text + "\",\n"
                        + "          \"type\": \"content\"\n"
                        + "      }\n"
                        + "    ]\n"
                        + "}"));
        JsonNode result = response.get("result");
        return new TextModerationResp()
                .builder()
                .suggestion(Suggestion.fromKey(result.get("suggestion").asText()).get())
                .hitDetail(
                        $.fun.stream(result.get("detail").fields())
                                .collect(Collectors.toMap(
                                        json -> TextCategory.fromKey(json.getKey()).get(),
                                        json -> $.fun.stream(json.getValue().iterator())
                                                .map(JsonNode::asText)
                                                .collect(Collectors.toSet())
                                        )
                                )
                )
                .build();
    }

    /**
     * 图片审核.
     *
     * @param imageUrl   审核图片
     * @param categories 审核类型
     * @return 审核结果 image moderation result
     * @link https ://support.huaweicloud.com/api-moderation/moderation_03_0019.html
     */
    public ImageModerationResp image(String imageUrl, ImageCategory... categories) {
        return image(imageUrl, -1, categories);
    }

    /**
     * 图片审核.
     *
     * @param imageUrl   审核图片
     * @param threshold  结果过滤门限，只有置信度不低于此门限的结果才会呈现在detail的列表中，取值范围 0-1，
     *                   当未设置此值时各个检测场景会使用各自的默认值。
     *                   politics检测场景的默认值为0.95
     *                   terrorism检测场景的默认值为0
     *                   ad检测场景的默认值为0
     * @param categories 审核类型
     * @return 审核结果 image moderation result
     * @link https ://support.huaweicloud.com/api-moderation/moderation_03_0019.html
     */
    public ImageModerationResp image(String imageUrl, float threshold, ImageCategory... categories) {
        String strThreshold = threshold == -1 ? "" : threshold + "";
        String cate = Stream.of(categories).map(c -> "\"" + c.getKey() + "\"").collect(Collectors.joining(","));
        JsonNode response = respFilter(http.postWrap(url + "/v1.0/moderation/image",
                "{\n"
                        + "   \"categories\":[" + cate + "],\n"
                        + "   \"url\":\"" + imageUrl + "\",\n"
                        + "   \"image\":\"\",\n"
                        + "   \"threshold\":\"" + strThreshold + "\"\n"
                        + "}"));
        JsonNode result = response.get("result");
        return packageImageModerationResult(imageUrl, result);
    }

    /**
     * 图片批量审核.
     *
     * @param imageUrls  审核图片列表
     * @param categories 审核类型
     * @return 审核结果 list
     * @link https ://support.huaweicloud.com/api-moderation/moderation_03_0036.html
     */
    public List<ImageModerationResp> image(List<String> imageUrls, ImageCategory... categories) {
        return image(imageUrls, -1, categories);
    }

    /**
     * 图片批量审核.
     *
     * @param imageUrls  审核图片列表
     * @param threshold  结果过滤门限，只有置信度不低于此门限的结果才会呈现在detail的列表中，
     *                   取值范围 0-1，当未设置此值时各个检测场景会使用各自的默认值。
     *                   politics检测场景的默认值为0.95
     *                   terrorism检测场景的默认值为0
     *                   ad检测场景的默认值为0
     * @param categories 审核类型
     * @return 审核结果 list
     * @link https ://support.huaweicloud.com/api-moderation/moderation_03_0036.html
     */
    public List<ImageModerationResp> image(List<String> imageUrls, float threshold, ImageCategory... categories) {
        String strThreshold = threshold == -1 ? "" : threshold + "";
        String cate = Stream.of(categories).map(c -> "\"" + c.getKey() + "\"").collect(Collectors.joining(","));
        JsonNode response = respFilter(http.postWrap(url + "/v1.0/moderation/image/batch",
                "{\n"
                        + "   \"categories\":[" + cate + "],\n"
                        + "   \"urls\":" + imageUrls.stream().map(i -> "\"" + i + "\"").collect(Collectors.joining(",", "[", "]")) + ",\n"
                        + "   \"threshold\":\"" + strThreshold + "\"\n"
                        + "}"));
        JsonNode result = response.get("result");
        return $.fun.stream(result.iterator())
                .map(res -> packageImageModerationResult(res.get("url").asText(), res))
                .collect(Collectors.toList());
    }

    private ImageModerationResp packageImageModerationResult(String imageUrl, JsonNode result) {
        return new ImageModerationResp()
                .builder()
                .url(imageUrl)
                .suggestion(Suggestion.fromKey(result.get("suggestion").asText()).get())
                .hitDetail(
                        $.fun.stream(result.get("detail").fields())
                                .filter(cateDetail ->
                                        // Batch 下不存在 category_suggestions
                                        !result.has("category_suggestions")
                                                || Suggestion.fromKey(
                                                result.get("category_suggestions").get(cateDetail.getKey()).asText())
                                                .get() != Suggestion.PASS)
                                .collect(Collectors.toMap(
                                        cateDetail -> ImageCategory.fromKey(cateDetail.getKey()).get(),
                                        cateDetail -> new ImageModerationResp.CategoryDetail()
                                                .builder()
                                                .suggestion(
                                                        result.has("category_suggestions")
                                                                ? Suggestion.fromKey(
                                                                result.get("category_suggestions").get(cateDetail.getKey()).asText())
                                                                .get()
                                                                : null
                                                )
                                                .details(
                                                        $.fun.stream(cateDetail.getValue().iterator())
                                                                .filter(detail -> detail.get("confidence").asDouble() != 0)
                                                                .map(detail ->
                                                                        new ImageModerationResp.CategoryDetail.Detail()
                                                                                .builder()
                                                                                .confidence(detail.get("confidence").asDouble())
                                                                                .label(detail.get("label").asText())
                                                                                .build())
                                                                .collect(Collectors.toList())
                                                )
                                                .build()
                                        )
                                )
                )
                .build();
    }

}
