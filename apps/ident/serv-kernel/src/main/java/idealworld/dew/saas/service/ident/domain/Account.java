package idealworld.dew.saas.service.ident.domain;

import idealworld.dew.saas.common.service.domain.SafeSoftDelEntity;
import idealworld.dew.saas.service.ident.enumeration.AccountStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

/**
 * The type Account.
 *
 * @author gudaoxuri
 */
@Entity
@Table(name = "ident_account", indexes = {
        @Index(columnList = "delFlag,relTenantId,status"),
        @Index(columnList = "relTenantId")
})
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Account extends SafeSoftDelEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String avatar;

    @Column(nullable = false)
    private String parameters;

    @Column(nullable = false)
    private AccountStatus status;

    @Column(nullable = false)
    private Long relTenantId;

}
