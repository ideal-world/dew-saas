package idealworld.dew.saas.service.ident.enumeration;

import com.ecfront.dew.common.exception.RTException;

import java.util.Arrays;

/**
 * @author gudaoxuri
 */
public enum AccountCertKind {

    PHONE("PHONE"), USERNAME("USERNAME"), EMAIL("EMAIL"), WECHAT_MP("WECHAT_MP");

    private String code;

    AccountCertKind(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return code;
    }

    public static AccountCertKind parse(String code) {
        return Arrays.stream(AccountCertKind.values())
                .filter(item -> item.code.equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new RTException("Cert access key kind {" + code + "} NOT exist."));
    }
}
