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

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The type Image moderation result.
 *
 * @author gudaoxuri
 */
@Data
@Builder
public class ImageModerationResp {

    private Suggestion suggestion;
    private String url;

    private Map<ImageCategory, CategoryDetail> hitDetail = new HashMap<>();

    @Tolerate
    public ImageModerationResp() {
    }

    /**
     * The type Detail.
     */
    @Data
    @Builder
    public static class CategoryDetail {

        private Suggestion suggestion;
        private List<Detail> details;

        @Tolerate
        public CategoryDetail() {
        }

        /**
         * The type Detail.
         */
        @Data
        @Builder
        public static class Detail {

            private double confidence;
            private String label;

            @Tolerate
            public Detail() {
            }

        }

    }

}
