package idealworld.dew.saas.service.ident.domain;

import com.ecfront.dew.common.exception.RTException;
import idealworld.dew.saas.common.service.domain.SafeSoftDelEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.Arrays;
import java.util.Date;

/**
 * The type Cert Account.
 *
 * @author gudaoxuri
 */
@Entity
@Table(name = "ident_account_cert", indexes = {
        @Index(columnList = "delFlag,relTenantId,kind,ak", unique = true),
        @Index(columnList = "delFlag"),
        @Index(columnList = "delFlag,relAccountId,kind")
})
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class AccountCert extends SafeSoftDelEntity {

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Kind kind;

    @Column(nullable = false)
    private String ak;

    @Column(nullable = false)
    private String sk;

    @Column(nullable = false)
    private Date validTime;

    @Column(nullable = false)
    private Long relAccountId;

    @Column(nullable = false)
    private Long relTenantId;

    public enum Kind {

        PHONE("PHONE"), USERNAME("USERNAME"), EMAIL("EMAIL"), WECHAT("WECHAT");

        private String code;

        Kind(String code) {
            this.code = code;
        }

        @Override
        public String toString() {
            return code;
        }

        public static Kind parse(String code) {
            return Arrays.stream(Kind.values())
                    .filter(item -> item.code.equalsIgnoreCase(code))
                    .findFirst()
                    .orElseThrow(() -> new RTException("Cert access key kind {" + code + "} NOT exist."));
        }
    }

}
