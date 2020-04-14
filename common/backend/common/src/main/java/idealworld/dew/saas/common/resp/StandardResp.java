package idealworld.dew.saas.common.resp;

import com.ecfront.dew.common.$;
import com.ecfront.dew.common.Resp;
import com.ecfront.dew.common.StandardCode;
import com.ecfront.dew.common.exception.RTException;
import lombok.extern.slf4j.Slf4j;

/**
 * @author gudaoxuri
 */
@Slf4j
public class StandardResp {

    private static String serviceFlag = "";

    public static void setServiceFlag(String _serviceFlag) {
        if (serviceFlag.contains("-")) {
            throw new RTException("service flag can't contain '-'");
        }
        if (serviceFlag.length() > 10) {
            throw new RTException("service flag length can't be greater than 10");
        }
        serviceFlag = _serviceFlag;
    }

    public static <E> Resp<E> success(E body) {
        return Resp.success(body);
    }

    public static <E> Resp<E> error(Resp<?> resp) {
        return new Resp<>(resp.getCode(), resp.getMessage(), null);
    }

    public static <E> Resp<E> custom(String code, String businessFlag, String content, Object... args) {
        return packageResp(code, businessFlag, String.format(content, args));
    }

    public static <E> Resp<E> notFoundResource(String businessFlag, String resource) {
        return packageResp(StandardCode.NOT_FOUND.toString(), businessFlag, "找不到[" + resource + "],请检查权限");
    }

    public static <E> Resp<E> notFound(String businessFlag, String content, Object... args) {
        return packageResp(StandardCode.NOT_FOUND.toString(), businessFlag, String.format(content, args));
    }

    public static <E> Resp<E> badRequest(String businessFlag, String content, Object... args) {
        return packageResp(StandardCode.BAD_REQUEST.toString(), businessFlag, String.format(content, args));
    }

    public static <E> Resp<E> unAuthorizedOperate(String businessFlag, String operate) {
        return packageResp(StandardCode.UNAUTHORIZED.toString(), businessFlag, "操作[" + operate + "]没有权限");
    }

    public static <E> Resp<E> unAuthorizedResource(String businessFlag, String resource) {
        return packageResp(StandardCode.UNAUTHORIZED.toString(), businessFlag, "资源[" + resource + "]没有权限");
    }

    public static <E> Resp<E> unAuthorized(String businessFlag, String content, Object... args) {
        return packageResp(StandardCode.UNAUTHORIZED.toString(), businessFlag, String.format(content, args));
    }

    public static <E> Resp<E> conflict(String businessFlag, String content, Object... args) {
        return packageResp(StandardCode.CONFLICT.toString(), businessFlag, String.format(content, args));
    }

    public static <E> Resp<E> lockedResource(String businessFlag, String resource) {
        return packageResp(StandardCode.LOCKED.toString(), businessFlag, "资源[" + resource + "]被锁定");
    }

    public static <E> Resp<E> locked(String businessFlag, String content, Object... args) {
        return packageResp(StandardCode.LOCKED.toString(), businessFlag, String.format(content, args));
    }

    public static <E> Resp<E> unsupportedMediaType(String businessFlag, String request) {
        return packageResp(StandardCode.UNSUPPORTED_MEDIA_TYPE.toString(), businessFlag, "请求[" + request + "]类型不支持");
    }

    public static <E> Resp<E> unsupportedMediaType(String businessFlag, String content, Object... args) {
        return packageResp(StandardCode.UNSUPPORTED_MEDIA_TYPE.toString(), businessFlag, String.format(content, args));
    }

    public static <E> Resp<E> serverError(String businessFlag, Throwable e) {
        return packageResp(StandardCode.INTERNAL_SERVER_ERROR.toString(), businessFlag, "服务错误:" + e.getMessage());
    }

    public static <E> Resp<E> serverError(String businessFlag, String content, Object... args) {
        return packageResp(StandardCode.INTERNAL_SERVER_ERROR.toString(), businessFlag, String.format(content, args));
    }

    public static <E> Resp<E> notImplementedMethod(String businessFlag, String method) {
        return packageResp(StandardCode.NOT_IMPLEMENTED.toString(), businessFlag, "方法[" + method + "]未实现");
    }

    public static <E> Resp<E> notImplemented(String businessFlag, String content, Object... args) {
        return packageResp(StandardCode.NOT_IMPLEMENTED.toString(), businessFlag, String.format(content, args));
    }

    public static <E> Resp<E> serverUnavailable(String businessFlag) {
        return packageResp(StandardCode.SERVICE_UNAVAILABLE.toString(), businessFlag, "服务不可用，请稍后再试");
    }

    public static <E> Resp<E> serverUnavailable(String businessFlag, String content, Object... args) {
        return packageResp(StandardCode.SERVICE_UNAVAILABLE.toString(), businessFlag, String.format(content, args));
    }

    private static <E> Resp<E> packageResp(String statusCode, String businessFlag, String content) {
        var code = statusCode + "-" + serviceFlag + "-" + businessFlag + ":" + $.field.createShortUUID();
        log.trace("RESP [{}] {}", code, content);
        return Resp.custom(code, content);
    }

}
