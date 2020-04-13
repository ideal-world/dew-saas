package idealworld.dew.saas.service.ident.domain;

import idealworld.dew.saas.common.service.domain.SafeEntity;
import idealworld.dew.saas.service.ident.enumeration.CommonStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

/**
 * The type Account.
 *
 * @author gudaoxuri
 */
@Entity
@Table(name = "ident_account", indexes = {
        @Index(columnList = "relTenantId,status")
})
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Account extends SafeEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String avatar;

    @Column(nullable = false)
    private String parameters;

    @Column(nullable = false)
    private CommonStatus status;

    @Column(nullable = false)
    private Long relTenantId;

}
