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
 * 资源类型枚举.
 *
 * @author gudaoxuri
 */
public enum ResourceKind {

    /**
     * 资源组.
     */
    GROUP("GROUP"),
    /**
     * URI.
     */
    URI("URI"),
    /**
     * 菜单.
     */
    MENU("MENU"),
    /**
     * 页面操作.
     */
    ACTION("ACTION");

    private final String code;

    ResourceKind(String code) {
        this.code = code;
    }

    /**
     * Parse resource kind.
     *
     * @param code the code
     * @return the resource kind
     */
    public static ResourceKind parse(String code) {
        return Arrays.stream(ResourceKind.values())
                .filter(item -> item.code.equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> StandardResp.e(
                        StandardResp.badRequest("BASIC",
                                "Resource kind {" + code + "} NOT exist.")));
    }

    @Override
    public String toString() {
        return code;
    }
}
