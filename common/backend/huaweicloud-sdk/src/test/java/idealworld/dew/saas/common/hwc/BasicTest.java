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

package idealworld.dew.saas.common.hwc;

import com.ecfront.dew.common.$;

/**
 * The type Basic test.
 *
 * @author gudaoxuri
 */
public abstract class BasicTest {

    static {
        var config = $.file.readAllByClassPath("config.secret", "UTF-8");
        var oauthJson = $.json.toJson(config);
        AK = oauthJson.get("ak").asText();
        SK = oauthJson.get("sk").asText();
        PROJECT_ID = oauthJson.get("pid").asText();
        OBS_HOST = oauthJson.get("obs_host").asText();
        FACE_HOST = oauthJson.get("face_host").asText();
        MODERATION_HOST = oauthJson.get("moderation_host").asText();
    }

    /**
     * The constant AK.
     */
    protected static String AK;
    /**
     * The constant SK.
     */
    protected static String SK;

    /**
     * The constant PROJECT_ID.
     */
    protected static String PROJECT_ID;
    /**
     * The constant OBS_HOST.
     */
    protected static String OBS_HOST;
    /**
     * The constant FACE_HOST.
     */
    protected static String FACE_HOST;

    /**
     * The constant MODERATION_HOST.
     */
    protected static String MODERATION_HOST;

}
