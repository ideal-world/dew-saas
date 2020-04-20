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

package idealworld.dew.saas.service.ident.enumeration;

import idealworld.dew.saas.common.resp.StandardResp;

import java.util.Arrays;

/**
 * 机构类型枚举.
 *
 * @author gudaoxuri
 */
public enum OrganizationKind {

    /**
     * 行政.
     */
    ADMINISTRATION("ADMINISTRATION"),
    /**
     * 虚拟.
     */
    VIRTUAL("VIRTUAL");

    private final String code;

    OrganizationKind(String code) {
        this.code = code;
    }

    /**
     * Parse organization kind.
     *
     * @param code the code
     * @return the organization kind
     */
    public static OrganizationKind parse(String code) {
        return Arrays.stream(OrganizationKind.values())
                .filter(item -> item.code.equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> StandardResp.e(
                        StandardResp.badRequest("BASIC",
                                "Organization kind {" + code + "} NOT exist.")));
    }

    @Override
    public String toString() {
        return code;
    }
}
