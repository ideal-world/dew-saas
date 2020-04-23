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
 * The type Face Add result.
 *
 * @author gudaoxuri
 */
public class FaceAddResult {

    private String faceId;
    private String extId;
    private int boxTopX;
    private int boxTopY;
    private int boxWidth;
    private int boxHeight;

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
    public FaceAddResult setFaceId(String faceId) {
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
    public FaceAddResult setExtId(String extId) {
        this.extId = extId;
        return this;
    }

    /**
     * Gets box top x.
     *
     * @return the box top x
     */
    public int getBoxTopX() {
        return boxTopX;
    }

    /**
     * Sets box top x.
     *
     * @param boxTopX the box top x
     * @return the box top x
     */
    public FaceAddResult setBoxTopX(int boxTopX) {
        this.boxTopX = boxTopX;
        return this;
    }

    /**
     * Gets box top y.
     *
     * @return the box top y
     */
    public int getBoxTopY() {
        return boxTopY;
    }

    /**
     * Sets box top y.
     *
     * @param boxTopY the box top y
     * @return the box top y
     */
    public FaceAddResult setBoxTopY(int boxTopY) {
        this.boxTopY = boxTopY;
        return this;
    }

    /**
     * Gets box width.
     *
     * @return the box width
     */
    public int getBoxWidth() {
        return boxWidth;
    }

    /**
     * Sets box width.
     *
     * @param boxWidth the box width
     * @return the box width
     */
    public FaceAddResult setBoxWidth(int boxWidth) {
        this.boxWidth = boxWidth;
        return this;
    }

    /**
     * Gets box height.
     *
     * @return the box height
     */
    public int getBoxHeight() {
        return boxHeight;
    }

    /**
     * Sets box height.
     *
     * @param boxHeight the box height
     * @return the box height
     */
    public FaceAddResult setBoxHeight(int boxHeight) {
        this.boxHeight = boxHeight;
        return this;
    }
}
