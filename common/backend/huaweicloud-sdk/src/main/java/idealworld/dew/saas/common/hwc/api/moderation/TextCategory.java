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

package idealworld.dew.saas.common.hwc.api.moderation;

import java.util.Arrays;
import java.util.Optional;

/**
 * The enum Text category.
 */
public enum TextCategory {
    /**
     * Politics category.
     */
    POLITICS("politics", "涉政"),
    /**
     * Porn category.
     */
    PORN("porn", "涉黄"),
    /**
     * Ad category.
     */
    AD("ad", "广告"),
    /**
     * Abuse category.
     */
    ABUSE("abuse", "辱骂"),
    /**
     * Contraband category.
     */
    CONTRABAND("contraband", "违禁品"),
    /**
     * Flood category.
     */
    FLOOD("flood", "灌水");

    /**
     * From key optional.
     *
     * @param key the key
     * @return the optional
     */
    public static Optional<TextCategory> fromKey(String key) {
        return Arrays.stream(values())
                .filter(e -> e.key.equalsIgnoreCase(key))
                .findFirst();
    }

    private String key;
    private String value;

    TextCategory(String key, String value) {
        this.key = key;
        this.value = value;
    }

    /**
     * Gets key.
     *
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * Sets key.
     *
     * @param key the key
     * @return the key
     */
    public TextCategory setKey(String key) {
        this.key = key;
        return this;
    }

    /**
     * Gets value.
     *
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets value.
     *
     * @param value the value
     * @return the value
     */
    public TextCategory setValue(String value) {
        this.value = value;
        return this;
    }
}
