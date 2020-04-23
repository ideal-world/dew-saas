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

import java.util.Date;

/**
 * The type Face set result.
 *
 * @author gudaoxuri
 */
public class FaceSetResult {

    private String faceSetId;
    private String faceSetName;
    private Long faceNumber;
    private Date createTime;
    private Long faceCapacity;

    /**
     * Gets face set id.
     *
     * @return the face set id
     */
    public String getFaceSetId() {
        return faceSetId;
    }

    /**
     * Sets face set id.
     *
     * @param faceSetId the face set id
     * @return the face set id
     */
    public FaceSetResult setFaceSetId(String faceSetId) {
        this.faceSetId = faceSetId;
        return this;
    }

    /**
     * Gets face set name.
     *
     * @return the face set name
     */
    public String getFaceSetName() {
        return faceSetName;
    }

    /**
     * Sets face set name.
     *
     * @param faceSetName the face set name
     * @return the face set name
     */
    public FaceSetResult setFaceSetName(String faceSetName) {
        this.faceSetName = faceSetName;
        return this;
    }

    /**
     * Gets face number.
     *
     * @return the face number
     */
    public Long getFaceNumber() {
        return faceNumber;
    }

    /**
     * Sets face number.
     *
     * @param faceNumber the face number
     * @return the face number
     */
    public FaceSetResult setFaceNumber(Long faceNumber) {
        this.faceNumber = faceNumber;
        return this;
    }

    /**
     * Gets create time.
     *
     * @return the create time
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * Sets create time.
     *
     * @param createTime the create time
     * @return the create time
     */
    public FaceSetResult setCreateTime(Date createTime) {
        this.createTime = createTime;
        return this;
    }

    /**
     * Gets face capacity.
     *
     * @return the face capacity
     */
    public Long getFaceCapacity() {
        return faceCapacity;
    }

    /**
     * Sets face capacity.
     *
     * @param faceCapacity the face capacity
     * @return the face capacity
     */
    public FaceSetResult setFaceCapacity(Long faceCapacity) {
        this.faceCapacity = faceCapacity;
        return this;
    }
}
