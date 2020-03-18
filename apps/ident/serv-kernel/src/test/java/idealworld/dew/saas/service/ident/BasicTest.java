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

import com.ecfront.dew.common.Resp;
import group.idealworld.dew.Dew;
import idealworld.dew.saas.common.service.dto.IdentOptInfo;
import idealworld.dew.saas.common.utils.ResponseProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import java.util.Map;

public abstract class BasicTest extends ResponseProcessor {

    @Autowired(required = false)
    protected TestRestTemplate restTemplate;

    private IdentOptInfo identOptInfo;

    protected void setIdentOptInfo(IdentOptInfo identOptInfo) {
        this.identOptInfo = identOptInfo;
    }

    @Override
    public Resp<?> exchange(String method, String url, Object body, Map<String, String> header) {
        var httpHeader = new HttpHeaders();
        if (identOptInfo != null) {
            httpHeader.add(Dew.dewConfig.getSecurity().getTokenFlag(), identOptInfo.getToken());
        }
        for (Map.Entry<String, String> entry : header.entrySet()) {
            httpHeader.add(entry.getKey(), entry.getValue());
        }
        return restTemplate.exchange(url, HttpMethod.resolve(method), new HttpEntity<>(body, httpHeader), Resp.class).getBody();
    }
}
