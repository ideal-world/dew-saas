package idealworld.dew.saas.service.ident.enumeration;

import com.ecfront.dew.common.exception.RTException;

import java.util.Arrays;

/**
 * @author gudaoxuri
 */
public enum AccountStatus {

    DISABLED("DISABLED"), ENABLED("ENABLED");

    private String code;

    AccountStatus(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return code;
    }

    public static AccountStatus parse(String code) {
        return Arrays.stream(AccountStatus.values())
                .filter(item -> item.code.equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new RTException("Account status {" + code + "} NOT exist."));
    }
}
