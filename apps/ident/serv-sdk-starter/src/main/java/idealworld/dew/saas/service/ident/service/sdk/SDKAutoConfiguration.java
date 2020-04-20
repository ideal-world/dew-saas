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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Sdk auto configuration.
 *
 * @author gudaoxuri
 */
@Configuration
public class SDKAutoConfiguration {

    @Autowired
    private SDKConfig sdkConfig;

    /**
     * Ident sdk ident sdk.
     *
     * @return the ident sdk
     */
    @Bean
    public IdentSDK identSDK() {
        var identSdk = IdentSDK.builder(sdkConfig);
        if (!sdkConfig.isLazyInit()) {
            identSdk.init();
        }
        return identSdk;
    }

}
