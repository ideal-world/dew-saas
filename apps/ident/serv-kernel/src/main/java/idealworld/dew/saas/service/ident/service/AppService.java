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
import com.querydsl.core.types.Projections;
import idealworld.dew.saas.service.ident.Constant;
import idealworld.dew.saas.service.ident.domain.*;
import idealworld.dew.saas.service.ident.dto.app.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author gudaoxuri
 */
@Service
public class AppService extends BasicService {

    @Transactional
    public Resp<Long> addApp(AddAppReq addAppReq, Long relTenantId) {
        var qApp = QApp.app;
        if (sqlBuilder.select(qApp.id)
                .from(qApp)
                .where(qApp.delFlag.eq(false))
                .where(qApp.relTenantId.eq(relTenantId))
                .where(qApp.name.eq(addAppReq.getName()))
                .fetchCount() != 0) {
            return Resp.conflict("此应用名已存在");
        }
        var app = App.builder()
                .name(addAppReq.getName())
                .icon(addAppReq.getIcon() != null ? addAppReq.getIcon() : "")
                .relTenantId(relTenantId)
                .build();
        var saveR = saveEntity(app);
        addAppCert(AddAppCertReq.builder()
                .note("默认凭证")
                .build(), saveR.getBody(), relTenantId);
        return saveR;
    }

    public Resp<List<AppInfoResp>> findAppInfo(Long relTenantId) {
        var qApp = QApp.app;
        var qAccountCreateUser = QAccount.account;
        var qAccountUpdateUser = QAccount.account;
        var query = sqlBuilder
                .select(Projections.bean(
                        AppInfoResp.class,
                        qApp.id,
                        qApp.name,
                        qApp.icon,
                        qApp.relTenantId,
                        qApp.delFlag,
                        qApp.createTime,
                        qApp.updateTime,
                        qAccountCreateUser.name.as("createUserName"),
                        qAccountUpdateUser.name.as("updateUserName")))
                .from(qApp)
                .leftJoin(qAccountCreateUser).on(qApp.createUser.eq(qAccountCreateUser.id))
                .leftJoin(qAccountUpdateUser).on(qApp.updateUser.eq(qAccountUpdateUser.id))
                .where(qApp.relTenantId.eq(relTenantId));
        return findDTOs(query);
    }

    @Transactional
    public Resp<Void> modifyApp(ModifyAppReq modifyAppReq, Long appId, Long relTenantId) {
        var qApp = QApp.app;
        var updateClause = sqlBuilder.update(qApp)
                .where(qApp.id.eq(appId))
                .where(qApp.relTenantId.eq(relTenantId));
        if (modifyAppReq.getName() != null) {
            updateClause.set(qApp.name, modifyAppReq.getName());
        }
        if (modifyAppReq.getIcon() != null) {
            updateClause.set(qApp.icon, modifyAppReq.getIcon());
        }
        return updateEntity(updateClause);
    }

    @Transactional
    public Resp<Void> deleteApp(Long appId, Long relTenantId) {
        var qApp = QApp.app;
        return updateEntity(sqlBuilder
                .update(qApp)
                .set(qApp.delFlag, true)
                .where(qApp.id.eq(appId))
                .where(qApp.relTenantId.eq(relTenantId))
        );
    }

    // ========================== Cert ==============================

    @Transactional
    public Resp<Long> addAppCert(AddAppCertReq addAppCertReq, Long relAppId, Long relTenantId) {
        if (!checkAppMembership(relAppId, relTenantId)) {
            return Constant.RESP.NOT_FOUNT();
        }
        var appCert = AppCert.builder()
                .note(addAppCertReq.getNote())
                // TODO 生成模型
                .ak($.field.createShortUUID())
                .sk($.field.createUUID())
                .validTime(addAppCertReq.getValidTime() != null ? addAppCertReq.getValidTime() : Constant.NEVER_EXPIRE_TIME)
                .validTimes(addAppCertReq.getValidTimes() != null ? addAppCertReq.getValidTimes() : -1L)
                .relAppId(relAppId)
                .build();
        return saveEntity(appCert);
    }

