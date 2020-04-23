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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The type Image moderation result.
 *
 * @author gudaoxuri
 */
public class ImageModerationResult {

    private Suggestion suggestion;
    private String url;

    private Map<ImageCategory, CategoryDetail> hitDetail = new HashMap<>();

    /**
     * The type Detail.
     */
    public static class CategoryDetail {

        private Suggestion suggestion;
        private List<Detail> details;

        /**
         * The type Detail.
         */
        public static class Detail {

            private double confidence;
            private String label;

            /**
             * Gets confidence.
             *
             * @return the confidence
             */
            public double getConfidence() {
                return confidence;
            }

            /**
             * Sets confidence.
             *
             * @param confidence the confidence
             * @return the confidence
             */
            public Detail setConfidence(double confidence) {
                this.confidence = confidence;
                return this;
            }

            /**
             * Gets label.
             *
             * @return the label
             */
            public String getLabel() {
                return label;
            }

            /**
             * Sets label.
             *
             * @param label the label
             * @return the label
             */
            public Detail setLabel(String label) {
                this.label = label;
                return this;
            }
        }

        /**
         * Gets suggestion.
         *
         * @return the suggestion
         */
        public Suggestion getSuggestion() {
            return suggestion;
        }

        /**
         * Sets suggestion.
         *
         * @param suggestion the suggestion
         * @return the suggestion
         */
        public CategoryDetail setSuggestion(Suggestion suggestion) {
            this.suggestion = suggestion;
            return this;
        }

        /**
         * Gets details.
         *
         * @return the details
         */
        public List<Detail> getDetails() {
            return details;
        }

        /**
         * Sets details.
         *
         * @param details the details
         * @return the details
         */
        public CategoryDetail setDetails(List<Detail> details) {
            this.details = details;
            return this;
        }
    }

    /**
     * Gets suggestion.
     *
     * @return the suggestion
     */
    public Suggestion getSuggestion() {
        return suggestion;
    }

    /**
     * Sets suggestion.
     *
     * @param suggestion the suggestion
     * @return the suggestion
     */
    public ImageModerationResult setSuggestion(Suggestion suggestion) {
        this.suggestion = suggestion;
        return this;
    }

    /**
     * Gets hit detail.
     *
     * @return the hit detail
     */
    public Map<ImageCategory, CategoryDetail> getHitDetail() {
        return hitDetail;
    }

    /**
     * Sets hit detail.
     *
     * @param hitDetail the hit detail
     * @return the hit detail
     */
    public ImageModerationResult setHitDetail(Map<ImageCategory, CategoryDetail> hitDetail) {
        this.hitDetail = hitDetail;
        return this;
    }

    /**
     * Gets url.
     *
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets url.
     *
     * @param url the url
     * @return the url
     */
    public ImageModerationResult setUrl(String url) {
        this.url = url;
        return this;
    }
}
