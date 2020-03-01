package group.idealworld.dew.saas.basic.common.service.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 * The type Soft delete entity.
 *
 * @author gudaoxuri
 */
@MappedSuperclass
@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public abstract class SoftDelEntity extends IdEntity {

    /**
     * The Del flag.
     */
    @Column(nullable = false)
    protected Boolean delFlag = false;

}
