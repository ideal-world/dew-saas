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

package idealworld.dew.saas.common.hwc.api.common;

import com.ecfront.dew.common.$;
import com.ecfront.dew.common.HttpHelper;
import idealworld.dew.saas.common.hwc.api.common.auth.Signer;
import idealworld.dew.saas.common.hwc.api.common.auth.SignerFactory;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * The type Basic processor.
 *
 * @param <T> the type parameter
 * @author gudaoxuri
 */
@Slf4j
public abstract class BasicProcessor<T> {


    /**
     * The Http.
     */
    protected HttpHelper http;
    /**
     * The Signer.
     */
    protected Signer signer;

    /**
     * The URL.
     */
    protected String url;

    /**
     * The P id.
     */
    protected String pId;

    /**
     * Instantiates a new Auth.
     */
    public BasicProcessor() {
        http = $.http(-1, true);
        http.setPreRequest(request -> {
            try {
                signer.sign(request);
                return request;
            } catch (URISyntaxException | IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Auth.
     *
     * @param ak the ak
     * @param sk the sk
     * @return the t
     */
    public T auth(String ak, String sk) {
        signer = SignerFactory.instance(ak, sk, this);
        return (T) this;
    }

    /**
     * Host.
     *
     * @param h the h
     * @return the t
     */
    public T host(String h) {
        this.url = "https://" + h;
        return (T) this;
    }

    /**
     * Project id.
     *
     * @param pId the p id
     * @return the t
     */
    public T projectId(String pId) {
        this.pId = pId;
        return (T) this;
    }

    /**
     * Resp filter.
     *
     * @param <E>      the type parameter
     * @param response the response
     * @return the e
     */
    protected abstract <E> E respFilter(String response);

}
