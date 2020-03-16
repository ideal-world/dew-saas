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

/**
 * The type Permission.
 *
 * @author gudaoxuri
 */
@Entity
@Table(name = "ident_permission", indexes = {
        @Index(columnList = "delFlag,relPostId,relResourceId", unique = true),
        @Index(columnList = "delFlag")
})
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Permission extends SafeSoftDelEntity {

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
