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

package idealworld.dew.saas.common.hwc.api.common.util;

import com.ecfront.dew.common.$;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Similar cos.
 *
 * @author gudaoxuri
 */
public class Similar {

    private static final Logger logger = LoggerFactory.getLogger(Similar.class);

    /**
     * Similar double.
     *
     * @param collection1 the collection 1
     * @param collection2 the collection 2
     * @return the double
     * @link 参考来源 ：http://www.voidcn.com/article/p-nbmwwhpd-bch.html
     */
    public static double similarCos(Map<String, Double> collection1, Map<String, Double> collection2) {
        double weight = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;

        Set<String> c1Keys = collection1.keySet();
        Set<String> c2Keys = collection2.keySet();

        Set<String> intersection = new HashSet<>(c1Keys);
        intersection.retainAll(c2Keys);

        for (String item : intersection) {
            weight += collection1.get(item) * collection2.get(item);
        }

        for (String item : collection1.keySet()) {
            norm1 += Math.pow(collection1.get(item), 2);
        }
        for (String item : collection2.keySet()) {
            norm2 += Math.pow(collection2.get(item), 2);
        }
        return weight / Math.sqrt(norm1 * norm2);
    }

    /**
     * Merge list.
     *
     * @param similarCollection Map key = ids, Map value = weight
     * @param threshold         the threshold (0-1)
     * @return the merged list
     */
    public static List<Set<String>> merge(List<Map<String, Double>> similarCollection, float threshold) {
        List<Set<String>> mergedSimilarCollection = new ArrayList<>();
        for (int i = 0; i < similarCollection.size() - 1; i++) {
            for (int j = i + 1; j < similarCollection.size(); j++) {
                double similarity = Similar.similarCos(similarCollection.get(i), similarCollection.get(j));
                if (similarity != 0) {
                    logger.debug("Similarity:" + similarity
                            + "\nC1:"
                            + $.json.toJsonString(similarCollection.get(i))
                            + "\nC2:"
                            + $.json.toJsonString(similarCollection.get(j)));
                }
                if (similarity > threshold) {
                    // 找到相似集合
                    int finalI = i;
                    // 合并相似集合
                    similarCollection.get(j).forEach((key, value) -> {
                        if (similarCollection.get(finalI).containsKey(key)) {
                            similarCollection.get(finalI).put(key, (value + similarCollection.get(finalI).get(key)) / 2);
                        } else {
                            similarCollection.get(finalI).put(key, value);
                        }
                    });
                    similarCollection.get(j).clear();
                    // 重新计算
                    return merge(similarCollection.stream().filter(v -> !v.isEmpty()).collect(Collectors.toList()), threshold);
                }
            }
            mergedSimilarCollection.add(similarCollection.get(i).keySet());
        }
        mergedSimilarCollection.add(similarCollection.get(similarCollection.size() - 1).keySet());
        return mergedSimilarCollection;
    }
}
