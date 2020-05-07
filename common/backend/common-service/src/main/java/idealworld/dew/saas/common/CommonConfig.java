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

package idealworld.dew.saas.common;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * The type Common config.
 *
 * @author gudaoxuri
 */
@Component
@Data
@ConfigurationProperties(prefix = "dew.saas.common")
public class CommonConfig {

    private HuaWeiCloud hwc = new HuaWeiCloud();
    private Wechat wechat = new Wechat();

    /**
     * The type Hua wei cloud.
     */
    @Data
    public static class HuaWeiCloud {

        private String ak;
        private String sk;
        private String projectId = "";
        private String obsHost = "";
        private String faceHost = "";
        private String moderationHost = "";

    }

    /**
     * The type Hua wei cloud.
     */
    @Data
    public static class Wechat {

        private Boolean subscribe = true;

    }

}
