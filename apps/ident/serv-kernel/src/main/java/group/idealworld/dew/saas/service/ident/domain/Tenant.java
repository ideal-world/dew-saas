package group.idealworld.dew.saas.service.ident.domain;

import group.idealworld.dew.saas.basic.common.service.domain.SafeSoftDelEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

/**
 * The type Tenant.
 *
 * @author gudaoxuri
 */
@Entity
@Table(name = "ident_tenant", indexes = {
        @Index(columnList = "name", unique = true)
})
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Tenant extends SafeSoftDelEntity {

    @Column(nullable = false)
    private String name;

    @Column
    private String icon;

}
