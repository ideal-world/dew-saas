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

package idealworld.dew.saas.common.hwc.scene;

/**
 * The type Face group result.
 *
 * @author gudaoxuri
 */
public class FaceGroupResult {

    private String currentFaceId;
    private String imagePath;
    private String imageUrl;
    private String matchedUserId;
    private int matchedBoxTopX;
    private int matchedBoxTopY;
    private int matchedBoxWidth;
    private int matchedBoxHeight;

    public String getCurrentFaceId() {
        return currentFaceId;
    }

    public FaceGroupResult setCurrentFaceId(String currentFaceId) {
        this.currentFaceId = currentFaceId;
        return this;
    }

    public String getImagePath() {
        return imagePath;
    }

    public FaceGroupResult setImagePath(String imagePath) {
        this.imagePath = imagePath;
        return this;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public FaceGroupResult setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    public String getMatchedUserId() {
        return matchedUserId;
    }

    public FaceGroupResult setMatchedUserId(String matchedUserId) {
        this.matchedUserId = matchedUserId;
        return this;
    }

    public int getMatchedBoxTopX() {
        return matchedBoxTopX;
    }

    public FaceGroupResult setMatchedBoxTopX(int matchedBoxTopX) {
        this.matchedBoxTopX = matchedBoxTopX;
        return this;
    }

    public int getMatchedBoxTopY() {
        return matchedBoxTopY;
    }

    public FaceGroupResult setMatchedBoxTopY(int matchedBoxTopY) {
        this.matchedBoxTopY = matchedBoxTopY;
        return this;
    }

    public int getMatchedBoxWidth() {
        return matchedBoxWidth;
    }

    public FaceGroupResult setMatchedBoxWidth(int matchedBoxWidth) {
        this.matchedBoxWidth = matchedBoxWidth;
        return this;
    }

    public int getMatchedBoxHeight() {
        return matchedBoxHeight;
    }

    public FaceGroupResult setMatchedBoxHeight(int matchedBoxHeight) {
        this.matchedBoxHeight = matchedBoxHeight;
        return this;
    }
}
