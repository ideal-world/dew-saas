package idealworld.dew.saas.common.utils;

import com.ecfront.dew.common.Page;
import com.ecfront.dew.common.Resp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author gudaoxuri
 */
public abstract class ResponseProcessor {

    public Resp<Long> post(String url, Object body) {
        var response = exchange("POST", url, body, new HashMap<>());
        return Resp.generic(response, Long.class);
    }

    public <E> Resp<E> postToEntity(String url, Object body, Class<E> responseClazz) {
        var response = exchange("POST", url, body, new HashMap<>());
        return Resp.generic(response, responseClazz);
    }

    public <E> Resp<List<E>> postToList(String url, Object body, Class<E> responseClazz) {
        var response = exchange("POST", url, body, new HashMap<>());
        return Resp.genericList(response, responseClazz);
    }

    public <E> Resp<Page<E>> postToPage(String url, Object body, Class<E> responseClazz) {
        var response = exchange("POST", url, body, new HashMap<>());
        return Resp.genericPage(response, responseClazz);
    }

    public Resp<Void> patch(String url, Object body) {
        var response = exchange("PATCH", url, body, new HashMap<>());
        return Resp.generic(response, Void.class);
    }

    public <E> Resp<E> patchToEntity(String url, Object body, Class<E> responseClazz) {
        var response = exchange("PATCH", url, body, new HashMap<>());
        return Resp.generic(response, responseClazz);
    }

    public <E> Resp<List<E>> patchToList(String url, Object body, Class<E> responseClazz) {
        var response = exchange("PATCH", url, body, new HashMap<>());
        return Resp.genericList(response, responseClazz);
    }

    public <E> Resp<Page<E>> patchToPage(String url, Object body, Class<E> responseClazz) {
        var response = exchange("PATCH", url, body, new HashMap<>());
        return Resp.genericPage(response, responseClazz);
    }

    public <E> Resp<E> getToEntity(String url, Class<E> responseClazz) {
        var response = exchange("GET", url, null, new HashMap<>());
        return Resp.generic(response, responseClazz);
    }

    public <E> Resp<E> getToEntity(String url, Map<String, String> header, Class<E> responseClazz) {
        var response = exchange("GET", url, null, header);
        return Resp.generic(response, responseClazz);
    }

    public <E> Resp<List<E>> getToList(String url, Class<E> responseClazz) {
        var response = exchange("GET", url, null, new HashMap<>());
        return Resp.genericList(response, responseClazz);
    }

    public <E> Resp<Page<E>> getToPage(String url, Long pageNumber, Integer pageSize, Class<E> responseClazz) {
        if (url.contains("?")) {
            url += "&pageNumber=" + pageNumber + "&pageSize=" + pageSize;
        } else {
            url += "?pageNumber=" + pageNumber + "&pageSize=" + pageSize;
        }
        var response = exchange("GET", url, null, new HashMap<>());
        return Resp.genericPage(response, responseClazz);
    }

    public Resp<Void> delete(String url) {
        var response = exchange("DELETE", url, null, new HashMap<>());
        return Resp.generic(response, Void.class);
    }

    public abstract Resp<?> exchange(String method, String url, Object body, Map<String, String> header);

}
