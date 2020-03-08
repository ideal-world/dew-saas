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
import group.idealworld.dew.saas.service.ident.domain.Position;
import group.idealworld.dew.saas.service.ident.dto.position.AddPositionReq;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author gudaoxuri
 */
@Service
public class PositionService extends BasicService {

    @Transactional
    public Resp<Long> addPosition(AddPositionReq addPositionReq, Long relAppId) {
        var position = Position.builder()
                .code(addPositionReq.getCode())
                .name(addPositionReq.getName())
                .icon(addPositionReq.getIcon() != null ? addPositionReq.getIcon() : "")
                .relAppId(relAppId)
                .build();
        entityManager.persist(position);
        return Resp.success(position.getId());
    }

}
