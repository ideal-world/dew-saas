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
 * The type Position.
 *
 * @author gudaoxuri
 */
@Entity
@Table(name = "ident_position", indexes = {
        @Index(columnList = "delFlag,relTenantId,relAppId,code"),
        @Index(columnList = "delFlag")
})
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Position extends SafeSoftDelEntity {

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
