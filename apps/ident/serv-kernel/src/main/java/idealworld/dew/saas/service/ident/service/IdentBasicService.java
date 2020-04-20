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

import group.idealworld.dew.Dew;
import group.idealworld.dew.core.cluster.ClusterElection;
import idealworld.dew.saas.common.service.BasicService;
import idealworld.dew.saas.common.service.domain.IdEntity;
import idealworld.dew.saas.service.ident.domain.SoftDelRecord;

/**
 * Ident basic service.
 *
 * @author gudaoxuri
 */
public abstract class IdentBasicService extends BasicService<SoftDelRecord> {

    /**
     * The constant ELECTION.
     */
    protected static final ClusterElection ELECTION = Dew.cluster.election.instance("ident");

    @Override
    protected String softDelGetKind() {
        return "IDENT";
    }

    @Override
    protected <E extends IdEntity> SoftDelRecord softDelPackage(E deleteEntity) {
        return SoftDelRecord.builder().build();
    }

}
