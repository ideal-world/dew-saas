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

package idealworld.dew.saas.service.ident.service;

import com.ecfront.dew.common.$;
import com.ecfront.dew.common.Resp;
import group.idealworld.dew.Dew;
import idealworld.dew.saas.common.enumeration.CommonStatus;
import idealworld.dew.saas.common.resp.StandardResp;
import idealworld.dew.saas.common.service.dto.IdentOptInfo;
import idealworld.dew.saas.service.ident.domain.QAccount;
import idealworld.dew.saas.service.ident.domain.QAccountIdent;
import idealworld.dew.saas.service.ident.dto.account.AddAccountIdentReq;
import idealworld.dew.saas.service.ident.dto.account.AddAccountReq;
import idealworld.dew.saas.service.ident.dto.account.OAuthLoginReq;
import idealworld.dew.saas.service.ident.enumeration.AccountIdentKind;
import idealworld.dew.saas.service.ident.service.oauthimpl.WechatMPAPI;
import idealworld.dew.saas.service.ident.utils.KeyHelper;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * OAuth service.
 *
 * @author gudaoxuri
 */
@Service
@Slf4j
public class OAuthService extends IdentBasicService {

    private static final String BUSINESS_OAUTH = "OAUTH";

    @Autowired
    private TenantService tenantService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private WechatMPAPI wechatService;

    /**
     * Login.
     *
     * @param oauthLoginReq the o auth login req
     * @param tenantId      the tenant id
     * @return the resp
     * @throws Exception the exception
     */
    @Transactional
    public Resp<IdentOptInfo> login(OAuthLoginReq oauthLoginReq, Long tenantId) throws Exception {
        Resp<OAuthUserInfo> oauthUserInfoR;
        var tenantIdentR = tenantService.getTenantIdent(oauthLoginReq.getIdentKind(), tenantId);
        if (!tenantIdentR.ok()) {
            return StandardResp.error(tenantIdentR);
        }
        var oauthAk = tenantIdentR.getBody().getOauthAk();
        var oauthSk = tenantIdentR.getBody().getOauthSk();
        switch (oauthLoginReq.getIdentKind()) {
            case WECHAT_MP:
                oauthUserInfoR = wechatService.getUserInfo(oauthLoginReq.getCode(), oauthAk, oauthSk);
                break;
            default:
                return StandardResp.notFound(BUSINESS_OAUTH, "认证类型不合法");
        }
        if (!oauthUserInfoR.ok()) {
            return StandardResp.error(oauthUserInfoR);
        }
        var lock = Dew.cluster.lock.instance("date:oauth:account:add:" + oauthUserInfoR.getBody().getOpenid());
        if (lock.tryLock(5000, 5000)) {
            // 空方法，新请求等待5s后才能操作
            // 5s后释放锁
        }
        log.info("OAuth Login : [{}] {}", tenantId, $.json.toJsonString(oauthLoginReq));
        var qAccountIdent = QAccountIdent.accountIdent;
        var accountId = sqlBuilder.select(qAccountIdent.relAccountId)
                .from(qAccountIdent)
                .where(qAccountIdent.kind.eq(oauthLoginReq.getIdentKind()))
                .where(qAccountIdent.ak.eq(oauthUserInfoR.getBody().getOpenid()))
                .fetchOne();
        if (accountId == null) {
            accountId = accountService.addAccountExt(AddAccountReq.builder()
                    .name("")
                    .identReq(AddAccountIdentReq.builder()
                            .kind(AccountIdentKind.WECHAT_MP)
                            .ak(oauthUserInfoR.getBody().getOpenid())
                            .sk("").build())
                    .build(), tenantId).getBody();
        } else {
            var qAccount = QAccount.account;
            var exist = sqlBuilder.select(qAccount.id)
                    .from(qAccount)
                    .where(qAccount.id.eq(accountId))
                    .where(qAccount.status.eq(CommonStatus.ENABLED))
                    .fetchOne() != null;
            if (!exist) {
                return StandardResp.badRequest(BUSINESS_OAUTH, "用户状态异常");
            }
        }
        log.info("Login Success:  [{}] ak(openId) {}", tenantId, oauthUserInfoR.getBody().getOpenid());
        var qAccount = QAccount.account;
        var parameters = sqlBuilder.select(qAccount.parameters)
                .from(qAccount)
                .where(qAccount.id.eq(accountId))
                .fetchOne();
        String token = KeyHelper.generateToken();
        var optInfo = new IdentOptInfo()
                .setAccountCode(accountService.getOpenId(accountId).getBody())
                .setToken(token)
                .setRoleInfo(accountService.findRoleInfo(accountId));
        optInfo.setRelTenantId(tenantId);
        if (StringUtils.isEmpty(parameters)) {
            parameters = "{}";
        }
        optInfo.setParameters($.json.toMap(parameters, String.class, Object.class));
        Dew.auth.setOptInfo(optInfo);
        return StandardResp.success(optInfo);
    }

    /**
     * Gets access token.
     *
     * @param oauthKind the oauth kind
     * @param tenantId  the tenant id
     * @return the access token
     */
    public Resp<String> getAccessToken(String oauthKind, Long tenantId) {
        var tenantIdentR = tenantService.getTenantIdent(AccountIdentKind.parse(oauthKind), tenantId);
        if (!tenantIdentR.ok()) {
            return StandardResp.error(tenantIdentR);
        }
        var oauthAk = tenantIdentR.getBody().getOauthAk();
        var oauthSk = tenantIdentR.getBody().getOauthSk();
        if (oauthKind.equalsIgnoreCase(AccountIdentKind.WECHAT_MP.toString())) {
            return wechatService.getAccessToken(oauthAk, oauthSk);
        }
        return StandardResp.notFound(BUSINESS_OAUTH, "认证类型不合法");
    }

    /**
     * O auth user info.
     */
    @Data
    @Builder
    public static class OAuthUserInfo {

        /**
         * 用户唯一标识.
         */
        private String openid;
        /**
         * 同一平台下的多个用户共用一个标识.
         */
        private String unionid;

        /**
         * Instantiates a new O auth user info.
         */
        @Tolerate
        public OAuthUserInfo() {
        }

    }

}
