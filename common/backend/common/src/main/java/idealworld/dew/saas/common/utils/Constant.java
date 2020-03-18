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

package idealworld.dew.saas.common.utils;

import com.ecfront.dew.common.$;
import com.ecfront.dew.common.Resp;

import java.text.ParseException;
import java.util.Date;

/**
 * @author gudaoxuri
 */
public class Constant {

    public static Date NEVER_EXPIRE_TIME;
    public static final String ROLE_SPLIT = "-";
    public static final Long OBJECT_UNDEFINED = 0L;

    static {
        try {
            NEVER_EXPIRE_TIME = $.time().yyyy_MM_dd.parse("3000-01-01");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static class RESP {

        public static <E> Resp<E> NOT_FOUNT() {
            return Resp.notFound("找不到操作对象,请检查权限");
        }

        public static <E> Resp<E> NOT_FOUNT(String objName) {
            return Resp.notFound("找不到操作的" + objName + ",请检查权限");
        }

    }

    public static class MQ {


    }

}
