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
import com.ecfront.dew.common.HttpHelper;
import idealworld.dew.saas.common.hwc.api.common.OptException;
import lombok.SneakyThrows;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * The type Token signer.
 *
 * @author gudaoxuri
 * @link https ://support.huaweicloud.com/api-iam/iam_30_0001.html
 */
public class TokenSigner implements Signer {

    private static final String ISO8601UTC = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    private String ak;
    private String sk;
    private String projectId;
    private String accountName;

    private String token;
    private Long expireMS;

    /**
     * Instantiates a new Token signer.
     *
     * @param ak          the ak
     * @param sk          the sk
     * @param projectId   the project id
     * @param accountName the account name
     */
    TokenSigner(String ak, String sk, String projectId, String accountName) {
        this.ak = ak;
        this.sk = sk;
        this.projectId = projectId;
        this.accountName = accountName;
        fetchToken();
    }

    @SneakyThrows
    private void fetchToken() {
        // https://support.huaweicloud.com/api-iam/iam_30_0001.html
        HttpHelper.ResponseWrap tokenFetchResult = $.http.postWrap("https://iam.myhuaweicloud.com/v3/auth/tokens?nocatalog=true",
                "{\n" +
                        "    \"auth\": {\n" +
                        "        \"identity\": {\n" +
                        "            \"methods\": [\n" +
                        "                \"password\"\n" +
                        "            ],\n" +
                        "            \"password\": {\n" +
                        "                \"user\": {\n" +
                        "                    \"domain\": {\n" +
                        "                        \"name\": \"" + accountName + "\"\n" +
                        "                    },\n" +
                        "                    \"name\": \"" + ak + "\",\n" +
                        "                    \"password\": \"" + sk + "\"\n" +
                        "                }\n" +
                        "            }\n" +
                        "        },\n" +
                        "        \"scope\": {\n" +
                        "            \"project\": {\n" +
                        "                \"id\": \"" + projectId + "\"\n" +
                        "            }\n" +
                        "        }\n" +
                        "    }\n" +
                        "}");
        if (tokenFetchResult.statusCode != 201) {
            throw new OptException(tokenFetchResult.statusCode + "", "Token fetch error", "");
        }
        token = tokenFetchResult.head.get("X-Subject-Token").get(0);
        String expireMSStr = $.json.toJson(tokenFetchResult.result).get("token").get("expires_at").asText();
        var sdf = new SimpleDateFormat(ISO8601UTC);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        expireMS = sdf.parse(expireMSStr).getTime();
    }

    @Override
    public void sign(HttpHelper.PreRequestContext request)
            throws URISyntaxException, MalformedURLException {
        if (System.currentTimeMillis() > expireMS) {
            fetchToken();
        }
        request.getHeader().put("X-Auth-Token", token);
    }

}
