package group.idealworld.dew.saas.service.ident.domain;

import com.ecfront.dew.common.exception.RTException;
import group.idealworld.dew.saas.basic.common.service.domain.SafeSoftDelEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.Arrays;

/**
 * The type Account.
 *
 * @author gudaoxuri
 */
@Entity
@Table(name = "ident_account", indexes = {
        @Index(columnList = "relTenantId"),
        @Index(columnList = "delFlag,name")
})
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Account extends SafeSoftDelEntity {

    public enum Status {

        DISABLED("DISABLED"), ENABLED("ENABLED");

        private String code;

        Status(String code) {
            this.code = code;
        }

        @Override
        public String toString() {
            return code;
        }

        public static Account.Status parse(String code) {
            return Arrays.stream(Account.Status.values())
                    .filter(item -> item.code.equalsIgnoreCase(code))
                    .findFirst()
                    .orElseThrow(() -> new RTException("Account status {" + code + "} NOT exist."));
        }
    }

    @Column(nullable = false)
    private String name;

    @Column
    private String avatar;

    @Column
    private String parameters;

    @Column(nullable = false)
    private Status status;

    @Column(nullable = false)
    private Long relTenantId;

}
