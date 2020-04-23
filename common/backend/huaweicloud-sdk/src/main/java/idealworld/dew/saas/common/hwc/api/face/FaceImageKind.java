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

package idealworld.dew.saas.common.hwc.api.face;

import java.util.Arrays;
import java.util.Optional;

/**
 * The enum Face image kind.
 *
 * @author gudaoxuri
 */
public enum FaceImageKind {

    /**
     * Image url face image kind.
     */
    IMAGE_URL("image_url"),

    /**
     * Face id face image kind.
     */
    FACE_ID("face_id");

    /**
     * From key optional.
     *
     * @param key the key
     * @return the optional
     */
    public static Optional<FaceImageKind> fromKey(String key) {
        return Arrays.stream(values())
                .filter(e -> e.key.equalsIgnoreCase(key))
                .findFirst();
    }

    private String key;

    FaceImageKind(String key) {
        this.key = key;
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
    public FaceImageKind setKey(String key) {
        this.key = key;
        return this;
    }

}
