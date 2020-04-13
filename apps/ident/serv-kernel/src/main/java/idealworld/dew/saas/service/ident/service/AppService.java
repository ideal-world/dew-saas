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
import idealworld.dew.saas.common.Constant;
import idealworld.dew.saas.service.ident.domain.*;
import idealworld.dew.saas.service.ident.dto.app.*;
import idealworld.dew.saas.service.ident.enumeration.CommonStatus;
import idealworld.dew.saas.service.ident.utils.KeyHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author gudaoxuri
 */
@Service
@Slf4j
public class AppService extends IdentBasicService {

    @Autowired
    private OrganizationService organizationService;
    @Autowired
    private PositionService positionService;
    @Autowired
    private InterceptService interceptService;

    @Transactional
    public Resp<Long> addApp(AddAppReq addAppReq, Long relTenantId) {
        var qApp = QApp.app;
        if (sqlBuilder.select(qApp.id)
                .from(qApp)
                .where(qApp.relTenantId.eq(relTenantId))
                .where(qApp.name.eq(addAppReq.getName()))
                .fetchCount() != 0) {
            return Resp.conflict("此应用名已存在");
        }
        log.info("Add App : [{}] {}", relTenantId, $.json.toJsonString(addAppReq));
        var app = App.builder()
                .name(addAppReq.getName())
                .icon(addAppReq.getIcon() != null ? addAppReq.getIcon() : "")
                .parameters(addAppReq.getParameters() != null ? addAppReq.getParameters() : "{}")
                .status(CommonStatus.ENABLED)
                .relTenantId(relTenantId)
                .build();
        var saveR = saveEntity(app);
        interceptService.changeAppStatus(app.getId(), CommonStatus.ENABLED);
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
                        qApp.parameters,
                        qApp.status,
                        qApp.relTenantId,
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
        if (modifyAppReq.getParameters() != null) {
            updateClause.set(qApp.parameters, modifyAppReq.getParameters());
        }
        if (modifyAppReq.getStatus() != null) {
            updateClause.set(qApp.status, modifyAppReq.getStatus());
            interceptService.changeAppStatus(appId, modifyAppReq.getStatus());
        }
        return updateEntity(updateClause);
    }

    @Transactional
    public Resp<Void> deleteApp(Long appId, Long relTenantId) {
        // 删除机构
        organizationService.deleteOrganization(appId, relTenantId);
        // 删除职位
        positionService.deletePositions(appId, relTenantId);
        // 删除应用凭证
        deleteAppCerts(appId, relTenantId);
        var qApp = QApp.app;
        interceptService.changeAppStatus(appId, CommonStatus.DISABLED);
        return softDelEntity(sqlBuilder
                .selectFrom(qApp)
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
        log.info("Add App Cert : [{}] {} : {}", relTenantId, relAppId, $.json.toJsonString(addAppCertReq));
        var ak = KeyHelper.generateAK();
        var sk = KeyHelper.generateSK(ak);
        var appCert = AppCert.builder()
                .note(addAppCertReq.getNote())
                .ak(ak)
                .sk(sk)
                .validTime(addAppCertReq.getValidTime() != null ? addAppCertReq.getValidTime() : Constant.NEVER_EXPIRE_TIME)
                .status(CommonStatus.ENABLED)
                .relAppId(relAppId)
                .build();
        var saveR = saveEntity(appCert);
        if (saveR.ok()) {
            interceptService.changeAppCert(appCert, relAppId, relTenantId);
        }
        return saveR;
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
                        qAppCert.status,
                        qAppCert.validTime,
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
        if (modifyAppCertReq.getStatus() != null) {
            updateClause.set(qAppCert.status, modifyAppCertReq.getStatus());
        }
        var updateR = updateEntity(updateClause);
        if (updateR.ok() && (
                modifyAppCertReq.getValidTime() != null || modifyAppCertReq.getStatus() != null
        )) {
            var updateAppCert = sqlBuilder.selectFrom(qAppCert)
                    .where(qAppCert.id.eq(appCertId))
                    .fetchOne();
            interceptService.changeAppCert(updateAppCert, relAppId, relTenantId);
        }
        return updateR;
    }

    @Transactional
    public Resp<Long> deleteAppCerts(Long relAppId, Long relTenantId) {
        if (!checkAppMembership(relAppId, relTenantId)) {
            return Constant.RESP.NOT_FOUNT();
        }
        var qAppCert = QAppCert.appCert;
        sqlBuilder.select(qAppCert.ak)
                .from(qAppCert)
                .where(qAppCert.relAppId.eq(relAppId))
                .fetch()
                .forEach(ak -> interceptService.deleteAppCert(ak));
        return softDelEntities(sqlBuilder
                .selectFrom(qAppCert)
                .where(qAppCert.relAppId.eq(relAppId)));
    }

    @Transactional
    public Resp<Void> deleteAppCert(Long appCertId, Long relAppId, Long relTenantId) {
        if (!checkAppMembership(relAppId, relTenantId)) {
            return Constant.RESP.NOT_FOUNT();
        }
        var qAppCert = QAppCert.appCert;
        var ak = sqlBuilder.select(qAppCert.ak)
                .from(qAppCert)
                .where(qAppCert.id.eq(appCertId))
                .where(qAppCert.relAppId.eq(relAppId))
                .fetchOne();
        interceptService.deleteAppCert(ak);
        return deleteEntity(sqlBuilder
                .delete(qAppCert)
                .where(qAppCert.id.eq(appCertId))
                .where(qAppCert.relAppId.eq(relAppId)));
    }

    protected Boolean checkAppMembership(Long appId, Long relTenantId) {
        var qApp = QApp.app;
        var num = sqlBuilder
                .selectFrom(qApp)
                .where(qApp.id.eq(appId))
                .where(qApp.relTenantId.eq(relTenantId))
                .fetchCount();
        return num != 0;
    }

}
