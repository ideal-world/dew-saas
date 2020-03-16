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

import com.ecfront.dew.common.Page;
import com.ecfront.dew.common.Resp;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.impl.JPAUpdateClause;
import group.idealworld.dew.Dew;
import group.idealworld.dew.core.cluster.ClusterElection;
import idealworld.dew.saas.common.service.Constant;
import idealworld.dew.saas.common.service.domain.IdEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import java.util.List;

public abstract class BasicService {

    protected static final Logger logger = LoggerFactory.getLogger(BasicService.class);

    protected static final ClusterElection ELECTION = Dew.cluster.election.instance("ident");

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
        } else {
            return Resp.success(null);
        }
    }

    protected <E> Resp<E> getDTO(JPAQuery<E> jpaQuery) {
        var obj = jpaQuery.fetchOne();
        if (obj == null) {
            return Constant.RESP.NOT_FOUNT();
        } else {
            return Resp.success(obj);
        }
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
