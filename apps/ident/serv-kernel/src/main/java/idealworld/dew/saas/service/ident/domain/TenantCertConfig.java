package idealworld.dew.saas.service.ident.domain;

import idealworld.dew.saas.common.service.domain.SafeSoftDelEntity;
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
        @Index(columnList = "delFlag,relTenantId,kind"),
        @Index(columnList = "delFlag")
})
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class TenantCertConfig extends SafeSoftDelEntity {

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
    private Long relTenantId;

}