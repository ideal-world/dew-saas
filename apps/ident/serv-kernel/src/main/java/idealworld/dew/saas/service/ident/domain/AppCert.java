package idealworld.dew.saas.service.ident.domain;

import idealworld.dew.saas.common.service.domain.SafeSoftDelEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import java.util.Date;

/**
 * The type Cert App.
 *
 * @author gudaoxuri
 */
@Entity
@Table(name = "ident_app_cert", indexes = {
        @Index(columnList = "delFlag,ak", unique = true),
        @Index(columnList = "delFlag,relAppId,validTime"),
        @Index(columnList = "relAppId")
})
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class AppCert extends SafeSoftDelEntity {

    @Column(nullable = false)
    private String note;

    @Column(nullable = false)
    private String ak;

    @Column(nullable = false)
    private String sk;

    @Column(nullable = false)
    private Date validTime;

    @Column(nullable = false)
    private Long relAppId;
}
