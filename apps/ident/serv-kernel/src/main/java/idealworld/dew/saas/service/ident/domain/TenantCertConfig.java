package idealworld.dew.saas.service.ident.domain;

import idealworld.dew.saas.common.service.domain.SafeEntity;
import idealworld.dew.saas.service.ident.enumeration.AccountCertKind;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

/**
 * The type Tenant.
 *
 * @author gudaoxuri
 */
@Entity
@Table(name = "ident_tenant_cert_cfg", indexes = {
        @Index(columnList = "relTenantId,kind",unique = true)
})
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class TenantCertConfig extends SafeEntity {

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountCertKind kind;

    @Column(nullable = false)
    private String validRuleNote;

    @Column(nullable = false)
    private String validRule;

    @Column(nullable = false)
    private Long validTimeSec;

    @Column(nullable = false)
    private String oauthAk;

    @Column(nullable = false)
    private String oauthSk;

    @Column(nullable = false)
    private Long relTenantId;

}
