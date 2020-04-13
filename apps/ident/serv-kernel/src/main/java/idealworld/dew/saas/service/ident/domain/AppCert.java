package idealworld.dew.saas.service.ident.domain;

import idealworld.dew.saas.common.service.domain.SafeEntity;
import idealworld.dew.saas.service.ident.enumeration.CommonStatus;
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
@Table(name = "ident_app_cert", indexes = {
        @Index(columnList = "ak", unique = true),
        @Index(columnList = "relAppId,validTime"),
        @Index(columnList = "status")
})
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class AppCert extends SafeEntity {

    @Column(nullable = false)
    private String note;

    @Column(nullable = false)
    private String ak;

    @Column(nullable = false)
    private String sk;

    @Column(nullable = false)
    private CommonStatus status;

    @Column(nullable = false)
    private Date validTime;

    @Column(nullable = false)
    private Long relAppId;
}
