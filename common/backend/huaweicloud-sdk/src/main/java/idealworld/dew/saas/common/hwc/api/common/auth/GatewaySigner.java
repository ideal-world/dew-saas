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
import idealworld.dew.saas.common.hwc.api.common.util.StringHelper;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpRequestBase;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.TimeZone;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The type Gateway signer.
 *
 * @author gudaoxuri
 * @link https ://support.huaweicloud.com/devg-apisign/api-sign-algorithm.html
 */
public class GatewaySigner implements Signer {

    private static final String YYYY_MMDD_T_HHMMSS_Z = "yyyyMMdd'T'HHmmss'Z'";
    private static final String SDK_HMAC_SHA_256 = "SDK-HMAC-SHA256";

    /**
     * The Ak.
     */
    private String ak;
    /**
     * The Sk.
     */
    private String sk;

    /**
     * Instantiates a new Gateway signer.
     *
     * @param ak the ak
     * @param sk the sk
     */
    GatewaySigner(String ak, String sk) {
        this.ak = ak;
        this.sk = sk;
    }

    @Override
    public void sign(HttpRequestBase request) throws IOException {
        SimpleDateFormat sdf = new SimpleDateFormat(YYYY_MMDD_T_HHMMSS_Z);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String requestTime = sdf.format(new Date());
        request.setHeader("x-sdk-date", requestTime);

        final String method = request.getMethod();

        String canonicalURI = request.getURI().getPath();
        if (!canonicalURI.endsWith("/")) {
            canonicalURI += "/";
        }
        String canonicalQueryString = "";
        if (request.getURI().getRawQuery() != null && !request.getURI().getRawQuery().isEmpty()) {
            canonicalQueryString = Stream.of(request.getURI().getRawQuery().split("&"))
                    .map(q -> q.split("=", 2))
                    .sorted(Comparator.comparing(i -> i[0]))
                    .map(i -> i[0] + "=" + i[1])
                    .collect(Collectors.joining("&"));
        }

        String canonicalHeaders = Stream.of(request.getAllHeaders())
                .sorted(Comparator.comparing(h -> h.getName().toLowerCase().trim()))
                .map(h -> h.getName().toLowerCase().trim() + ":" + h.getValue().trim() + "\n")
                .collect(Collectors.joining(""));

        String signedHeaders = Stream.of(request.getAllHeaders())
                .map(h -> h.getName().toLowerCase().trim())
                .sorted(Comparator.comparing(h -> h))
                .collect(Collectors.joining(";"));

        String hexedRequestPayload;
        if (request instanceof HttpEntityEnclosingRequestBase) {
            byte[] payloadBytes = StringHelper.readFullyAsBytes(((HttpEntityEnclosingRequestBase) request).getEntity().getContent());
            hexedRequestPayload = $.security.byte2HexStr($.security.digest.digest(payloadBytes, "SHA-256"));
        } else {
            hexedRequestPayload = $.security.digest.digest("", "SHA-256");
        }

        String hashedCanonicalRequest = $.security.digest.digest(
                method + "\n"
                        + canonicalURI + "\n"
                        + canonicalQueryString + "\n"
                        + canonicalHeaders + "\n"
                        + signedHeaders + "\n"
                        + hexedRequestPayload, "SHA-256").toLowerCase();

        String stringToSign = SDK_HMAC_SHA_256 + "\n" + requestTime + "\n" + hashedCanonicalRequest;
        String signature = $.security.digest.digest(stringToSign, sk, "HmacSHA256");

        String authorization = SDK_HMAC_SHA_256 + " Access=" + ak + ", SignedHeaders=" + signedHeaders + ", Signature=" + signature;
        request.addHeader("Authorization", authorization);
    }
}
