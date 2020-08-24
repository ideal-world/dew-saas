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

package idealworld.dew.saas.common.hwc.api;

import idealworld.dew.saas.common.hwc.api.face.Face;
import idealworld.dew.saas.common.hwc.api.moderation.Moderation;
import idealworld.dew.saas.common.hwc.api.obs.OBS;
import idealworld.dew.saas.common.hwc.api.vod.VOD;
import idealworld.dew.saas.common.hwc.scene.FaceGroup;

/**
 * 华为服务入口函数.
 * <p>
 * 所有功能都有两种调用方式：
 * 1) 单实例：使用属性调用，e.g. HWC.obs.xxx
 * 2) 多实例：使用方法调用, e.g. OBS obs1 = HWC.obs(); obs1.xx
 *
 * @author gudaoxuri
 */
public class HWC {

    public static OBS obs = new OBS();
    public static Moderation moderation = new Moderation();
    public static Face face = new Face();
    public static VOD vod = new VOD();

    public static OBS obs() {
        return new OBS();
    }

    public static Moderation moderation() {
        return new Moderation();
    }

    public static Face face() {
        return new Face();
    }

    public static VOD vod() {
        return new VOD();
    }

    public static FaceGroup faceGroup(Face face, OBS obs, String faceSetName, FaceGroup.DBProcessor processor) {
        return new FaceGroup(face, obs, faceSetName, processor);
    }

}
