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

package idealworld.dew.saas.common.wechat.service;

import com.ecfront.dew.common.$;
import com.ecfront.dew.common.Resp;
import com.querydsl.jpa.impl.JPAQueryFactory;
import group.idealworld.dew.Dew;
import group.idealworld.dew.core.cluster.ClusterElection;
import group.idealworld.dew.core.cluster.VoidProcessFun;
import idealworld.dew.saas.common.CommonConfig;
import idealworld.dew.saas.common.resp.StandardResp;
import idealworld.dew.saas.common.wechat.domain.QSubscribeInfo;
import idealworld.dew.saas.common.wechat.domain.SubscribeInfo;
import idealworld.dew.saas.common.wechat.dto.SubscribeReq;
import idealworld.dew.saas.service.ident.enumeration.AccountIdentKind;
import idealworld.dew.saas.service.ident.service.sdk.IdentSDK;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.HashMap;
import java.util.Map;


/**
 * Subscribe service.
 *
 * @author gudaoxuri
 */
@Service
@Slf4j
public class WechatCommonSubscribeService {

    public static final int DISABLED_OR_NOTFOUND_COUNTER = -1;

    private static final ClusterElection ELECTION = Dew.cluster.election.instance("dew.saas.wechat.sub");

    private static final String BUSINESS_SUB = "SUB";

    @Autowired
    private CommonConfig commonConfig;
    @Autowired
    private JPAQueryFactory sqlBuilder;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private IdentSDK identSDK;

    /**
     * Counter.
     *
     * @param templateId    the template id
     * @param currentOpenId the current open id
     * @return the resp
     */
    public Resp<Integer> counter(String templateId, String currentOpenId) {
        if (!commonConfig.getWechat().getSubscribe()) {
            return StandardResp.success(DISABLED_OR_NOTFOUND_COUNTER);
        }
        var qSubscribeInfo = QSubscribeInfo.subscribeInfo;
        var counter = sqlBuilder.select(qSubscribeInfo.balanceCounter)
                .from(qSubscribeInfo)
                .where(qSubscribeInfo.relAccountId.eq(currentOpenId))
                .where(qSubscribeInfo.templateId.eq(templateId))
                .fetchOne();
        if (counter == null) {
            return StandardResp.success(DISABLED_OR_NOTFOUND_COUNTER);
        }
        return StandardResp.success(counter);
    }

    /**
     * Request subscribe message.
     *
     * @param subscribeReq  the subscribe req
     * @param currentOpenId the current open id
     * @return the resp
     */
    @SneakyThrows
    @Transactional
    public Resp<Integer> requestSubscribeMessage(SubscribeReq subscribeReq, String currentOpenId) {
        log.info("Request Sub Msg : {} By {}", $.json.toJsonString(subscribeReq), currentOpenId);
        var qSubscribeInfo = QSubscribeInfo.subscribeInfo;
        var counterR = counter(subscribeReq.getTemplateId(), currentOpenId);
        if (!counterR.ok()) {
            return StandardResp.error(counterR);
        }
        if (counterR.getBody() == -1) {
            entityManager.persist(SubscribeInfo.builder()
                    .templateId(subscribeReq.getTemplateId())
                    .relAccountId(currentOpenId)
                    .totalCounter(1)
                    .balanceCounter(1)
                    .build());
            return StandardResp.success(1);
        } else {
            sqlBuilder.update(qSubscribeInfo)
                    .set(qSubscribeInfo.totalCounter, qSubscribeInfo.totalCounter.add(1))
                    .set(qSubscribeInfo.balanceCounter, qSubscribeInfo.balanceCounter.add(1))
                    .where(qSubscribeInfo.relAccountId.eq(currentOpenId))
                    .where(qSubscribeInfo.templateId.eq(subscribeReq.getTemplateId()))
                    .execute();
            return counter(subscribeReq.getTemplateId(), currentOpenId);
        }
    }

