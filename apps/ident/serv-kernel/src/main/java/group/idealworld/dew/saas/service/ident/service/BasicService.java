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

import com.ecfront.dew.common.exception.RTException;
import com.querydsl.jpa.impl.JPAQueryFactory;
import group.idealworld.dew.saas.basic.common.service.domain.IdEntity;
import group.idealworld.dew.saas.basic.common.service.domain.SafeSoftDelEntity;
import group.idealworld.dew.saas.basic.common.service.domain.SoftDelEntity;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Root;

public abstract class BasicService {

    @Autowired
    protected JPAQueryFactory queryFactory;
    @Autowired
    protected EntityManager entityManager;

    protected void delete(Class<? extends IdEntity> clazz, Long entityId) {
        if (SoftDelEntity.class.isAssignableFrom(clazz) || SafeSoftDelEntity.class.isAssignableFrom(clazz)) {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaUpdate updateCriteria = cb.createCriteriaUpdate(clazz);
            Root root = updateCriteria.from(clazz);
            updateCriteria.set(root.get("delFlag"), true);
            updateCriteria.where(cb.equal(root.get("id"), entityId));
            entityManager.createQuery(updateCriteria).executeUpdate();
        } else {
            throw new RTException("Entity type error, no [del_flag] field");
        }
    }

}
