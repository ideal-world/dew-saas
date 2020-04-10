package idealworld.dew.saas.common.service.domain;

import group.idealworld.dew.Dew;
import idealworld.dew.saas.common.Constant;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.util.Date;

/**
 * @author gudaoxuri
 */
@MappedSuperclass
@Data
@SuperBuilder
@NoArgsConstructor
public class BasicSoftDelEntity extends IdEntity {

    @Column(nullable = false)
    protected String kind;

    @Column(nullable = false)
    protected String entityName;

    @Column(nullable = false)
    protected Long recordId;

    @Column(nullable = false)
    protected String content;

    /**
     * The Create time.
     */
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column
    protected Date deleteTime;

    /**
     * The Create user.
     */
    @Column(nullable = false)
    protected Long deleteUser;

    @PrePersist
    public void addUser() {
        Dew.auth.getOptInfo().ifPresent(optInfo -> {
            if (StringUtils.isEmpty(this.getDeleteUser())) {
                this.setDeleteUser(Long.valueOf((String) optInfo.getAccountCode()));
            }
        });
        if (StringUtils.isEmpty(this.getDeleteUser())) {
            this.setDeleteUser(Constant.OBJECT_UNDEFINED);
        }
    }

}
