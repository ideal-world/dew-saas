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

/**
 * The type Face Search result.
 *
 * @author gudaoxuri
 */
public class FaceSearchResult {

    private String faceId;
    private String extId;
    private double similarity;

    /**
     * Gets face id.
     *
     * @return the face id
     */
    public String getFaceId() {
        return faceId;
    }

    /**
     * Sets face id.
     *
     * @param faceId the face id
     * @return the face id
     */
    public FaceSearchResult setFaceId(String faceId) {
        this.faceId = faceId;
        return this;
    }

    /**
     * Gets ext id.
     *
     * @return the ext id
     */
    public String getExtId() {
        return extId;
    }

    /**
     * Sets ext id.
     *
     * @param extId the ext id
     * @return the ext id
     */
    public FaceSearchResult setExtId(String extId) {
        this.extId = extId;
        return this;
    }

    /**
     * Gets similarity.
     *
     * @return the similarity
     */
    public double getSimilarity() {
        return similarity;
    }

    /**
     * Sets similarity.
     *
     * @param similarity the similarity
     * @return the similarity
     */
    public FaceSearchResult setSimilarity(double similarity) {
        this.similarity = similarity;
        return this;
    }
}
