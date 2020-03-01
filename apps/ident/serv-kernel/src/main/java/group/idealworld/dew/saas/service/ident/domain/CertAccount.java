package group.idealworld.dew.saas.service.ident.domain;

import com.ecfront.dew.common.exception.RTException;
import group.idealworld.dew.saas.basic.common.service.domain.SafeSoftDelEntity;
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
@Table(name = "ident_cert_account", indexes = {
        @Index(columnList = "delFlag,kind,ak"),
        @Index(columnList = "relAccountId,kind,delFlag")
})
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class CertAccount extends SafeSoftDelEntity {

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

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Kind kind;

    @Column(nullable = false)
    private String ak;

    @Column(nullable = false)
    private String sk;

    @Column
    private Date validTime;

    @Column
    private Integer validTimes;

    @Column(nullable = false)
    private Long relAccountId;

}
