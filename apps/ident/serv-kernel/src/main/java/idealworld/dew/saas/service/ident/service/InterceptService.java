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

import com.ecfront.dew.common.Resp;
import com.ecfront.dew.common.tuple.Tuple3;
import group.idealworld.dew.Dew;
import idealworld.dew.saas.common.Constant;
import idealworld.dew.saas.common.resp.StandardResp;
import idealworld.dew.saas.service.ident.domain.AppCert;
import idealworld.dew.saas.service.ident.domain.QApp;
import idealworld.dew.saas.service.ident.domain.QAppCert;
import idealworld.dew.saas.service.ident.domain.QTenant;
import idealworld.dew.saas.service.ident.enumeration.CommonStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author gudaoxuri
 */
@Service
@Slf4j
public class InterceptService extends IdentBasicService {

    private static final String CACHE_AK = "ident:app:ak:";
    private static final String CACHE_TENANT_STATUS = "ident:tenant:enable:";
    private static final String CACHE_APP_STATUS = "ident:app:enable:";

    public void cacheTenantAndAppStatus() {
        if (!ELECTION.isLeader()) {
            return;
        }
        var qTenant = QTenant.tenant;
        sqlBuilder.select(qTenant.id)
                .from(qTenant)
                .where(qTenant.status.eq(CommonStatus.ENABLED))
                .fetch()
                .forEach(id -> Dew.cluster.cache.set(CACHE_TENANT_STATUS + id, ""));
        var qApp = QApp.app;
        sqlBuilder.select(qApp.id)
                .from(qApp)
                .where(qApp.status.eq(CommonStatus.ENABLED))
                .fetch()
                .forEach(id -> Dew.cluster.cache.set(CACHE_APP_STATUS + id, ""));
    }

    public void changeTenantStatus(Long tenantId, CommonStatus status) {
        if (status == CommonStatus.ENABLED) {
            Dew.cluster.cache.set(CACHE_TENANT_STATUS + tenantId, "");
        } else {
            Dew.cluster.cache.del(CACHE_TENANT_STATUS + tenantId);
        }
    }

    public void changeAppStatus(Long appId, CommonStatus status) {
        if (status == CommonStatus.ENABLED) {
            Dew.cluster.cache.set(CACHE_APP_STATUS + appId, "");
        } else {
            Dew.cluster.cache.del(CACHE_APP_STATUS + appId);
        }
    }

    public boolean checkTenantStatus(Long appId) {
        return Dew.cluster.cache.exists(CACHE_TENANT_STATUS + appId);
    }

    public boolean checkAppStatus(Long appId) {
        return Dew.cluster.cache.exists(CACHE_APP_STATUS + appId);
    }

    public void cacheAppCerts() {
        if (!ELECTION.isLeader()) {
            return;
        }
        var qAppCert = QAppCert.appCert;
        var qTenant = QTenant.tenant;
        var qApp = QApp.app;
        sqlBuilder
                .select(qAppCert.ak, qAppCert.sk, qAppCert.relAppId, qAppCert.validTime, qApp.relTenantId)
                .from(qAppCert)
                .leftJoin(qApp).on(qApp.id.eq(qAppCert.relAppId).and(qApp.status.eq(CommonStatus.ENABLED)))
                .leftJoin(qTenant).on(qTenant.id.eq(qApp.relTenantId).and(qTenant.status.eq(CommonStatus.ENABLED)))
                .where(qAppCert.validTime.gt(new Date()))
                .where(qAppCert.status.eq(CommonStatus.ENABLED))
                .fetch()
                .forEach(info -> {
                    var ak = info.get(0, String.class);
                    var sk = info.get(1, String.class);
                    var relAppId = info.get(2, Long.class);
                    var validTime = info.get(3, Date.class);
                    var relTenantId = info.get(4, Long.class);
                    if (validTime.getTime() == Constant.NEVER_EXPIRE_TIME.getTime()) {
                        Dew.cluster.cache.set(CACHE_AK + ak, sk + ":" + relTenantId + ":" + relAppId);
                    } else {
                        Dew.cluster.cache.setex(CACHE_AK + ak, sk + ":" + relTenantId + ":" + relAppId,
                                (validTime.getTime() - System.currentTimeMillis()) / 1000);
                    }
                });
    }

    public void changeAppCert(AppCert appCert, Long relAppId, Long relTenantId) {
        Dew.cluster.cache.del(CACHE_AK + appCert.getAk());
        if (appCert.getStatus() == CommonStatus.ENABLED) {
            if (appCert.getValidTime() == null) {
                Dew.cluster.cache.set(CACHE_AK + appCert.getAk(), appCert.getSk() + ":" + relTenantId + ":" + relAppId);
            } else {
                Dew.cluster.cache.setex(CACHE_AK + appCert.getAk(), appCert.getSk() + ":" + relTenantId + ":" + relAppId,
                        (appCert.getValidTime().getTime() - System.currentTimeMillis()) / 1000);
            }
        }
    }

    public void deleteAppCert(String ak) {
        Dew.cluster.cache.del(CACHE_AK + ak);
    }

    public Resp<Tuple3<String, Long, Long>> getAppCertByAk(String ak) {
        var skAndTenantAndAppId = Dew.cluster.cache.get(CACHE_AK + ak);
        if (skAndTenantAndAppId == null) {
            return StandardResp.notFound("INTERCEPT","通过 ak:%s 找不到对应的应用和租户",ak);
        }
        var skAndAppIdSplit = skAndTenantAndAppId.split(":");
        return StandardResp.success(new Tuple3<>(skAndAppIdSplit[0], Long.valueOf(skAndAppIdSplit[1]), Long.valueOf(skAndAppIdSplit[2])));
    }

}
