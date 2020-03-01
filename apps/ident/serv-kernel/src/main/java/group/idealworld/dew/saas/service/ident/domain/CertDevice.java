package group.idealworld.dew.saas.service.ident.domain;

import group.idealworld.dew.saas.basic.common.service.domain.SafeSoftDelEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.Date;

/**
 * The type Cert device.
 *
 * @author gudaoxuri
 */
@Entity
@Table(name = "ident_cert_device", indexes = {
        @Index(columnList = "relAccountId"),
        @Index(columnList = "delFlag,sn")
})
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class CertDevice extends SafeSoftDelEntity {

    @Column(nullable = false)
    private String sn;

    @Column(nullable = false)
    private String name;

    @Column
    private Date validTime;

    @Column(nullable = false)
    private Long relAccountId;

}
