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

package idealworld.dew.saas.common.hwc.api.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The type Opt exception.
 *
 * @author gudaoxuri
 */
public class OptException extends RuntimeException {

    private static final Logger logger = LoggerFactory.getLogger(OptException.class);

    private String code;
    private String message;

    /**
     * Instantiates a new Opt exception.
     *
     * @param code    the code
     * @param msg     the msg
     * @param extInfo the ext info
     */
    public OptException(String code, String msg, String extInfo) {
        super("[" + code + "]" + msg);
        this.code = code;
        this.message = msg;
        logger.warn("Opt error:[" + code + "]" + msg + " | " + extInfo);
    }

    /**
     * Gets code.
     *
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets code.
     *
     * @param code the code
     * @return the code
     */
    public OptException setCode(String code) {
        this.code = code;
        return this;
    }

    @Override
    public String getMessage() {
        return message;
    }

    /**
     * Sets message.
     *
     * @param message the message
     * @return the message
     */
    public OptException setMessage(String message) {
        this.message = message;
        return this;
    }

}
