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

package idealworld.dew.saas.service.ident.service.sdk;

import com.ecfront.dew.common.StandardCode;
import com.ecfront.dew.common.exception.RTException;
import group.idealworld.dew.core.auth.AuthAdapter;
import group.idealworld.dew.core.auth.dto.OptInfo;
import idealworld.dew.saas.common.service.dto.IdentOptInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Optional;


/**
 * Ident鉴权适配器.
 *
 * @author gudaoxuri
 * @author gjason
 */
@Component
public class IdentAuthAdapter implements AuthAdapter {

    private static final Logger logger = LoggerFactory.getLogger(IdentAuthAdapter.class);

    @Autowired
    protected IdentSDK identSDK;

    @Override
    public <E extends OptInfo<E>> Optional<E> getOptInfo(String token) {
        var resp = identSDK.auth.getOptInfo(token, IdentOptInfo.class);
        if (!resp.ok()) {
            logger.warn("获取登录信息错误[" + resp.getCode() + "] " + resp.getMessage());
            if(resp.getCode().equalsIgnoreCase(StandardCode.UNAUTHORIZED.toString())){
                return Optional.empty();
            }
            throw new RTException("获取登录信息错误[" + resp.getCode() + "] " + resp.getMessage());
        }
        return Optional.of((E) resp.getBody());
    }

    @Override
    public void removeOptInfo(String token) {
        throw new UnsupportedOperationException("不支持删除登录信息");
    }

    @Override
    public <E extends OptInfo<E>> void setOptInfo(E optInfo) {
        throw new UnsupportedOperationException("不支持设置登录信息");
    }

}
