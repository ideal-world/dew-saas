package idealworld.dew.saas.service.ident.service.oauth;

import com.ecfront.dew.common.Resp;
import com.ecfront.dew.common.tuple.Tuple2;
import group.idealworld.dew.Dew;
import idealworld.dew.saas.common.resp.StandardResp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author gudaoxuri
 */

@Service
@Slf4j
public abstract class PlatformAPI {

    protected static final String ACCESS_TOKEN_FLAG = "oauth:access-token:";
    protected static final String BUSINESS_OAUTH="OAUTH";

    abstract String getPlatformFlag();

    abstract Resp<OAuthService.OAuthUserInfo> getUserInfo(String code, String ak, String sk);

    abstract protected Resp<Tuple2<String, Long>> doGetAccessToken(String ak, String sk);

    public Resp<String> getAccessToken(String ak, String sk) {
        var accessToken = Dew.cluster.cache.get(ACCESS_TOKEN_FLAG + getPlatformFlag());
        if (accessToken != null) {
            return StandardResp.success(accessToken);
        }
        var getR = doGetAccessToken(ak, sk);
        if (!getR.ok()) {
            return StandardResp.error(getR);
        }
        Dew.cluster.cache.setex(ACCESS_TOKEN_FLAG, getR.getBody()._0, getR.getBody()._1 - 10);
        return StandardResp.success(getR.getBody()._0);
    }

}
