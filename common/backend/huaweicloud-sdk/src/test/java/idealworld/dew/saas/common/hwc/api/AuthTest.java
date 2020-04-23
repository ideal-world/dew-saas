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

import com.ecfront.dew.common.$;
import com.ecfront.dew.common.exception.RTIOException;
import org.apache.http.client.methods.HttpRequestBase;
import org.junit.Assert;
import org.junit.Test;

import java.util.Comparator;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The type Auth test.
 *
 * @author gudaoxuri
 */
public class AuthTest {

    /**
     * Test sign.
     */
    @Test(expected = RTIOException.class)
    public void testSign() {
        $.http.setPreRequest((Consumer<HttpRequestBase>) request -> {
            try {
                String method = request.getMethod();
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
                String requestPayload = "";
                String hexedRequestPayload = $.security.digest.digest(requestPayload, "SHA-256");

                String hashedCanonicalRequest = $.security.digest.digest(
                        method + "\n"
                                + canonicalURI + "\n"
                                + canonicalQueryString + "\n"
                                + canonicalHeaders + "\n"
                                + signedHeaders + "\n"
                                + hexedRequestPayload, "SHA-256").toLowerCase();
                String stringToSign = "SDK-HMAC-SHA256" + "\n20190329T074551Z\n" + hashedCanonicalRequest;
                String signature = $.security.digest.digest(stringToSign, "MFyfvK41ba2giqM7Uio6PznpdUKGpownRZlmVmHc", "HmacSHA256");
                String authorization = "SDK-HMAC-SHA256 Access=QTWAOYTTINDUT2QVKYUC, SignedHeaders=" + signedHeaders + ", Signature=" + signature;
                Assert.assertEquals("SDK-HMAC-SHA256 Access=QTWAOYTTINDUT2QVKYUC, "
                                + "SignedHeaders=content-type;host;x-sdk-date, "
                                + "Signature=d66f6a6c536e984129e13a4060f465225909fd126d212cb25e9e292346aae036",
                        authorization);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        $.http.get("https://service.region.example.com/v1/77b6a44cba5143ab91d13ab9a8ff44fd/vpcs?limit=2&marker=13551d6b-755d-4757-b956-536f674975c0",
                new HashMap<>() {
                    {
                        put("host", "service.region.example.com");
                        put("X-Sdk-Date", "20190329T074551Z");
                        put("Content-Type", "application/json");
                    }
                });
    }
}
