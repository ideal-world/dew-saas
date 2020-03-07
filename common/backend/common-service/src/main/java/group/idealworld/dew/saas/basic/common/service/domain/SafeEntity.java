package group.idealworld.dew.saas.basic.common.service.domain;


import group.idealworld.dew.Dew;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.util.StringUtils;

import javax.persistence.*;

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

    @PrePersist
    public void addUser() {
        Dew.auth.getOptInfo().ifPresent(optInfo -> {
            if (StringUtils.isEmpty(this.getCreateUser())) {
                this.setCreateUser((Long) optInfo.getAccountCode());
            }
            if (StringUtils.isEmpty(this.getUpdateUser())) {
                this.setUpdateUser((Long) optInfo.getAccountCode());
            }
        });
        if (StringUtils.isEmpty(this.getCreateUser())) {
            this.setCreateUser(0L);
        }
        if (StringUtils.isEmpty(this.getUpdateUser())) {
            this.setUpdateUser(0L);
        }
    }

    @PreUpdate
    public void updateUser() {
        Dew.auth.getOptInfo().ifPresent(optInfo -> {
            if (StringUtils.isEmpty(this.getUpdateUser())) {
                this.setUpdateUser((Long) optInfo.getAccountCode());
            }
        });
        if (StringUtils.isEmpty(this.getUpdateUser())) {
            this.setUpdateUser(0L);
        }
    }

}
