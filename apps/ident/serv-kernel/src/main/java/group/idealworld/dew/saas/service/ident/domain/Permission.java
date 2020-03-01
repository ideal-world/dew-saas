package group.idealworld.dew.saas.service.ident.domain;

import group.idealworld.dew.saas.basic.common.service.domain.SafeEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

/**
 * The type Permission.
 *
 * @author gudaoxuri
 */
@Entity
@Table(name = "ident_permission", indexes = {
        @Index(columnList = "organizationCode,positionCode"),
        @Index(columnList = "resourceCode")
})
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Permission extends SafeEntity {

    @Column(nullable = false)
    private String organizationCode;

    @Column(nullable = false)
    private String positionCode;

    @Column(nullable = false)
    private String resourceCode;

}
