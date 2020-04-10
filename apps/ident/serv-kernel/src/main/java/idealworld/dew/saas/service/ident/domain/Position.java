package idealworld.dew.saas.service.ident.domain;

import idealworld.dew.saas.common.service.domain.SafeEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

/**
 * The type Position.
 *
 * @author gudaoxuri
 */
@Entity
@Table(name = "ident_position", indexes = {
        @Index(columnList = "relTenantId,relAppId,code",unique = true)
})
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Position extends SafeEntity {

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String icon;

    // 为空表示是系统或租户管理员
    @Column(nullable = false)
    private Long relAppId;

    // 为空表示是系统或租户管理员
    @Column(nullable = false)
    private Long relTenantId;

}
