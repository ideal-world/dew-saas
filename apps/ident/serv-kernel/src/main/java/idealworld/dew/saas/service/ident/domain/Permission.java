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
 * The type Permission.
 *
 * @author gudaoxuri
 */
@Entity
@Table(name = "ident_permission", indexes = {
        @Index(columnList = "relPostId"),
        @Index(columnList = "relResourceId")
})
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Permission extends SafeEntity {

    @Column(nullable = false)
    private Long relPostId;

    @Column(nullable = false)
    private Long relResourceId;

    // 为空表示是系统或租户管理员权限
    @Column(nullable = false)
    private Long relAppId;

    // 为空表示是系统或租户管理员权限
    @Column(nullable = false)
    private Long relTenantId;

}
