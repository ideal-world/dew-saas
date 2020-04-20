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
import idealworld.dew.saas.common.resp.StandardResp;
import idealworld.dew.saas.common.service.domain.BasicSoftDelEntity;
import idealworld.dew.saas.common.service.domain.IdEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * Basic service.
 *
 * @param <D> the type parameter
 * @author gudaoxuri
 */
@Slf4j
public abstract class BasicService<D extends BasicSoftDelEntity> {

    /**
     * The Sql builder.
     */
    @Autowired
    protected JPAQueryFactory sqlBuilder;
    @Autowired
    private EntityManager entityManager;

    /**
     * Save entity.
     *
     * @param idEntity the id entity
     * @return the resp
     */
    protected Resp<Long> saveEntity(IdEntity idEntity) {
        entityManager.persist(idEntity);
        return StandardResp.success(idEntity.getId());
    }

    /**
     * Update entity.
     *
     * @param updateClause the update clause
     * @return the resp
     */
    protected Resp<Void> updateEntity(JPAUpdateClause updateClause) {
        var modifyRowNum = updateClause.execute();
        if (modifyRowNum == 0) {
            log.warn("没有需要更新的记录 {}", updateClause.toString());
            return StandardResp.notFound("BASIC", "没有需要更新的记录");
        }
        return StandardResp.success(null);
    }

    /**
     * Update entities.
     *
     * @param updateClause the update clause
     * @return the resp
     */
    protected Resp<Long> updateEntities(JPAUpdateClause updateClause) {
        return StandardResp.success(updateClause.execute());
    }

    /**
     * Delete entity.
     *
     * @param deleteClause the delete clause
     * @return the resp
     */
    protected Resp<Void> deleteEntity(JPADeleteClause deleteClause) {
        log.info("Delete entity , cond : {}", deleteClause.toString());
        var modifyRowNum = deleteClause.execute();
        if (modifyRowNum == 0) {
            log.warn("没有需要删除的记录 {}", deleteClause.toString());
            return StandardResp.notFound("BASIC", "没有需要删除的记录");
        }
        return StandardResp.success(null);
    }

    /**
     * Delete entities.
     *
     * @param deleteClause the delete clause
     * @return the resp
     */
    protected Resp<Long> deleteEntities(JPADeleteClause deleteClause) {
        log.info("Delete entities , cond : {}", deleteClause.toString());
        return StandardResp.success(deleteClause.execute());
    }

    /**
     * Soft del entity.
     *
     * @param <E>      the type parameter
     * @param jpaQuery the jpa query
     * @return the resp
     */
    protected <E extends IdEntity> Resp<Void> softDelEntity(JPAQuery<E> jpaQuery) {
        var entity = jpaQuery.fetchOne();
        if (entity == null) {
            log.warn("没有需要软删的记录 {}", jpaQuery.toString());
            return StandardResp.notFound("BASIC", "没有需要软删的记录");
        }
        log.info("Soft Delete entity {} , cond : {}", jpaQuery.getType().getSimpleName(), jpaQuery.toString());
        BasicSoftDelEntity basicSoftDelEntity = softDelPackage(entity);
        basicSoftDelEntity.setKind(softDelGetKind());
        basicSoftDelEntity.setEntityName(jpaQuery.getType().getSimpleName());
        basicSoftDelEntity.setRecordId(entity.getId() + "");
        basicSoftDelEntity.setContent($.json.toJsonString(entity));
        saveEntity(basicSoftDelEntity);
        entityManager.remove(entity);
        return StandardResp.success(null);
    }

    /**
     * Soft del entities.
     *
     * @param <E>      the type parameter
     * @param jpaQuery the jpa query
     * @return the resp
     */
    protected <E extends IdEntity> Resp<Long> softDelEntities(JPAQuery<E> jpaQuery) {
        log.info("Soft Delete entities {} , cond : {}", jpaQuery.getType().getSimpleName(), jpaQuery.toString());
        var deleteCounts = jpaQuery.fetch()
                .stream()
                .map(entity -> {
                    BasicSoftDelEntity basicSoftDelEntity = softDelPackage(entity);
                    basicSoftDelEntity.setKind(softDelGetKind());
                    basicSoftDelEntity.setEntityName(jpaQuery.getType().getSimpleName());
                    basicSoftDelEntity.setRecordId(entity.getId() + "");
                    basicSoftDelEntity.setContent($.json.toJsonString(entity));
                    saveEntity(basicSoftDelEntity);
                    entityManager.remove(entity);
                    return entity.getId();
                })
                .count();
        return StandardResp.success(deleteCounts);
    }

    /**
     * Soft del get kind string.
     *
     * @return the string
     */
    protected abstract String softDelGetKind();

    /**
     * Soft del package del.
     *
     * @param <E>          the type parameter
     * @param deleteEntity the delete entity
     * @return the del
     */
    protected abstract <E extends IdEntity> D softDelPackage(E deleteEntity);

    /**
     * Gets dto.
     *
     * @param <E>      the type parameter
     * @param jpaQuery the jpa query
     * @return the dto
     */
    protected <E> Resp<E> getDTO(JPAQuery<E> jpaQuery) {
        var obj = jpaQuery.fetchOne();
        if (obj == null) {
            log.warn("没有获取到记录 {}", jpaQuery.toString());
            return StandardResp.notFound("BASIC", "没有获取到记录");
        }
        return StandardResp.success(obj);
    }

    /**
     * Find dt os.
     *
     * @param <E>      the type parameter
     * @param jpaQuery the jpa query
     * @return the resp
     */
    protected <E> Resp<List<E>> findDTOs(JPAQuery<E> jpaQuery) {
        var obj = jpaQuery.fetch();
        return StandardResp.success(obj);
    }

    /**
     * Page dt os.
     *
     * @param <E>        the type parameter
     * @param jpaQuery   the jpa query
     * @param pageNumber the page number
     * @param pageSize   the page size
     * @return the resp
     */
    protected <E> Resp<Page<E>> pageDTOs(JPAQuery<E> jpaQuery, Long pageNumber, Integer pageSize) {
        var obj = jpaQuery
                .limit(pageSize)
                .offset(pageNumber == 1 ? 0 : pageNumber * pageSize)
                .fetchResults();
        return StandardResp.success(Page.build(pageNumber, pageSize, obj.getTotal(), obj.getResults()));
    }

    /**
     * Count query.
     *
     * @param <E>      the type parameter
     * @param jpaQuery the jpa query
     * @return the resp
     */
    protected <E> Resp<Long> countQuery(JPAQuery<E> jpaQuery) {
        return StandardResp.success(jpaQuery.fetchCount());
    }

    /**
     * Exist query.
     *
     * @param <E>      the type parameter
     * @param jpaQuery the jpa query
     * @return the resp
     */
    protected <E> Resp<Boolean> existQuery(JPAQuery<E> jpaQuery) {
        return StandardResp.success(jpaQuery.fetchCount() != 0);
    }

}
