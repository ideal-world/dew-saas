package idealworld.dew.saas.service.ident.domain;

import idealworld.dew.saas.common.service.domain.SafeSoftDelEntity;
import idealworld.dew.saas.service.ident.enumeration.AccountCertKind;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
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
    private AccountCertKind kind;

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

}
