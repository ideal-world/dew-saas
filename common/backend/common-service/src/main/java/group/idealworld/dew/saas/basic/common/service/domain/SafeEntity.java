package group.idealworld.dew.saas.basic.common.service.domain;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 * The type Safe entity.
 *
 * @author gudaoxuri
 */
@MappedSuperclass
@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public abstract class SafeEntity extends TimestampEntity {

    /**
     * The Create user.
     */
    @Column(nullable = false)
    protected Long createUser;

    /**
     * The Update user.
     */
    @Column(nullable = false)
    protected Long updateUser;

}
