package idealworld.dew.saas.service.ident;

import com.ecfront.dew.common.$;
import group.idealworld.dew.Dew;

import java.util.ArrayList;

/**
 * @author gudaoxuri
 */
public class MiscTest {

    public void batchDoSomethings() {
        // 第1小时跑批一次
        Dew.Timer.periodic(60*60,() ->{
            // 领导者选举，确保只能在一个节点上运行
            if(Dew.cluster.election.instance("batchTask").isLeader()){
                try {
                    // 锁定某个资源避免竞争(等待10s)
                    Dew.cluster.lock.instance("users").tryLockWithFun(10,() ->{
                        // Do Some things
                        var result=new ArrayList<String>();
                        // 结果集写入缓存
                        Dew.cluster.cache.lmset("cache:users",result);
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
