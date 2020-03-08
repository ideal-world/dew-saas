package group.idealworld.dew.saas.service.ident.domain;

import group.idealworld.dew.saas.basic.common.service.domain.SafeEntity;
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
 * The type Organization Position.
 *
 * @author gudaoxuri
 */
@Entity
@Table(name = "ident_post", indexes = {
        @Index(columnList = "organizationCode,positionCode")
})
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Post extends SafeEntity {

    @Column(nullable = false)
    private String organizationCode;

    @Column(nullable = false)
    private String positionCode;

}
