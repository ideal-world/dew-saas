package idealworld.dew.saas.service.ident.domain;

import idealworld.dew.saas.common.service.domain.SafeEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

/**
 * The type Organization Position.
 *
 * @author gudaoxuri
 */
@Entity
@Table(name = "ident_post", indexes = {
        @Index(columnList = "relTenantId,relAppId,relPositionCode,relOrganizationCode",unique = true),
        @Index(columnList = "relOrganizationCode"),
        @Index(columnList = "relPositionCode")
})
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Post extends SafeEntity {

    // 可以为空
    @Column(nullable = false)
    private String relOrganizationCode;

    @Column(nullable = false)
    private String relPositionCode;

    // 为空表示是系统或租户管理员
    @Column(nullable = false)
    private Long relAppId;

    // 为空表示是系统或租户管理员
    @Column(nullable = false)
    private Long relTenantId;

}
