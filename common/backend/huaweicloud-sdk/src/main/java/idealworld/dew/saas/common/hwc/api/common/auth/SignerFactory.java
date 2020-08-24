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

package idealworld.dew.saas.common.hwc.api.common.auth;

import idealworld.dew.saas.common.hwc.api.common.BasicProcessor;
import idealworld.dew.saas.common.hwc.api.obs.OBS;
import idealworld.dew.saas.common.hwc.api.vod.VOD;

/**
 * The type Signer factory.
 *
 * @author gudaoxuri
 */
public class SignerFactory {

    /**
     * Instance signer.
     *
     * @param ak          the ak
     * @param sk          the sk
     * @param projectId   the project id
     * @param accountName the account name
     * @param processor   the processor
     * @return the signer
     */
    public static Signer instance(String ak, String sk, String projectId, String accountName, BasicProcessor<?> processor) {
        if (processor instanceof OBS) {
            return new OBSSigner(ak, sk);
        } else if (processor instanceof VOD) {
            return new TokenSigner(ak, sk, projectId, accountName);
        } else {
            return new GatewaySigner(ak, sk);
        }
    }

}
