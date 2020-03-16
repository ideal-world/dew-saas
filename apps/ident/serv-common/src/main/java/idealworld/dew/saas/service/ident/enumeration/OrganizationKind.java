package idealworld.dew.saas.service.ident.enumeration;

import com.ecfront.dew.common.exception.RTException;

import java.util.Arrays;

/**
 * @author gudaoxuri
 */
public enum OrganizationKind {

    ADMINISTRATION("ADMINISTRATION"), VIRTUAL("VIRTUAL");

    /**
     * 组织编码.
     * <p>
     * 前三位
     * 000 租户级树
     * XXX 应用级树
     * 后续编码四位一层级
     */
    private String code;

    OrganizationKind(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return code;
    }

    public static OrganizationKind parse(String code) {
        return Arrays.stream(OrganizationKind.values())
                .filter(item -> item.code.equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new RTException("Resource kind {" + code + "} NOT exist."));
    }
}
