package group.idealworld.dew.saas.service.ident.domain;

import group.idealworld.dew.saas.basic.common.service.domain.SafeSoftDelEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.Date;

/**
 * The type Cert App.
 *
 * @author gudaoxuri
 */
@Entity
@Table(name = "ident_cert_app", indexes = {
        @Index(columnList = "ak", unique = true),
        @Index(columnList = "relAppId"),
        @Index(columnList = "delFlag, ak")
})
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class CertApp extends SafeSoftDelEntity {

    @Column(nullable = false)
    private String ak;

    @Column(nullable = false)
    private String sk;

    @Column
    private Date validTime;

    @Column
    private Integer validTimes;

    @Column(nullable = false)
    private Long relAppId;
}
