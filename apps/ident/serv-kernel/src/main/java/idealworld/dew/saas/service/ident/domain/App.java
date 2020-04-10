package idealworld.dew.saas.service.ident.domain;

import idealworld.dew.saas.common.service.domain.SafeEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

/**
 * The type App.
 *
 * @author gudaoxuri
 */
@Entity
@Table(name = "ident_app", indexes = {
        @Index(columnList = "relTenantId,name", unique = true)
})
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class App extends SafeEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String icon;

    @Column(nullable = false)
    private String parameters;

    @Column(nullable = false)
    private Long relTenantId;

}
