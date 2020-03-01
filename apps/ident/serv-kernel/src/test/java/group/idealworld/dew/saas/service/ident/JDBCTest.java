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

package group.idealworld.dew.saas.service.ident;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * The type Jdbc test.
 *
 * @author gudaoxuri
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = DewIdentApplication.class)
@Transactional
public class JDBCTest {

    /*@Autowired
    private JPAQueryFactory queryFactory;
    @Autowired
    private EntityManager entityManager;

    @Test
    public void testJDBC() throws InterruptedException {
        // Insert
        var tenant = Tenant.builder().name("测试租户").createUser("sys").updateUser("sys").delFlag(false).build();
        entityManager.persist(tenant);

        // Query
        QTenant qTenant = QTenant.tenant;
        List<Tenant> tenants = queryFactory
                .selectFrom(qTenant)
                .orderBy(
                        qTenant.id.desc()
                )
                .fetch();
        Assert.assertEquals(1, tenants.size());

        Thread.sleep(1000);
        // Update
        tenant.setName("测试租户1");
        entityManager.merge(tenant);

        var tenantNew = queryFactory
                .selectFrom(qTenant)
                .where(qTenant.id.eq(tenants.get(0).getId()))
                .fetchOne();
        Assert.assertEquals("测试租户1", tenantNew.getName());
        Assert.assertNotEquals(tenantNew.getCreateTime(), tenantNew.getUpdateTime());

        var nameNew = queryFactory
                .select(qTenant.name)
                .from(qTenant)
                .where(qTenant.id.eq(tenants.get(0).getId()))
                .fetchOne();
        Assert.assertEquals("测试租户1", nameNew);

    }*/
}
