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

import com.ecfront.dew.common.Resp;
import group.idealworld.dew.Dew;
import idealworld.dew.saas.common.Constant;
import idealworld.dew.saas.common.service.dto.IdentOptInfo;
import idealworld.dew.saas.service.ident.domain.QAccountCert;
import idealworld.dew.saas.service.ident.dto.account.*;
import idealworld.dew.saas.service.ident.enumeration.AccountCertKind;
import idealworld.dew.saas.service.ident.service.AccountService;
import idealworld.dew.saas.service.ident.service.BasicService;
import idealworld.dew.saas.service.ident.utils.KeyHelper;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author gudaoxuri
 */
@Service
public class OAuthService extends BasicService {

    @Autowired
    private AccountService accountService;
    @Autowired
    private WechatPlatformAPI wechatService;

    @Transactional
    public Resp<IdentOptInfo> login(OAuthReq oAuthReq, Long tenantId) {
        Resp<OAuthUserInfo> oAuthUserInfoR;
        switch (oAuthReq.getCertKind()) {
            case WECHAT:
                // TODO
                oAuthUserInfoR = wechatService.getUserInfo(oAuthReq.getCode(), "", "");
                break;
            default:
                return Resp.notFound("凭证类型不合法");
        }
        if (!oAuthUserInfoR.ok()) {
            return Resp.error(oAuthUserInfoR);
        }
        var qAccountCert = QAccountCert.accountCert;
        var accountId = sqlBuilder.select(qAccountCert.relAccountId)
                .from(qAccountCert)
                .where(qAccountCert.delFlag.eq(false))
                .where(qAccountCert.kind.eq(oAuthReq.getCertKind()))
                .where(qAccountCert.ak.eq(oAuthUserInfoR.getBody().getUnionid()))
                .fetchOne();
        if (accountId == null) {
            accountId = accountService.addAccountExt(AddAccountReq.builder()
                    .name("")
                    .certReq(AddAccountCertReq.builder()
                            .kind(AccountCertKind.WECHAT)
                            .ak(oAuthUserInfoR.getBody().getUnionid())
                            .sk("").build())
                    .postReq(AddAccountPostReq.builder()
                            .relPostId(Constant.OBJECT_UNDEFINED).build())
                    .build(), tenantId).getBody();
        }
        logger.info("Login Success:  [" + tenantId + "][" + oAuthUserInfoR.getBody().getUnionid() + "]");
        String token = KeyHelper.generateToken();
        var optInfo = new IdentOptInfo()
                // 转成String避免转化成Integer
                .setAccountCode(accountId + "")
                .setToken(token)
                .setRoleInfo(accountService.findRoleInfo(accountId));
        optInfo.setRelTenantId(tenantId);
        Dew.auth.setOptInfo(optInfo);
        accountService.login(LoginReq.builder()
                .certKind(AccountCertKind.WECHAT).build(), tenantId);
        return Resp.success(optInfo);
    }

    public Resp<String> getAccessToken(String oauthKind, Long tenantId) {
        if (oauthKind.equalsIgnoreCase(AccountCertKind.WECHAT.toString())) {
            // TODO
            return wechatService.getAccessToken("", "");
        }
        return Resp.notFound("凭证类型不合法");
    }

    @Data
    @Builder
    public class OAuthUserInfo {

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
