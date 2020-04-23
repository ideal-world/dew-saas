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

package idealworld.dew.saas.common.hwc.api.common.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * The type String helper.
 *
 * @author gudaoxuri
 */
public class StringHelper {

    /**
     * Read fully as string string.
     *
     * @param inputStream the input stream
     * @param encoding    the encoding
     * @return the string
     * @throws IOException the io exception
     */
    public static String readFullyAsString(InputStream inputStream, Charset encoding)
            throws IOException {
        return readFully(inputStream).toString(encoding.name());
    }

    /**
     * Read fully as bytes byte [ ].
     *
     * @param inputStream the input stream
     * @return the byte [ ]
     * @throws IOException the io exception
     */
    public static byte[] readFullyAsBytes(InputStream inputStream)
            throws IOException {
        return readFully(inputStream).toByteArray();
    }

    private static ByteArrayOutputStream readFully(InputStream inputStream)
            throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            baos.write(buffer, 0, length);
        }
        return baos;
    }

}