    /**
     * Send messages.
     *
     * @param templateId   the template id
     * @param redirectPage the redirect page
     * @param openId       the open id
     * @param content      the content
     * @param successFun   the success fun
     * @param failureFun   the failure fun
     */
    @SneakyThrows
    @Transactional
    public void sendMessages(String templateId, String redirectPage,
                             String openId, Map<String, String> content,
                             VoidProcessFun successFun, VoidProcessFun failureFun) {
        if (!ELECTION.isLeader()) {
            return;
        }
        log.info("Send Message template : {} to {}", templateId, openId);
        if (redirectPage.contains("?")) {
            redirectPage += "&";
        } else {
            redirectPage += "?";
        }
        String finalRedirectPage = redirectPage;
        var qSubscribeInfo = QSubscribeInfo.subscribeInfo;
        var wechatContent = new HashMap<String, Map<String, String>>();
        content.entrySet().forEach(entry -> wechatContent.put(entry.getKey(), new HashMap<>() {
            {
                put("value", entry.getValue());
            }
        }));
        var resp = sendMessage(templateId, openId, finalRedirectPage + "instId=" + openId, wechatContent);
        if (resp.ok()) {
            // 剩余次数减1
            var updateCount = sqlBuilder.update(qSubscribeInfo)
                    .set(qSubscribeInfo.balanceCounter, qSubscribeInfo.balanceCounter.add(-1))
                    .where(qSubscribeInfo.relAccountId.eq(openId))
                    .where(qSubscribeInfo.templateId.eq(templateId))
                    .execute();
            if (updateCount == 0) {
                // 置为0次
                sqlBuilder.update(qSubscribeInfo)
                        .set(qSubscribeInfo.balanceCounter, 0)
                        .where(qSubscribeInfo.relAccountId.eq(openId))
                        .where(qSubscribeInfo.templateId.eq(templateId))
                        .execute();
            }
            successFun.exec();
        } else if (resp.getCode().startsWith("43101")) {
            // 发送错误，用户没有授权或授权次数不足，剩余次数改成0
            sqlBuilder.update(qSubscribeInfo)
                    .set(qSubscribeInfo.balanceCounter, 0)
                    .where(qSubscribeInfo.relAccountId.eq(openId))
                    .where(qSubscribeInfo.templateId.eq(templateId))
                    .execute();
            failureFun.exec();
        }
    }

    private Resp<Void> sendMessage(String templateId, String currentOpenId, String redirectPage, Map<String, Map<String, String>> content) {
        var accessToken = identSDK.auth.oauthGetAccessToken(AccountIdentKind.WECHAT_MP).getBody();
        var openId = identSDK.auth.oauthGetOpenId(currentOpenId, AccountIdentKind.WECHAT_MP).getBody();
        log.trace("Send Message data : {} by {}", $.json.toJsonString(content), currentOpenId);
        com.ecfront.dew.common.HttpHelper.ResponseWrap response = $.http.postWrap("https://api.weixin.qq.com/cgi-bin/message/subscribe/send?access_token=" + accessToken, new HashMap<>() {
            {
                put("touser", openId);
                put("template_id", templateId);
                put("page", redirectPage);
                put("data", content);
            }
        });
        if (response.statusCode != 200) {
            log.trace("发送错误 [200] {} @ {}", "微信接口调用异常", currentOpenId);
            return StandardResp.custom(String.valueOf(response.statusCode), BUSINESS_SUB, "微信接口调用异常");
        }
        var sendResp = $.json.toJson(response.result);
        // 0成功，-1系统繁忙，43101 用户没有授权或授权已用完
        if (sendResp.has("errcode")
                && !sendResp.get("errcode").asText().equalsIgnoreCase("0")) {
            log.trace("发送错误 [{}] {} @ {}", sendResp.get("errcode").asText(), sendResp.get("errmsg").asText(), currentOpenId);
            return StandardResp.custom(sendResp.get("errcode").asText(), BUSINESS_SUB, sendResp.get("errmsg").asText());
        }
        return StandardResp.success(null);
    }

}
