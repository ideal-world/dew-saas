package idealworld.dew.saas.service.ident.utils;

import com.ecfront.dew.common.$;

/**
 * @author gudaoxuri
 */
public class KeyHelper {

    public static String generateToken() {
        return $.security.digest.digest(
                $.field.createUUID().replaceAll("\\-", ""),
                "MD5"
        );
    }

    public static String generateAK() {
        return $.field.createUUID().replaceAll("\\-", "");
    }

    public static String generateSK(String key) {
        return $.security.digest.digest(
                key + $.field.createUUID().replaceAll("\\-", ""),
                "SHA1"
        );
    }

}
