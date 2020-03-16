package idealworld.dew.saas.service.ident.enumeration;

import com.ecfront.dew.common.exception.RTException;

import java.util.Arrays;

/**
 * @author gudaoxuri
 */
public enum ResourceKind {

    GROUP("GROUP"), URI("URI"), MENU("MENU"), ACTION("ACTION");

    private String code;

    ResourceKind(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return code;
    }

    public static ResourceKind parse(String code) {
        return Arrays.stream(ResourceKind.values())
                .filter(item -> item.code.equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new RTException("Resource kind {" + code + "} NOT exist."));
    }
}
