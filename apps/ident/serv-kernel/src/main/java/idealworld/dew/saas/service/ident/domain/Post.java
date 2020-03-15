package idealworld.dew.saas.service.ident.domain;

import idealworld.dew.saas.basic.common.service.domain.SafeEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

/**
 * The type Organization Position.
 *
 * @author gudaoxuri
 */
@Entity
@Table(name = "ident_post", indexes = {
        @Index(columnList = "relOrganizationId,relPositionCode", unique = true)
})
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Post extends SafeEntity {

    // 可以为空
    @Column(nullable = false)
    private Long relOrganizationId;

    @Column(nullable = false)
    private String relPositionCode;

    // 为空表示是系统或租户管理员
    @Column(nullable = false)
    private Long relAppId;

    // 为空表示是系统或租户管理员
    @Column(nullable = false)
    private Long relTenantId;

}
