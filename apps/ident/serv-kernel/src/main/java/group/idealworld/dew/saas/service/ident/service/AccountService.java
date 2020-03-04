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

import com.ecfront.dew.common.Resp;
import group.idealworld.dew.saas.service.ident.domain.CertAccount;
import group.idealworld.dew.Dew;
import org.springframework.stereotype.Service;

/**
 * @author gudaoxuri
 */
@Service
public class AccountService extends BasicService {

    private static final String SK_KIND_VCODE_TMP_REL = "sk-kind:vocde:tmp-rel:";
    // TODO config
    private static final int SK_KIND_VCODE_EXPRIE_SEC = 60 * 5;

    public void sendSk(CertAccount.Kind certKind, String ak) {
        switch (certKind) {
            case PHONE:
                String tmpSk = (int) ((Math.random() * 9 + 1) * 1000) + "";
                Dew.cluster.cache.setex(SK_KIND_VCODE_TMP_REL + ak, tmpSk, SK_KIND_VCODE_EXPRIE_SEC);
                // TODO
                //sendSkByVCode(ak,sk);
                break;
            default:
                break;
        }
    }

    public Resp<Void> validateSk(CertAccount.Kind certKind, String ak, String sk) {
        switch (certKind) {
            case PHONE:
                String tmpSk = Dew.cluster.cache.get(SK_KIND_VCODE_TMP_REL + ak);
                if (tmpSk == null) {
                    return Resp.badRequest("验证码不存在或已过期");
                }
                if (!tmpSk.equalsIgnoreCase(sk)) {
                    return Resp.badRequest("验证码错误");
                }
                return Resp.success(null);
            case WECHAT:
                // TODO
                return Resp.success(null);
            default:
                return Resp.success(null);
        }
    }

}
