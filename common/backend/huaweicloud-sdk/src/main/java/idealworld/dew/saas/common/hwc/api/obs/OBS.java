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

package idealworld.dew.saas.common.hwc.api.obs;

import idealworld.dew.saas.common.hwc.api.common.BasicProcessor;
import idealworld.dew.saas.common.hwc.api.common.OptException;
import idealworld.dew.saas.common.hwc.api.common.auth.OBSSigner;
import org.joox.JOOX;
import org.joox.Match;

import java.net.MalformedURLException;
import java.util.Map;

/**
 * OBS操作.
 *
 * @author gudaoxuri
 * @link https ://support.huaweicloud.com/api-obs/zh-cn_topic_0100846747.html
 */
public class OBS extends BasicProcessor<OBS> {

    @Override
    protected String respFilter(String response) {
        if (!response.isEmpty()) {
            Match doc = JOOX.$(response);
            if (!doc.matchTag("Error").isEmpty()) {
                throw new OptException(doc.find("Code").get(0).getTextContent(),
                        doc.find("Message").get(0).getTextContent(), response);
            }
        }
        return response;
    }

    /**
     * 添加或更新文件.
     *
     * @param path 文件路径
     * @param obj  文件对象
     * @link https ://support.huaweicloud.com/api-obs/zh-cn_topic_0100846775.html#section2
     */
    public void put(String path, Object obj) {
        respFilter(http.put(url + path, obj));
    }

    /**
     * 删除文件.
     *
     * @param path 文件路径
     * @link https ://support.huaweicloud.com/api-obs/zh-cn_topic_0100846782.html
     */
    public void delete(String path) {
        respFilter(http.delete(url + path));
    }

    /**
     * 获取文件地址.
     *
     * @param path      文件路径
     * @param expireSec 访问过期时间
     * @return 文件地址 string
     */
    public String get(String path, long expireSec) {
        try {
            return ((OBSSigner) signer).signByUrl("GET", "",url + path, expireSec);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取签名后的地址.
     *
     * @param method      the method
     * @param contentType the content type
     * @param path        文件路径
     * @param expireSec   访问过期时间
     * @return 文件地址 sign url
     */
    public String signUrl(String method,String contentType, String path, long expireSec) {
        try {
            return ((OBSSigner) signer).signByUrl(method, contentType,url + path, expireSec);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取签名后的地址.
     *
     * @param path      文件路径
     * @param expireSec 访问过期时间
     * @return 文件地址 sign url
     */
    public Map<String,String> signByPostRequest(String path, long expireSec) {
        try {
            return ((OBSSigner) signer).signByPostRequest(url + path, expireSec);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取原始图片.
     *
     * @param path      图片路径
     * @param expireSec 访问过期时间
     * @return 图片地址 string
     */
    public String image(String path, long expireSec) {
        return get(path, expireSec);
    }

    /**
     * 获取缩略图.
     *
     * @param path      图片路径
     * @param expireSec 访问过期时间
     * @param width     宽（像素）
     * @param height    长（像素）
     * @return 图片地址 string
     */
    public String image(String path, long expireSec, int width, int height) {
        return image(path, expireSec, width, height, 100);
    }

    /**
     * 获取缩略图，改变图片质量.
     *
     * @param path      图片路径
     * @param expireSec 访问过期时间
     * @param width     宽（像素）
     * @param height    长（像素）
     * @param quality   质量（百分比）
     * @return 图片地址 string
     * @link https ://support.huaweicloud.com/fg-obs/obs_01_0480.html
     */
    public String image(String path, long expireSec, int width, int height, int quality) {
        return get(path + "?x-image-process=image/resize,w_" + width + ",h_" + height + "/quality,q_" + quality, expireSec);
    }

}
