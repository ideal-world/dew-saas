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

package idealworld.dew.saas.service.ident;

import com.ecfront.dew.common.$;
import group.idealworld.dew.Dew;

import java.util.ArrayList;

/**
 * Misc test.
 *
 * @author gudaoxuri
 */
public class MiscTest {

    /**
     * Batch do somethings.
     */
    public void batchDoSomethings() {
        // 第1小时跑批一次
        Dew.Timer.periodic(60 * 60, () -> {
            // 领导者选举，确保只能在一个节点上运行
            if (Dew.cluster.election.instance("batchTask").isLeader()) {
                try {
                    // 锁定某个资源避免竞争(等待10s)
                    Dew.cluster.lock.instance("users").tryLockWithFun(10, () -> {
                        // Do Some things
                        var result = new ArrayList<String>();
                        // 结果集写入缓存
                        Dew.cluster.cache.lmset("cache:users", result);
                        // 发送MQ通知
                        Dew.cluster.mq.publish("batch:user:finished", $.json.toJsonString(result));
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
