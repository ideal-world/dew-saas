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

package idealworld.dew.saas.service.ident;

import com.ecfront.dew.common.$;
import com.ecfront.dew.common.Resp;
import group.idealworld.dew.Dew;
import idealworld.dew.saas.common.service.dto.IdentOptInfo;
import idealworld.dew.saas.common.utils.ResponseProcessor;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;

/**
 * Basic test.
 *
 * @author gudaoxuri
 */
public abstract class BasicTest extends ResponseProcessor {

    @Value("${server.port:8080}")
    private int serverPort;

    private IdentOptInfo identOptInfo;

    /**
     * Sets ident opt info.
     *
     * @param identOptInfo the ident opt info
     */
    protected void setIdentOptInfo(IdentOptInfo identOptInfo) {
        this.identOptInfo = identOptInfo;
    }

    @Override
    public Resp<?> exchange(String method, String url, Object body, Map<String, String> header) {
        if (identOptInfo != null) {
            header.put(Dew.dewConfig.getSecurity().getTokenFlag(), identOptInfo.getToken());
        }
        var result = $.http.request(method, formatUrl(url), body, header,
                null, null,
                -1,
                -1).result;
        return $.json.toObject(result, Resp.class);
    }

    private String formatUrl(String url) {
        if (url.toLowerCase().startsWith("http://")) {
            return url;
        }
        if (!url.startsWith("/")) {
            url += "/";
        }
        return "http://127.0.0.1:" + serverPort + url;
    }
}
