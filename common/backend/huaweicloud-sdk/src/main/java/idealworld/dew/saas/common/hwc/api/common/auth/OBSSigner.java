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

package idealworld.dew.saas.common.hwc.api.common.auth;

import com.ecfront.dew.common.$;
import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpRequestBase;

import java.net.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The type OBS signer.
 *
 * @author gudaoxuri
 * @link https ://support.huaweicloud.com/api-obs/zh-cn_topic_0100846723.html
 */
public class OBSSigner implements Signer {

    private static final String EEE_DD_MMM_YYYY_HH_MM_SS_Z = "EEE, dd MMM yyyy HH:mm:ss z";
    private static final String ISO8601UTC = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    private static final String AUTH_BY_URL_FLAG = "X-AuthBy-URL";
    private static final String EXPIRE_SEC_FLAG = "X-EXPIRE_SEC";
    private static final long DEFAULT_EXPIRE_SEC = 60 * 10;

    /**
     * The Ak.
     */
    private String ak;
    /**
     * The Sk.
     */
    private String sk;

    /**
     * Instantiates a new Obs signer.
     *
     * @param ak the ak
     * @param sk the sk
     */
    OBSSigner(String ak, String sk) {
        this.ak = ak;
        this.sk = sk;
    }

    @Override
    public void sign(HttpRequestBase request)
            throws URISyntaxException, MalformedURLException {
        String date;
        if (request.containsHeader(AUTH_BY_URL_FLAG)) {
            if (request.containsHeader(EXPIRE_SEC_FLAG)) {
                date = (System.currentTimeMillis() / 1000 + Long.parseLong(request.getHeaders(EXPIRE_SEC_FLAG)[0].getValue())) + "";
            } else {
                date = (System.currentTimeMillis() / 1000 + DEFAULT_EXPIRE_SEC) + "";
            }
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat(EEE_DD_MMM_YYYY_HH_MM_SS_Z, Locale.US);
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            date = sdf.format(new Date());
        }

        String method = request.getMethod();
        String content = "";
        String contentType = request.getHeaders("Content-Type")[0].getValue();
        String canonicalizedHeaders = Stream.of(request.getAllHeaders())
                .filter(h -> h.getName().toLowerCase().startsWith("x-obs-"))
                //.map(h -> new String[]{h.getName().toLowerCase().trim(), h.getValue().toLowerCase().trim()})
                .collect(Collectors.groupingBy(Header::getName))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(h -> h.getKey().toLowerCase().trim() + ":"
                        + h.getValue().stream()
                        .map(NameValuePair::getValue)
                        .collect(Collectors.joining(","))
                        + "\n")
                .collect(Collectors.joining(""));
        String canonicalQueryString = getCanonicalQuery(request.getURI().getRawQuery());
        String canonicalizedResource = request.getURI().getPath() + canonicalQueryString;

        String signature = doSign(request.getURI().toString(), date, method, content, contentType, canonicalizedHeaders, canonicalizedResource);
        if (request.containsHeader(AUTH_BY_URL_FLAG)) {
            request.setURI(new URI(
                    signByUrl(request.getURI().toString(),
                            date, method, content, contentType, canonicalizedHeaders, canonicalizedResource)));
        } else {
            request.addHeader("Date", date);
            request.addHeader("Authorization", "OBS " + ak + ":" + signature);
        }
    }

    private String getCanonicalQuery(String query) {
        if (query != null && !query.isEmpty()) {
            return "?" + Stream.of(query.split("&"))
                    .map(q -> q.split("=", 2))
                    .sorted(Comparator.comparing(i -> i[0]))
                    .map(i -> i[0] + "=" + i[1])
                    .collect(Collectors.joining("&"));
        }
        return "";
    }

    /**
     * Sign by url string.
     *
     * @param url       the url
     * @param expireSec the expire sec
     * @return the string
     * @throws MalformedURLException the malformed url exception
     * @link https ://support.huaweicloud.com/api-obs/zh-cn_topic_0100846724.html
     */
    public Map<String, String> signByPostRequest(String url, long expireSec)
            throws MalformedURLException {
        var expTime = new Date();
        expTime.setTime((System.currentTimeMillis() + expireSec * 1000));
        var sdf = new SimpleDateFormat(ISO8601UTC);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        var expiration = sdf.format(expTime);
        var urlObj = new URL(url);
        var bucket = urlObj.getHost().substring(0, urlObj.getHost().indexOf("."));
        var key = new URL(url).getPath().substring(1);
        var policy =
                "{\n" +
                        "  \"expiration\": \"" + expiration + "\",\n" +
                        "  \"conditions\": [\n" +
                        "    {\"bucket\": \"" + bucket + "\"}, \n" +
                        "    { \"key\": \"" + key + "\"}\n" +
                        "  ]\n" +
                        "}";
        var base64Policy = $.security.encodeStringToBase64(policy, StandardCharsets.UTF_8);
        var sign = $.security.digest.digest(base64Policy.getBytes(StandardCharsets.UTF_8), sk.getBytes(StandardCharsets.UTF_8), "HmacSHA1");
        var signature = $.security.encodeBytesToBase64(sign);
        return new HashMap<>() {
            {
                put("url", urlObj.getProtocol() + "://" + urlObj.getHost());
                put("AccessKeyId", ak);
                put("policy", base64Policy);
                put("signature", signature);
                put("key", key);
            }
        };
    }

    /**
     * Sign by url string.
     *
     * @param method      the method
     * @param contentType the content type
     * @param url         the url
     * @param expireSec   the expire sec
     * @return the string
     * @throws MalformedURLException the malformed url exception
     * @link https ://support.huaweicloud.com/api-obs/zh-cn_topic_0100846724.html
     */
    public String signByUrl(String method, String contentType, String url, long expireSec)
            throws MalformedURLException {
        String expire = (System.currentTimeMillis() / 1000 + expireSec) + "";
        return signByUrl(url, expire,
                method, "", contentType, "",
                new URL(url).getPath() + getCanonicalQuery(new URL(url).getQuery()));
    }

    private String signByUrl(String url,
                             String date, String method, String content, String contentType,
                             String canonicalizedHeaders, String resource)
            throws MalformedURLException {
        // 资源需要带上桶名称
        String authorization = "AccessKeyId=" + ak + "&Expires=" + date + "&Signature="
                + URLEncoder.encode(doSign(url, date, method, content, contentType, canonicalizedHeaders, resource), StandardCharsets.UTF_8);
        if (url.contains("?")) {
            return url + "&" + authorization;
        } else {
            return url + "?" + authorization;
        }
    }

    private String doSign(String url,
                          String date, String method, String content, String contentType,
                          String canonicalizedHeaders, String resource)
            throws MalformedURLException {
        String canonicalizedResource =
                ("/" + new URL(url).getHost().split("\\.")[0] + resource).chars()
                        .mapToObj(i -> (char) i)
                        .map(c -> {
                            if ($.field.isChinese(c + "")) {
                                return URLEncoder.encode(c + "", StandardCharsets.UTF_8);
                            } else {
                                return c + "";
                            }
                        })
                        .collect(Collectors.joining());
        String stringToSign = method + "\n"
                + content + "\n"
                + contentType + "\n"
                + date + "\n"
                + canonicalizedHeaders
                + canonicalizedResource;
        byte[] sign = $.security.digest.digest(stringToSign.getBytes(StandardCharsets.UTF_8), sk.getBytes(StandardCharsets.UTF_8), "HmacSHA1");
        return $.security.encodeBytesToBase64(sign);
    }

}
