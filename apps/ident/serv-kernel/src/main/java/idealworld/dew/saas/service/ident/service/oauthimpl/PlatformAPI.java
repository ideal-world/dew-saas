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

package idealworld.dew.saas.service.ident.service.oauthimpl;

import com.ecfront.dew.common.Resp;
import com.ecfront.dew.common.tuple.Tuple2;
import group.idealworld.dew.Dew;
import idealworld.dew.saas.common.resp.StandardResp;
import idealworld.dew.saas.service.ident.service.OAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Platform api.
 *
 * @author gudaoxuri
 */
@Service
@Slf4j
public abstract class PlatformAPI {

    /**
     * The constant ACCESS_TOKEN_FLAG.
     */
    protected static final String ACCESS_TOKEN_FLAG = "oauth:access-token:";
    /**
     * The constant BUSINESS_OAUTH.
     */
    protected static final String BUSINESS_OAUTH = "OAUTH";

    /**
     * Gets platform flag.
     *
     * @return the platform flag
     */
    abstract String getPlatformFlag();

    /**
     * Gets user info.
     *
     * @param code the code
     * @param ak   the ak
     * @param sk   the sk
     * @return the user info
     */
    abstract Resp<OAuthService.OAuthUserInfo> getUserInfo(String code, String ak, String sk);

    /**
     * Do get access token.
     *
     * @param ak the ak
     * @param sk the sk
     * @return the resp
     */
    abstract Resp<Tuple2<String, Long>> doGetAccessToken(String ak, String sk);

    /**
     * Gets access token.
     *
     * @param ak the ak
     * @param sk the sk
     * @return the access token
     */
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
