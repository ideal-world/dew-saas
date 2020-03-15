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

package idealworld.dew.saas.service.ident;

import idealworld.dew.saas.service.ident.domain.AccountCert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * The type tenant test.
 *
 * @author gudaoxuri
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = DewIdentApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ITTest extends BasicTest {

    @Autowired
    private TenantAdminTest tenantAdminTest;

    @Autowired
    private AuthTest authTest;

    @Test
    public void testAll() {
       var info =  tenantAdminTest.testAll();
        authTest.testAll(info._0,info._1,info._2,info._3);
        /*testUser();
        testSystemAdmin();
        testAppConnection();*/
    }

}
