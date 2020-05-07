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

import idealworld.dew.saas.common.sdk.CommonConfig;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Ident config.
 *
 * @author gudaoxuri
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class IdentConfig extends CommonConfig {

    @Builder.Default
    private Ident ident = new Ident();

    /**
     * Ident.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Ident {

        private String url = "";
        @Builder.Default
        private boolean subscribe = false;
        @Builder.Default
        private Integer aliveHeartbeatPeriodSec = 60;
        @Builder.Default
        private String tokenFlag = "Dew-Token";
        @Builder.Default
        private Integer cacheSecByGetOptInfo = 30;

    }

}
