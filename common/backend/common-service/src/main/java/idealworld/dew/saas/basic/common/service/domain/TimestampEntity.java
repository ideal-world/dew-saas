package idealworld.dew.saas.basic.common.service.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

/**
 * The type Timestamp entity.
 *
 * @author gudaoxuri
 */
@MappedSuperclass
@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public abstract class TimestampEntity extends IdEntity {

    /**
     * The Create time.
     */
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column
    protected Date createTime;

    /**
     * The Update time.
     */
    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column
    protected Date updateTime;

}
