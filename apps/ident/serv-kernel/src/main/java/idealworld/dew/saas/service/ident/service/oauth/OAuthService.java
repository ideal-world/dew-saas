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

package idealworld.dew.saas.service.ident.service.oauth;

import com.ecfront.dew.common.$;
import com.ecfront.dew.common.Resp;
import group.idealworld.dew.Dew;
import idealworld.dew.saas.common.service.dto.IdentOptInfo;
import idealworld.dew.saas.service.ident.domain.QAccount;
import idealworld.dew.saas.service.ident.domain.QAccountCert;
import idealworld.dew.saas.service.ident.dto.account.AddAccountCertReq;
import idealworld.dew.saas.service.ident.dto.account.AddAccountReq;
import idealworld.dew.saas.service.ident.dto.account.OAuthLoginReq;
import idealworld.dew.saas.service.ident.enumeration.AccountCertKind;
import idealworld.dew.saas.service.ident.enumeration.AccountStatus;
import idealworld.dew.saas.service.ident.service.AccountService;
import idealworld.dew.saas.service.ident.service.IdentBasicService;
import idealworld.dew.saas.service.ident.service.TenantService;
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
 * @author gudaoxuri
 */
@Service
@Slf4j
public class OAuthService extends IdentBasicService {

    @Autowired
    private TenantService tenantService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private WechatMPAPI wechatService;

    @Transactional
    public Resp<IdentOptInfo> login(OAuthLoginReq oAuthLoginReq, Long tenantId) throws Exception {
        Resp<OAuthUserInfo> oAuthUserInfoR;
        var tenantCertConfigR = tenantService.getTenantCertConfig(oAuthLoginReq.getCertKind(), tenantId);
        if (!tenantCertConfigR.ok()) {
            return Resp.error(tenantCertConfigR);
        }
        var oauthAk = tenantCertConfigR.getBody().getOauthAk();
        var oauthSk = tenantCertConfigR.getBody().getOauthSk();
        switch (oAuthLoginReq.getCertKind()) {
            case WECHAT_MP:
                oAuthUserInfoR = wechatService.getUserInfo(oAuthLoginReq.getCode(), oauthAk, oauthSk);
                break;
            default:
                return Resp.notFound("凭证类型不合法");
        }
        if (!oAuthUserInfoR.ok()) {
            return Resp.error(oAuthUserInfoR);
        }
        var lock = Dew.cluster.lock.instance("date:oauth:account:add:"+oAuthUserInfoR.getBody().getOpenid());
        if(lock.tryLock(5000,5000)){
            // 空方法，新请求等待5s后才能操作
            // 5s后释放锁
        }
        log.info("OAuth Login : [{}] {}",tenantId,$.json.toJsonString(oAuthLoginReq));
        var qAccountCert = QAccountCert.accountCert;
        var accountId = sqlBuilder.select(qAccountCert.relAccountId)
                .from(qAccountCert)
                .where(qAccountCert.kind.eq(oAuthLoginReq.getCertKind()))
                .where(qAccountCert.ak.eq(oAuthUserInfoR.getBody().getOpenid()))
                .fetchOne();
        if (accountId == null) {
            accountId = accountService.addAccountExt(AddAccountReq.builder()
                    .name("")
                    .certReq(AddAccountCertReq.builder()
                            .kind(AccountCertKind.WECHAT_MP)
                            .ak(oAuthUserInfoR.getBody().getOpenid())
                            .sk("").build())
                    .build(), tenantId).getBody();
        } else {
            var qAccount = QAccount.account;
            var exist = sqlBuilder.select(qAccount.id)
                    .from(qAccount)
                    .where(qAccount.id.eq(accountId))
                    .where(qAccount.status.eq(AccountStatus.ENABLED))
                    .fetchOne() != null;
            if (!exist) {
                return Resp.badRequest("用户状态异常");
            }
        }
        log.info("Login Success:  [{}] ak(openId) {}",tenantId, oAuthUserInfoR.getBody().getOpenid());
        var qAccount = QAccount.account;
        var parameters = sqlBuilder.select(qAccount.parameters)
                .from(qAccount)
                .where(qAccount.id.eq(accountId))
                .fetchOne();
        String token = KeyHelper.generateToken();
        var optInfo = new IdentOptInfo()
                // 转成String避免转化成Integer
                .setAccountCode(accountId + "")
                .setToken(token)
                .setRoleInfo(accountService.findRoleInfo(accountId));
        optInfo.setRelTenantId(tenantId);
        if (StringUtils.isEmpty(parameters)) {
            parameters = "{}";
        }
        optInfo.setParameters($.json.toMap(parameters, String.class, Object.class));
        Dew.auth.setOptInfo(optInfo);
        return Resp.success(optInfo);
    }

    public Resp<String> getAccessToken(String oauthKind, Long tenantId) {
        var tenantCertConfigR = tenantService.getTenantCertConfig(AccountCertKind.parse(oauthKind), tenantId);
        if (!tenantCertConfigR.ok()) {
            return Resp.error(tenantCertConfigR);
        }
        var oauthAk = tenantCertConfigR.getBody().getOauthAk();
        var oauthSk = tenantCertConfigR.getBody().getOauthSk();
        if (oauthKind.equalsIgnoreCase(AccountCertKind.WECHAT_MP.toString())) {
            return wechatService.getAccessToken(oauthAk, oauthSk);
        }
        return Resp.notFound("凭证类型不合法");
    }

    @Data
    @Builder
    public static class OAuthUserInfo {

        @Tolerate
        public OAuthUserInfo() {
        }

        /**
         * 用户唯一标识.
         */
        private String openid;

        /**
         * 同一平台下的多个用户共用一个标识.
         */
        private String unionid;

    }

}
