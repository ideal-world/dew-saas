package idealworld.dew.saas.common.sdk;

import com.ecfront.dew.common.$;
import com.ecfront.dew.common.HttpHelper;
import com.ecfront.dew.common.HttpHelperFactory;
import com.ecfront.dew.common.Resp;
import com.ecfront.dew.common.exception.RTException;
import idealworld.dew.saas.common.utils.ResponseProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class CommonSDK<E extends CommonConfig> extends ResponseProcessor {

    protected static final Logger logger = LoggerFactory.getLogger(CommonSDK.class);

    private static final AtomicBoolean INITIALIZED = new AtomicBoolean(false);

    private HttpHelper httpHelper;
    protected String baseUrl;
    protected E config;
    private String serviceUrl;

    public void setConfig(E config) {
        this.config = config;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public E getConfig() {
        return config;
    }

    public boolean isInitialized() {
        return INITIALIZED.get();
    }

    public synchronized void init() {
        if (INITIALIZED.get()) {
            return;
        }
        if (serviceUrl == null
                || serviceUrl.isBlank()) {
            throw new RTException("参数错误：缺少服务地址");
        }
        if (config.getBasic().getTenantId() == null
                || config.getBasic().getTenantId() < 1) {
            throw new RTException("参数错误：缺少[basic.tenantId]");
        }
        if (config.getBasic().getAppAk() == null
                || config.getBasic().getAppAk().isBlank()) {
            throw new RTException("参数错误：缺少[basic.appAk]");
        }
        if (config.getBasic().getAppSk() == null
                || config.getBasic().getAppSk().isBlank()) {
            throw new RTException("参数错误：缺少[basic.appSk]");
        }
        httpHelper = $.http(config.getPerf().getMaxTotal(),
                config.getPerf().getMaxPerRoute(),
                config.getPerf().getDefaultConnectTimeoutMS(),
                config.getPerf().getDefaultSocketTimeoutMS(),
                true, false, HttpHelperFactory.BACKEND.APACHE);
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
                $.security.digest.digest(digestText,secretKey,"HmacSHA1"),
                StandardCharsets.UTF_8
        );

        header.put(config.getBasic().getAuthFieldName(),
                config.getBasic().getAppAk() + ":" + signature);

        uri = formatUrl(uri);
        logger.trace("Request: [" + method + "] " + uri);
        var result = httpHelper.request(method, uri, body, header,
                null, null,
                config.getPerf().getDefaultConnectTimeoutMS(),
                config.getPerf().getDefaultSocketTimeoutMS()).result;
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
