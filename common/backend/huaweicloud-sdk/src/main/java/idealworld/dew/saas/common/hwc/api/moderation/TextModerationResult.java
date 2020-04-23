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
import java.util.Map;
import java.util.Set;

/**
 * The type Text moderation result.
 *
 * @author gudaoxuri
 */
public class TextModerationResult {

    private Suggestion suggestion;

    private Map<TextCategory, Set<String>> hitDetail = new HashMap<>();

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
    public TextModerationResult setSuggestion(Suggestion suggestion) {
        this.suggestion = suggestion;
        return this;
    }

    /**
     * Gets hit detail.
     *
     * @return the hit detail
     */
    public Map<TextCategory, Set<String>> getHitDetail() {
        return hitDetail;
    }

    /**
     * Sets hit detail.
     *
     * @param hitDetail the hit detail
     * @return the hit detail
     */
    public TextModerationResult setHitDetail(Map<TextCategory, Set<String>> hitDetail) {
        this.hitDetail = hitDetail;
        return this;
    }
}
