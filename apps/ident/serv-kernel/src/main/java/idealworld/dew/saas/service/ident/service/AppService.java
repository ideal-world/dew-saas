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
import idealworld.dew.saas.common.enumeration.CommonStatus;
import idealworld.dew.saas.common.resp.StandardResp;
import idealworld.dew.saas.service.ident.domain.*;
import idealworld.dew.saas.service.ident.dto.app.*;
import idealworld.dew.saas.service.ident.utils.KeyHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * App service.
 *
 * @author gudaoxuri
 */
@Service
@Slf4j
public class AppService extends IdentBasicService {

    private static final String BUSINESS_APP = "APP";
    private static final String BUSINESS_APP_CERT = "APP_CERT";

    @Autowired
    private OrganizationService organizationService;
    @Autowired
    private PositionService positionService;
    @Autowired
    private InterceptService interceptService;

    /**
     * Add app.
     *
     * @param addAppReq   the add app req
     * @param relTenantId the rel tenant id
     * @return the resp
     */
    @Transactional
    public Resp<Long> addApp(AddAppReq addAppReq, Long relTenantId) {
        var qApp = QApp.app;
        if (sqlBuilder.select(qApp.id)
                .from(qApp)
                .where(qApp.relTenantId.eq(relTenantId))
                .where(qApp.name.eq(addAppReq.getName()))
                .fetchCount() != 0) {
            return StandardResp.conflict(BUSINESS_APP, "此应用名已存在");
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
        addAppIdent(AddAppIdentReq.builder()
                .note("默认认证")
                .build(), saveR.getBody(), relTenantId);
        return saveR;
    }

    /**
     * Find app info.
     *
     * @param relTenantId the rel tenant id
     * @return the resp
     */
    public Resp<List<AppInfoResp>> findAppInfo(Long relTenantId) {
        var qApp = QApp.app;
        var qAccountCreateUser = new QAccount("createUser");
        var qAccountUpdateUser = new QAccount("updateUser");
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
                .leftJoin(qAccountCreateUser).on(qApp.createUser.eq(qAccountCreateUser.openId))
                .leftJoin(qAccountUpdateUser).on(qApp.updateUser.eq(qAccountUpdateUser.openId))
                .where(qApp.relTenantId.eq(relTenantId));
        return findDTOs(query);
    }

    /**
     * Modify app.
     *
     * @param modifyAppReq the modify app req
     * @param appId        the app id
     * @param relTenantId  the rel tenant id
     * @return the resp
     */
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

    /**
     * Delete app.
     *
     * @param appId       the app id
     * @param relTenantId the rel tenant id
     * @return the resp
     */
    @Transactional
    public Resp<Void> deleteApp(Long appId, Long relTenantId) {
        // 删除机构
        organizationService.deleteOrganization(appId, relTenantId);
        // 删除职位
        positionService.deletePositions(appId, relTenantId);
        // 删除应用认证
        deleteAppIdents(appId, relTenantId);
        var qApp = QApp.app;
        interceptService.changeAppStatus(appId, CommonStatus.DISABLED);
        return softDelEntity(sqlBuilder
                .selectFrom(qApp)
                .where(qApp.id.eq(appId))
                .where(qApp.relTenantId.eq(relTenantId))
        );
    }

    // ========================== Ident ==============================

    /**
     * Add app ident.
     *
     * @param addAppIdentReq the add app ident req
     * @param relAppId       the rel app id
     * @param relTenantId    the rel tenant id
     * @return the resp
     */
    @Transactional
    public Resp<Long> addAppIdent(AddAppIdentReq addAppIdentReq, Long relAppId, Long relTenantId) {
        var membershipCheckR = checkAppMembership(relAppId, relTenantId);
        if (!membershipCheckR.ok()) {
            return StandardResp.error(membershipCheckR);
        }
        log.info("Add App Ident : [{}] {} : {}", relTenantId, relAppId, $.json.toJsonString(addAppIdentReq));
        var ak = KeyHelper.generateAK();
        var sk = KeyHelper.generateSK(ak);
        var appIdent = AppIdent.builder()
                .note(addAppIdentReq.getNote())
                .ak(ak)
                .sk(sk)
                .validTime(addAppIdentReq.getValidTime() != null ? addAppIdentReq.getValidTime() : Constant.NEVER_EXPIRE_TIME)
                .status(CommonStatus.ENABLED)
                .relAppId(relAppId)
                .build();
        var saveR = saveEntity(appIdent);
        if (saveR.ok()) {
            interceptService.changeAppIdent(appIdent, relAppId, relTenantId);
        }
        return saveR;
    }

    /**
     * Find app ident info.
     *
     * @param relAppId    the rel app id
     * @param relTenantId the rel tenant id
     * @return the resp
     */
    public Resp<List<AppIdentInfoResp>> findAppIdentInfo(Long relAppId, Long relTenantId) {
        var membershipCheckR = checkAppMembership(relAppId, relTenantId);
        if (!membershipCheckR.ok()) {
            return StandardResp.error(membershipCheckR);
        }
        var qAppIdent = QAppIdent.appIdent;
        var qAccountCreateUser = new QAccount("createUser");
        var qAccountUpdateUser = new QAccount("updateUser");
        var query = sqlBuilder
                .select(Projections.bean(
                        AppIdentInfoResp.class,
                        qAppIdent.id,
                        qAppIdent.note,
                        qAppIdent.ak,
                        qAppIdent.sk,
                        qAppIdent.status,
                        qAppIdent.validTime,
                        qAppIdent.createTime,
                        qAppIdent.updateTime,
                        qAccountCreateUser.name.as("createUserName"),
                        qAccountUpdateUser.name.as("updateUserName")))
                .from(qAppIdent)
                .leftJoin(qAccountCreateUser).on(qAppIdent.createUser.eq(qAccountCreateUser.openId))
                .leftJoin(qAccountUpdateUser).on(qAppIdent.updateUser.eq(qAccountUpdateUser.openId))
                .where(qAppIdent.relAppId.eq(relAppId));
        return findDTOs(query);
    }

    /**
     * Modify app ident.
     *
     * @param modifyAppIdentReq the modify app ident req
     * @param appIdentId        the app ident id
     * @param relAppId          the rel app id
     * @param relTenantId       the rel tenant id
     * @return the resp
     */
    @Transactional
    public Resp<Void> modifyAppIdent(ModifyAppIdentReq modifyAppIdentReq, Long appIdentId,
                                     Long relAppId, Long relTenantId) {
        var membershipCheckR = checkAppMembership(relAppId, relTenantId);
        if (!membershipCheckR.ok()) {
            return StandardResp.error(membershipCheckR);
        }
        var qAppIdent = QAppIdent.appIdent;
        var updateClause = sqlBuilder.update(qAppIdent)
                .where(qAppIdent.id.eq(appIdentId))
                .where(qAppIdent.relAppId.eq(relAppId));
        if (modifyAppIdentReq.getNote() != null) {
            updateClause.set(qAppIdent.note, modifyAppIdentReq.getNote());
        }
        if (modifyAppIdentReq.getValidTime() != null) {
            updateClause.set(qAppIdent.validTime, modifyAppIdentReq.getValidTime());
        }
        if (modifyAppIdentReq.getStatus() != null) {
            updateClause.set(qAppIdent.status, modifyAppIdentReq.getStatus());
        }
        var updateR = updateEntity(updateClause);
        if (updateR.ok() && (
                modifyAppIdentReq.getValidTime() != null || modifyAppIdentReq.getStatus() != null
        )) {
            var updateAppIdent = sqlBuilder.selectFrom(qAppIdent)
                    .where(qAppIdent.id.eq(appIdentId))
                    .fetchOne();
            interceptService.changeAppIdent(updateAppIdent, relAppId, relTenantId);
        }
        return updateR;
    }

    /**
     * Delete app idents.
     *
     * @param relAppId    the rel app id
     * @param relTenantId the rel tenant id
     * @return the resp
     */
    @Transactional
    public Resp<Long> deleteAppIdents(Long relAppId, Long relTenantId) {
        var membershipCheckR = checkAppMembership(relAppId, relTenantId);
        if (!membershipCheckR.ok()) {
            return StandardResp.error(membershipCheckR);
        }
        var qAppIdent = QAppIdent.appIdent;
        sqlBuilder.select(qAppIdent.ak)
                .from(qAppIdent)
                .where(qAppIdent.relAppId.eq(relAppId))
                .fetch()
                .forEach(ak -> interceptService.deleteAppIdent(ak));
        return softDelEntities(sqlBuilder
                .selectFrom(qAppIdent)
                .where(qAppIdent.relAppId.eq(relAppId)));
    }

    /**
     * Delete app ident.
     *
     * @param appIdentId  the app ident id
     * @param relAppId    the rel app id
     * @param relTenantId the rel tenant id
     * @return the resp
     */
    @Transactional
    public Resp<Void> deleteAppIdent(Long appIdentId, Long relAppId, Long relTenantId) {
        var membershipCheckR = checkAppMembership(relAppId, relTenantId);
        if (!membershipCheckR.ok()) {
            return StandardResp.error(membershipCheckR);
        }
        var qAppIdent = QAppIdent.appIdent;
        var ak = sqlBuilder.select(qAppIdent.ak)
                .from(qAppIdent)
                .where(qAppIdent.id.eq(appIdentId))
                .where(qAppIdent.relAppId.eq(relAppId))
                .fetchOne();
        interceptService.deleteAppIdent(ak);
        return deleteEntity(sqlBuilder
                .delete(qAppIdent)
                .where(qAppIdent.id.eq(appIdentId))
                .where(qAppIdent.relAppId.eq(relAppId)));
    }

    /**
     * Check app membership.
     *
     * @param appId       the app id
     * @param relTenantId the rel tenant id
     * @return the resp
     */
    protected Resp<Void> checkAppMembership(Long appId, Long relTenantId) {
        var qApp = QApp.app;
        var num = sqlBuilder
                .selectFrom(qApp)
                .where(qApp.id.eq(appId))
                .where(qApp.relTenantId.eq(relTenantId))
                .fetchCount();
        return num != 0 ? StandardResp.success(null)
                : StandardResp.unAuthorized(BUSINESS_APP, "应用:%s 不属于租户:%s", appId, relTenantId);

    }

}
