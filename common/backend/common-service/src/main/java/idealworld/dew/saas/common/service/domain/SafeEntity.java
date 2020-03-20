package idealworld.dew.saas.common.service.domain;

import group.idealworld.dew.Dew;
import idealworld.dew.saas.common.Constant;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.util.Date;

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
public abstract class SafeEntity extends IdEntity {

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

    @PrePersist
    public void addUser() {
        Dew.auth.getOptInfo().ifPresent(optInfo -> {
            if (StringUtils.isEmpty(this.getCreateUser())) {
                this.setCreateUser(Long.valueOf((String) optInfo.getAccountCode()));
            }
            if (StringUtils.isEmpty(this.getUpdateUser())) {
                this.setUpdateUser(Long.valueOf((String) optInfo.getAccountCode()));
            }
        });
        if (StringUtils.isEmpty(this.getCreateUser())) {
            this.setCreateUser(Constant.OBJECT_UNDEFINED);
        }
        if (StringUtils.isEmpty(this.getUpdateUser())) {
            this.setUpdateUser(Constant.OBJECT_UNDEFINED);
        }
    }

    @PreUpdate
    public void updateUser() {
        Dew.auth.getOptInfo().ifPresent(optInfo -> {
            if (StringUtils.isEmpty(this.getUpdateUser())) {
                this.setUpdateUser(Long.valueOf((String) optInfo.getAccountCode()));
            }
        });
        if (StringUtils.isEmpty(this.getUpdateUser())) {
            this.setUpdateUser(Constant.OBJECT_UNDEFINED);
        }
    }

}
