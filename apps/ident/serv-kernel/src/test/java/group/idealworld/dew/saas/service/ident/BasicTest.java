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

package group.idealworld.dew.saas.service.ident;

import com.ecfront.dew.common.Page;
import com.ecfront.dew.common.Resp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BasicTest {

    @Autowired
    protected TestRestTemplate restTemplate;

    protected <E> Resp<E> postToEntity(String url, Object body, Class<E> responseClazz) {
        var response = exchange(HttpMethod.POST, url, body, new HashMap<>());
        return Resp.generic(response, responseClazz);
    }

    protected <E> Resp<List<E>> postToList(String url, Object body, Class<E> responseClazz) {
        var response = exchange(HttpMethod.POST, url, body, new HashMap<>());
        return Resp.genericList(response, responseClazz);
    }

    protected <E> Resp<Page<E>> postToPage(String url, Object body, Class<E> responseClazz) {
        var response = exchange(HttpMethod.POST, url, body, new HashMap<>());
        return Resp.genericPage(response, responseClazz);
    }

    protected <E> Resp<E> putToEntity(String url, Object body, Class<E> responseClazz) {
        var response = exchange(HttpMethod.PUT, url, body, new HashMap<>());
        return Resp.generic(response, responseClazz);
    }

    protected <E> Resp<List<E>> putToList(String url, Object body, Class<E> responseClazz) {
        var response = exchange(HttpMethod.PUT, url, body, new HashMap<>());
        return Resp.genericList(response, responseClazz);
    }

    protected <E> Resp<Page<E>> putToPage(String url, Object body, Class<E> responseClazz) {
        var response = exchange(HttpMethod.PUT, url, body, new HashMap<>());
        return Resp.genericPage(response, responseClazz);
    }

    protected <E> Resp<E> getToEntity(String url, Class<E> responseClazz) {
        var response = exchange(HttpMethod.GET, url, null, new HashMap<>());
        return Resp.generic(response, responseClazz);
    }

    protected <E> Resp<List<E>> getToList(String url, Class<E> responseClazz) {
        var response = exchange(HttpMethod.GET, url, null, new HashMap<>());
        return Resp.genericList(response, responseClazz);
    }

    protected <E> Resp<Page<E>> getToPage(String url, Class<E> responseClazz) {
        var response = exchange(HttpMethod.GET, url, null, new HashMap<>());
        return Resp.genericPage(response, responseClazz);
    }

    protected <E> Resp<E> deleteToEntity(String url, Class<E> responseClazz) {
        var response = exchange(HttpMethod.DELETE, url, null, new HashMap<>());
        return Resp.generic(response, responseClazz);
    }

    protected <E> Resp<List<E>> deleteToList(String url, Class<E> responseClazz) {
        var response = exchange(HttpMethod.DELETE, url, null, new HashMap<>());
        return Resp.genericList(response, responseClazz);
    }

    protected <E> Resp<Page<E>> deleteToPage(String url, Class<E> responseClazz) {
        var response = exchange(HttpMethod.DELETE, url, null, new HashMap<>());
        return Resp.genericPage(response, responseClazz);
    }

    private Resp<?> exchange(HttpMethod method, String url, Object body, Map<String, String> header) {
        var httpHeader = new HttpHeaders();
        for (Map.Entry<String, String> entry : header.entrySet()) {
            httpHeader.add(entry.getKey(), entry.getValue());
        }
        return restTemplate.exchange(url, method, new HttpEntity<>(body, httpHeader), Resp.class).getBody();
    }
}
