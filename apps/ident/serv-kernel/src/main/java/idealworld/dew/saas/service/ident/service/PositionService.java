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
import com.querydsl.core.types.Projections;
import idealworld.dew.saas.service.ident.Constant;
import idealworld.dew.saas.service.ident.domain.Position;
import idealworld.dew.saas.service.ident.domain.QPosition;
import idealworld.dew.saas.service.ident.dto.position.AddPositionReq;
import idealworld.dew.saas.service.ident.dto.position.ModifyPositionReq;
import idealworld.dew.saas.service.ident.dto.position.PositionInfoResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author gudaoxuri
 */
@Service
public class PositionService extends BasicService {

    @Autowired
    private AppService appService;

    @Transactional
    public Resp<Long> addPosition(AddPositionReq addPositionReq, Long relAppId, Long relTenantId) {
        if (!appService.checkAppMembership(relAppId, relTenantId)) {
            return Constant.RESP.NOT_FOUNT();
        }
        var position = Position.builder()
                .code(addPositionReq.getCode())
                .name(addPositionReq.getName())
                .icon(addPositionReq.getIcon() != null ? addPositionReq.getIcon() : "")
                .relAppId(relAppId)
                .relTenantId(relTenantId)
                .build();
        return saveEntity(position);
    }

    @Transactional
    public Resp<Void> modifyPosition(ModifyPositionReq modifyPositionReq, Long positionId,
                                     Long relAppId, Long relTenantId) {
        var qPosition = QPosition.position;
        var positionUpdate = sqlBuilder.update(qPosition)
                .where(qPosition.id.eq(positionId))
                .where(qPosition.relAppId.eq(relAppId))
                .where(qPosition.relTenantId.eq(relTenantId));
        if (modifyPositionReq.getName() != null) {
            positionUpdate.set(qPosition.name, modifyPositionReq.getName());
        }
        if (modifyPositionReq.getIcon() != null) {
            positionUpdate.set(qPosition.icon, modifyPositionReq.getIcon());
        }
        return updateEntity(positionUpdate);
    }

    public Resp<PositionInfoResp> getPosition(Long positionId, Long relAppId, Long relTenantId) {
        var qPosition = QPosition.position;
        var positionQuery = sqlBuilder
                .select(Projections.bean(PositionInfoResp.class,
                        qPosition.id,
                        qPosition.code,
                        qPosition.name,
                        qPosition.icon,
                        qPosition.relAppId))
                .from(qPosition)
                .where(qPosition.id.eq(positionId))
                .where(qPosition.relAppId.eq(relAppId))
                .where(qPosition.relTenantId.eq(relTenantId));
        return getDTO(positionQuery);
    }

    public Resp<List<PositionInfoResp>> findPositionInfo(Long relAppId, Long relTenantId) {
        var qPosition = QPosition.position;
        var positionQuery = sqlBuilder
                .select(Projections.bean(PositionInfoResp.class,
                        qPosition.id,
                        qPosition.code,
                        qPosition.name,
                        qPosition.icon,
                        qPosition.relAppId))
                .from(qPosition)
                .where(qPosition.relAppId.eq(relAppId))
                .where(qPosition.relTenantId.eq(relTenantId));
        return findDTOs(positionQuery);
    }

    @Transactional
    public Resp<Void> deletePosition(Long positionId, Long relAppId, Long relTenantId) {
        // TODO post permission accountPost
        var qPosition = QPosition.position;
        return deleteEntity(sqlBuilder
                .delete(qPosition)
                .where(qPosition.id.eq(positionId))
                .where(qPosition.relAppId.eq(relAppId))
                .where(qPosition.relTenantId.eq(relTenantId))
        );
    }

}