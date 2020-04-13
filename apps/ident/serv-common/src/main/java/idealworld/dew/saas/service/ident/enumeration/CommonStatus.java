package idealworld.dew.saas.service.ident.enumeration;

import com.ecfront.dew.common.exception.RTException;

import java.util.Arrays;

/**
 * @author gudaoxuri
 */
public enum CommonStatus {

    DISABLED("DISABLED"), ENABLED("ENABLED");

    private String code;

    CommonStatus(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return code;
    }

    public static CommonStatus parse(String code) {
        return Arrays.stream(CommonStatus.values())
                .filter(item -> item.code.equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new RTException("Common status {" + code + "} NOT exist."));
    }
}
