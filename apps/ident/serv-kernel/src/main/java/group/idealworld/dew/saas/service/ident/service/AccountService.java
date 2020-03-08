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

package group.idealworld.dew.saas.service.ident.service;

import com.ecfront.dew.common.$;
import com.ecfront.dew.common.Resp;
import group.idealworld.dew.Dew;
import group.idealworld.dew.saas.service.ident.domain.Account;
import group.idealworld.dew.saas.service.ident.domain.AccountCert;
import group.idealworld.dew.saas.service.ident.domain.AccountPost;
import group.idealworld.dew.saas.service.ident.dto.account.AddAccountReq;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.Date;

/**
 * @author gudaoxuri
 */
@Service
public class AccountService extends BasicService {

    private static final String SK_KIND_VCODE_TMP_REL = "sk-kind:vocde:tmp-rel:";
    // TODO config
    private static final int SK_KIND_VCODE_EXPRIE_SEC = 60 * 5;
    private static Date NEVER_EXPIRE_TIME;

    static {
        try {
            NEVER_EXPIRE_TIME = $.time().yyyy_MM_dd.parse("3000-01-01");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Transactional
    public Resp<Long> addAccount(AddAccountReq addAccountReq, Long relTenantId) {
        var processR = certProcessSK(addAccountReq.getCertReq().getKind(),
                addAccountReq.getCertReq().getAk(),
                addAccountReq.getCertReq().getSk());
        if (!processR.ok()) {
            return Resp.error(processR);
        }
        var account = Account.builder()
                .name(addAccountReq.getName())
                .avatar(addAccountReq.getAvatar() != null ? addAccountReq.getAvatar() : "")
                .parameters(addAccountReq.getParameters() != null ? addAccountReq.getParameters() : "")
                .status(Account.Status.ENABLED)
                .relTenantId(relTenantId)
                .build();
        entityManager.persist(account);
        var accountCert = AccountCert.builder()
                .kind(addAccountReq.getCertReq().getKind())
                .ak(addAccountReq.getCertReq().getAk())
                .sk(processR.getBody())
                .validTime(addAccountReq.getCertReq().getValidTime() != null ? addAccountReq.getCertReq().getValidTime() : NEVER_EXPIRE_TIME)
                .validTimes(addAccountReq.getCertReq().getValidTimes() != null ? addAccountReq.getCertReq().getValidTimes() : -1L)
                .relAccountId(account.getId())
                .build();
        entityManager.persist(accountCert);
        var accountPost = AccountPost.builder()
                .relPostId(addAccountReq.getPostReq().getRelPostId())
                .relAccountId(account.getId())
                .build();
        entityManager.persist(accountPost);
        return Resp.success(account.getId());
    }

    public void certSendVC(String ak) {
        String tmpSk = (int) ((Math.random() * 9 + 1) * 1000) + "";
        Dew.cluster.cache.setex(SK_KIND_VCODE_TMP_REL + ak, tmpSk, SK_KIND_VCODE_EXPRIE_SEC);
    }

    private Resp<String> certProcessSK(AccountCert.Kind certKind, String ak, String sk) {
        switch (certKind) {
            case EMAIL:
            case PHONE:
                String tmpSk = Dew.cluster.cache.get(SK_KIND_VCODE_TMP_REL + ak);
                if (tmpSk == null) {
                    return Resp.badRequest("验证码不存在或已过期");
                }
                if (!tmpSk.equalsIgnoreCase(sk)) {
                    return Resp.badRequest("验证码错误");
                }
                return Resp.success("");
            case WECHAT:
                // TODO
                return Resp.success(null);
            case USERNAME:
                return Resp.success($.security.digest.digest(ak + sk, "SHA-512"));
            default:
                return Resp.notFound("凭证类型不合法");
        }
    }

}
