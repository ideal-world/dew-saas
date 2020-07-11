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

package idealworld.dew.saas.common.sdk;

import com.ecfront.dew.common.$;
import com.ecfront.dew.common.HttpHelper;
import com.ecfront.dew.common.Resp;
import com.ecfront.dew.common.exception.RTException;
import idealworld.dew.saas.common.utils.ResponseProcessor;
import lombok.extern.slf4j.Slf4j;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Common sdk.
 *
 * @param <E> the type parameter
 * @author gudaoxuri
 */
@Slf4j
public abstract class CommonSDK<E extends CommonConfig> extends ResponseProcessor {

    private static final AtomicBoolean INITIALIZED = new AtomicBoolean(false);
    /**
     * The Base url.
     */
    protected String baseUrl;
    /**
     * The Config.
     */
    protected E config;
    private HttpHelper httpHelper;
    private String serviceUrl;

    /**
     * Sets service url.
     *
     * @param serviceUrl the service url
     */
    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    /**
     * Gets config.
     *
     * @return the config
     */
    public E getConfig() {
        return config;
    }

    /**
     * Sets config.
     *
     * @param config the config
     */
    public void setConfig(E config) {
        this.config = config;
    }

    /**
     * Is initialized.
     *
     * @return the result
     */
    public boolean isInitialized() {
        return INITIALIZED.get();
    }

    /**
     * Init.
     */
    public synchronized void init() {
        if (INITIALIZED.get()) {
            return;
        }
        if (serviceUrl == null
                || serviceUrl.isBlank()) {
            throw new RTException("参数错误：缺少服务地址");
        }
        if (config.getBasic().getAppAk() == null
                || config.getBasic().getAppAk().isBlank()) {
            throw new RTException("参数错误：缺少[basic.appAk]");
        }
        if (config.getBasic().getAppSk() == null
                || config.getBasic().getAppSk().isBlank()) {
            throw new RTException("参数错误：缺少[basic.appSk]");
        }
        httpHelper = $.http(config.getPerf().getDefaultCTimeoutMS(), false);
        baseUrl = serviceUrl;
        INITIALIZED.set(true);
    }

    @Override
    public Resp<?> exchange(String method, String uri, Object body, Map<String, String> header) {
        if (header == null) {
            header = new HashMap<>();
        }
        if (!uri.startsWith("/")) {
            uri = "/" + uri;
        }
        var sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        var date = sdf.format(new Date());
        header.put("Dew-Date", date);

        URL url = null;
        try {
            url = new URL(formatUrl(uri));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        // 使用Base64 + HmacSHA1 签名
        var digestText = (method + "\n" + date + "\n" + url.getPath() + "\n" + (url.getQuery() != null ? url.getQuery() : "")).toLowerCase();
        var secretKey = config.getBasic().getAppSk();
        var signature = $.security.encodeStringToBase64(
                $.security.digest.digest(digestText, secretKey, "HmacSHA1"),
                StandardCharsets.UTF_8
        );

        header.put(config.getBasic().getAuthFieldName(),
                config.getBasic().getAppAk() + ":" + signature);

        uri = formatUrl(uri);
        log.trace("Request: [" + method + "] " + uri);
        var result = httpHelper.request(method, uri, body, header,
                null, null,
                config.getPerf().getDefaultCTimeoutMS()).result;
        return $.json.toObject(result, Resp.class);
    }

    private String formatUrl(String uri) {
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }
        String url;
        if (uri.startsWith("/")) {
            url = baseUrl + uri;
        } else {
            url = baseUrl + "/" + uri;
        }
        return url;
    }

}
