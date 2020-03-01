package group.idealworld.dew.saas.basic.common.service.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 * The type Safe soft delete entity.
 *
 * @author gudaoxuri
 */
@MappedSuperclass
@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public abstract class SafeSoftDelEntity extends SafeEntity {

    /**
     * The Del flag.
     */
    @Column(nullable = false)
    protected boolean delFlag = false;

}
