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

import idealworld.dew.saas.common.hwc.BasicTest;
import idealworld.dew.saas.common.hwc.api.moderation.*;
import idealworld.dew.saas.common.hwc.api.obs.OBS;
import org.junit.Assert;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * The type Moderation test.
 *
 * @author gudaoxuri
 */
public class ModerationTest extends BasicTest {

    /**
     * Test moderation.
     */
    /*@Test*/
    public void testModeration() {
        Moderation moderation = HWC.moderation;
        OBS obs = HWC.obs;
        obs.host(OBS_HOST).auth(AK, SK);
        moderation.host(MODERATION_HOST).auth(AK, SK);
        // Text
        TextModerationResult textModerationResult = moderation.text("666666luo聊请+110亚砷酸钾六位qq，fuck666666666666666",
                TextCategory.AD, TextCategory.POLITICS, TextCategory.ABUSE, TextCategory.PORN, TextCategory.CONTRABAND, TextCategory.FLOOD);
        Assert.assertEquals(Suggestion.BLOCK, textModerationResult.getSuggestion());
        Assert.assertEquals("六位qq", textModerationResult.getHitDetail().get(TextCategory.AD).iterator().next());
        // Image
        obs.put("/test/moderation-test-3.jpg", new File(ModerationTest.class.getResource("/").getPath() + "moderation-test/moderation-test-3.jpg"));
        String imageUrl = obs.image("/test/moderation-test-3.jpg", 60 * 5);
        System.out.println(imageUrl);
        ImageModerationResult imageModerationResult = moderation.image(imageUrl,
                ImageCategory.AD, ImageCategory.POLITICS, ImageCategory.TERRORISM, ImageCategory.PORN);
        Assert.assertEquals(Suggestion.BLOCK, imageModerationResult.getSuggestion());
        Assert.assertEquals("习近平(中华人民共和国中央军事委员会主席)",
                imageModerationResult.getHitDetail().get(ImageCategory.POLITICS).getDetails().get(0).getLabel());
        // Image batch
        obs.put("/test/moderation-test-1.jpg", new File(ModerationTest.class.getResource("/").getPath() + "moderation-test/moderation-test-1.jpg"));
        List<ImageModerationResult> imageModerationResults = moderation.image(new ArrayList<String>() {
            {
                add(obs.image("/test/moderation-test-1.jpg", 60 * 5));
                add(obs.image("/test/moderation-test-3.jpg", 60 * 5));
            }
        }, ImageCategory.AD, ImageCategory.POLITICS, ImageCategory.TERRORISM, ImageCategory.PORN);
        Assert.assertEquals(Suggestion.PASS, imageModerationResults.get(0).getSuggestion());
        Assert.assertEquals(Suggestion.BLOCK, imageModerationResults.get(1).getSuggestion());
        Assert.assertEquals("习近平(中华人民共和国中央军事委员会主席)",
                imageModerationResults.get(1).getHitDetail().get(ImageCategory.POLITICS).getDetails().get(0).getLabel());

    }
}
