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

package idealworld.dew.saas.common.service;

import com.ecfront.dew.common.$;
import com.ecfront.dew.common.Page;
import com.ecfront.dew.common.Resp;
import com.querydsl.jpa.impl.JPADeleteClause;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.impl.JPAUpdateClause;
import idealworld.dew.saas.common.Constant;
import idealworld.dew.saas.common.service.domain.BasicSoftDelEntity;
import idealworld.dew.saas.common.service.domain.IdEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import java.util.List;

@Slf4j
public abstract class BasicService<DEL extends BasicSoftDelEntity> {

    @Autowired
    protected JPAQueryFactory sqlBuilder;
    @Autowired
    private EntityManager entityManager;

    protected Resp<Long> saveEntity(IdEntity idEntity) {
        entityManager.persist(idEntity);
        return Resp.success(idEntity.getId());
    }

    protected Resp<Void> updateEntity(JPAUpdateClause updateClause) {
        var modifyRowNum = updateClause.execute();
        if (modifyRowNum == 0) {
            return Constant.RESP.NOT_FOUNT();
        }
        return Resp.success(null);
    }

    protected Resp<Long> updateEntities(JPAUpdateClause updateClause) {
        return Resp.success(updateClause.execute());
    }

    protected Resp<Void> deleteEntity(JPADeleteClause deleteClause) {
        log.info("Delete entity , cond : {}", deleteClause.toString());
        var modifyRowNum = deleteClause.execute();
        if (modifyRowNum == 0) {
            return Constant.RESP.NOT_FOUNT();
        }
        return Resp.success(null);
    }

    protected Resp<Long> deleteEntities(JPADeleteClause deleteClause) {
        log.info("Delete entities , cond : {}", deleteClause.toString());
        return Resp.success(deleteClause.execute());
    }

    protected <E extends IdEntity> Resp<Void> softDelEntity(JPAQuery<E> jpaQuery) {
        var entity = jpaQuery.fetchOne();
        if (entity == null) {
            return Constant.RESP.NOT_FOUNT();
        }
        log.info("Soft Delete entity {} , cond : {}", jpaQuery.getType().getSimpleName(), jpaQuery.toString());
        BasicSoftDelEntity basicSoftDelEntity = softDelPackage(entity);
        basicSoftDelEntity.setKind(softDelGetKind());
        basicSoftDelEntity.setEntityName(jpaQuery.getType().getSimpleName());
        basicSoftDelEntity.setRecordId(entity.getId());
        basicSoftDelEntity.setContent($.json.toJsonString(entity));
        saveEntity(basicSoftDelEntity);
        entityManager.remove(entity);
        return Resp.success(null);
    }

    protected <E extends IdEntity> Resp<Long> softDelEntities(JPAQuery<E> jpaQuery) {
        log.info("Soft Delete entities {} , cond : {}", jpaQuery.getType().getSimpleName(), jpaQuery.toString());
        var deleteCounts = jpaQuery.fetch()
                .stream()
                .map(entity -> {
                    BasicSoftDelEntity basicSoftDelEntity = softDelPackage(entity);
                    basicSoftDelEntity.setKind(softDelGetKind());
                    basicSoftDelEntity.setEntityName(jpaQuery.getType().getSimpleName());
                    basicSoftDelEntity.setRecordId(entity.getId());
                    basicSoftDelEntity.setContent($.json.toJsonString(entity));
                    saveEntity(basicSoftDelEntity);
                    entityManager.remove(entity);
                    return entity.getId();
                })
                .count();
        return Resp.success(deleteCounts);
    }

    protected abstract String softDelGetKind();

    protected abstract <E extends IdEntity> DEL softDelPackage(E deleteEntity);

    protected <E> Resp<E> getDTO(JPAQuery<E> jpaQuery) {
        var obj = jpaQuery.fetchOne();
        if (obj == null) {
            return Constant.RESP.NOT_FOUNT();
        }
        return Resp.success(obj);
    }

    protected <E> Resp<List<E>> findDTOs(JPAQuery<E> jpaQuery) {
        var obj = jpaQuery.fetch();
        return Resp.success(obj);
    }

    protected <E> Resp<Page<E>> pageDTOs(JPAQuery<E> jpaQuery, Long pageNumber, Integer pageSize) {
        var obj = jpaQuery
                .limit(pageSize)
                .offset(pageNumber == 1 ? 0 : pageNumber * pageSize)
                .fetchResults();
        return Resp.success(Page.build(pageNumber, pageSize, obj.getTotal(), obj.getResults()));
    }

    protected <E> Resp<Long> countQuery(JPAQuery<E> jpaQuery) {
        return Resp.success(jpaQuery.fetchCount());
    }

    protected <E> Resp<Boolean> existQuery(JPAQuery<E> jpaQuery) {
        return Resp.success(jpaQuery.fetchCount() != 0);
    }

}
