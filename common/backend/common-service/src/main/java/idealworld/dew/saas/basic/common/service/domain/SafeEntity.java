package idealworld.dew.saas.basic.common.service.domain;


import group.idealworld.dew.Dew;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.util.StringUtils;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

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
                this.setCreateUser(Long.valueOf((String) optInfo.getAccountCode()));
            }
            if (StringUtils.isEmpty(this.getUpdateUser())) {
                this.setUpdateUser(Long.valueOf((String) optInfo.getAccountCode()));
            }
        });
        if (StringUtils.isEmpty(this.getCreateUser())) {
            this.setCreateUser(-1L);
        }
        if (StringUtils.isEmpty(this.getUpdateUser())) {
            this.setUpdateUser(-1L);
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
            this.setUpdateUser(-1L);
        }
    }

}
