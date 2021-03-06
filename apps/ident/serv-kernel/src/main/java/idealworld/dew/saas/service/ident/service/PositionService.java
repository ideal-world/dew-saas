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
import idealworld.dew.saas.common.resp.StandardResp;
import idealworld.dew.saas.service.ident.domain.Position;
import idealworld.dew.saas.service.ident.domain.QPosition;
import idealworld.dew.saas.service.ident.dto.position.AddPositionReq;
import idealworld.dew.saas.service.ident.dto.position.ModifyPositionReq;
import idealworld.dew.saas.service.ident.dto.position.PositionInfoResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Position service.
 *
 * @author gudaoxuri
 */
@Service
public class PositionService extends IdentBasicService {

    private static final String BUSINESS_POSITION = "POSITION";

    @Autowired
    private AppService appService;
    @Autowired
    private PostService postService;

    /**
     * Add position.
     *
     * @param addPositionReq the add position req
     * @param relAppId       the rel app id
     * @param relTenantId    the rel tenant id
     * @return the resp
     */
    @Transactional
    public Resp<Long> addPosition(AddPositionReq addPositionReq, Long relAppId, Long relTenantId) {
        var membershipCheckR = appService.checkAppMembership(relAppId, relTenantId);
        if (!membershipCheckR.ok()) {
            return StandardResp.error(membershipCheckR);
        }
        var qPosition = QPosition.position;
        if (sqlBuilder.select(qPosition.id)
                .from(qPosition)
                .where(qPosition.relTenantId.eq(relTenantId))
                .where(qPosition.relAppId.eq(relAppId))
                .where(qPosition.code.eq(addPositionReq.getCode()))
                .fetchCount() != 0) {
            return StandardResp.conflict(BUSINESS_POSITION, "职位已存在");
        }
        var position = Position.builder()
                .code(addPositionReq.getCode())
                .name(addPositionReq.getName())
                .icon(addPositionReq.getIcon() != null ? addPositionReq.getIcon() : "")
                .sort(addPositionReq.getSort() != null ? addPositionReq.getSort() : 0)
                .relAppId(relAppId)
                .relTenantId(relTenantId)
                .build();
        return saveEntity(position);
    }

    /**
     * Modify position.
     *
     * @param modifyPositionReq the modify position req
     * @param positionId        the position id
     * @param relAppId          the rel app id
     * @param relTenantId       the rel tenant id
     * @return the resp
     */
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
        if (modifyPositionReq.getSort() != null) {
            positionUpdate.set(qPosition.sort, modifyPositionReq.getSort());
        }
        return updateEntity(positionUpdate);
    }

    /**
     * Gets position.
     *
     * @param positionId  the position id
     * @param relAppId    the rel app id
     * @param relTenantId the rel tenant id
     * @return the position
     */
    public Resp<PositionInfoResp> getPosition(Long positionId, Long relAppId, Long relTenantId) {
        var qPosition = QPosition.position;
        var positionQuery = sqlBuilder
                .select(Projections.bean(PositionInfoResp.class,
                        qPosition.id,
                        qPosition.code,
                        qPosition.name,
                        qPosition.icon,
                        qPosition.sort,
                        qPosition.relAppId))
                .from(qPosition)
                .where(qPosition.id.eq(positionId))
                .where(qPosition.relAppId.eq(relAppId))
                .where(qPosition.relTenantId.eq(relTenantId));
        return getDTO(positionQuery);
    }

    /**
     * Find position info.
     *
     * @param relAppId    the rel app id
     * @param relTenantId the rel tenant id
     * @return the resp
     */
    public Resp<List<PositionInfoResp>> findPositionInfo(Long relAppId, Long relTenantId) {
        var qPosition = QPosition.position;
        var positionQuery = sqlBuilder
                .select(Projections.bean(PositionInfoResp.class,
                        qPosition.id,
                        qPosition.code,
                        qPosition.name,
                        qPosition.icon,
                        qPosition.sort,
                        qPosition.relAppId))
                .from(qPosition)
                .where(qPosition.relAppId.eq(relAppId))
                .where(qPosition.relTenantId.eq(relTenantId));
        return findDTOs(positionQuery);
    }

    /**
     * Delete position.
     *
     * @param positionId  the position id
     * @param relAppId    the rel app id
     * @param relTenantId the rel tenant id
     * @return the resp
     */
    @Transactional
    public Resp<Void> deletePosition(Long positionId, Long relAppId, Long relTenantId) {
        var qPosition = QPosition.position;
        var positionCode = sqlBuilder
                .select(qPosition.code)
                .from(qPosition)
                .where(qPosition.id.eq(positionId))
                .where(qPosition.relAppId.eq(relAppId))
                .where(qPosition.relTenantId.eq(relTenantId))
                .fetchOne();
        // 删除岗位、账号岗位、权限
        postService.deletePostByPositionCodes(new ArrayList<>() {
            {
                add(positionCode);
            }
        }, relAppId, relTenantId);
        // 删除职位
        return deleteEntity(sqlBuilder
                .delete(qPosition)
                .where(qPosition.id.eq(positionId))
                .where(qPosition.relAppId.eq(relAppId))
                .where(qPosition.relTenantId.eq(relTenantId))
        );
    }

    /**
     * Delete positions.
     *
     * @param relAppId    the rel app id
     * @param relTenantId the rel tenant id
     * @return the resp
     */
    @Transactional
    protected Resp<Long> deletePositions(Long relAppId, Long relTenantId) {
        var qPosition = QPosition.position;
        var positionCodes = sqlBuilder
                .select(qPosition.code)
                .from(qPosition)
                .where(qPosition.relAppId.eq(relAppId))
                .where(qPosition.relTenantId.eq(relTenantId))
                .fetch();
        // 删除岗位、账号岗位、权限
        postService.deletePostByPositionCodes(positionCodes, relAppId, relTenantId);
        // 删除职位
        return deleteEntities(sqlBuilder
                .delete(qPosition)
                .where(qPosition.relAppId.eq(relAppId))
                .where(qPosition.relTenantId.eq(relTenantId))
        );
    }
}