    public Resp<List<AppCertInfoResp>> findAppCertInfo(Long relAppId, Long relTenantId) {
        if (!checkAppMembership(relAppId, relTenantId)) {
            return Constant.RESP.NOT_FOUNT();
        }
        var qAppCert = QAppCert.appCert;
        var qAccountCreateUser = QAccount.account;
        var qAccountUpdateUser = QAccount.account;
        var query = sqlBuilder
                .select(Projections.bean(
                        AppCertInfoResp.class,
                        qAppCert.id,
                        qAppCert.note,
                        qAppCert.ak,
                        qAppCert.sk,
                        qAppCert.validTime,
                        qAppCert.validTimes,
                        qAppCert.delFlag,
                        qAppCert.createTime,
                        qAppCert.updateTime,
                        qAccountCreateUser.name.as("createUserName"),
                        qAccountUpdateUser.name.as("updateUserName")))
                .from(qAppCert)
                .leftJoin(qAccountCreateUser).on(qAppCert.createUser.eq(qAccountCreateUser.id))
                .leftJoin(qAccountUpdateUser).on(qAppCert.updateUser.eq(qAccountUpdateUser.id))
                .where(qAppCert.relAppId.eq(relAppId));
        return findDTOs(query);
    }

    @Transactional
    public Resp<Void> modifyAppCert(ModifyAppCertReq modifyAppCertReq, Long appCertId,
                                    Long relAppId, Long relTenantId) {
        if (!checkAppMembership(relAppId, relTenantId)) {
            return Constant.RESP.NOT_FOUNT();
        }
        var qAppCert = QAppCert.appCert;
        var updateClause = sqlBuilder.update(qAppCert)
                .where(qAppCert.id.eq(appCertId))
                .where(qAppCert.relAppId.eq(relAppId));
        if (modifyAppCertReq.getNote() != null) {
            updateClause.set(qAppCert.note, modifyAppCertReq.getNote());
        }
        if (modifyAppCertReq.getValidTime() != null) {
            updateClause.set(qAppCert.validTime, modifyAppCertReq.getValidTime());
        }
        if (modifyAppCertReq.getValidTimes() != null) {
            updateClause.set(qAppCert.validTimes, modifyAppCertReq.getValidTimes());
        }
        return updateEntity(updateClause);
    }

    @Transactional
    public Resp<Void> deleteAppCerts(Long relAppId, Long relTenantId) {
        if (!checkAppMembership(relAppId, relTenantId)) {
            return Constant.RESP.NOT_FOUNT();
        }
        var qAppCert = QAppCert.appCert;
        sqlBuilder
                .update(qAppCert)
                .set(qAppCert.delFlag, true)
                .where(qAppCert.relAppId.eq(relAppId))
                .execute();
        return Resp.success(null);
    }

    @Transactional
    public Resp<Void> deleteAppCert(Long appCertId, Long relAppId, Long relTenantId) {
        if (!checkAppMembership(relAppId, relTenantId)) {
            return Constant.RESP.NOT_FOUNT();
        }
        var qAppCert = QAppCert.appCert;
        return updateEntity(sqlBuilder
                .update(qAppCert)
                .set(qAppCert.delFlag, true)
                .where(qAppCert.id.eq(appCertId))
                .where(qAppCert.relAppId.eq(relAppId))
        );
    }

    protected Boolean checkAppMembership(Long appId, Long relTenantId) {
        var qApp = QApp.app;
        var num = sqlBuilder
                .selectFrom(qApp)
                .where(qApp.id.eq(appId))
                .where(qApp.relTenantId.eq(relTenantId))
                .where(qApp.delFlag.eq(false))
                .fetchCount();
        return num != 0;
    }

}
