package idealworld.dew.saas.service.ident.domain;

import idealworld.dew.saas.common.service.domain.SafeSoftDelEntity;
import idealworld.dew.saas.service.ident.enumeration.OrganizationKind;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

/**
 * The type Organization.
 *
 * @author gudaoxuri
 */
@Entity
@Table(name = "ident_organization", indexes = {
        @Index(columnList = "delFlag,relTenantId,relAppId,code", unique = true),
        @Index(columnList = "delFlag")
})
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Organization extends SafeSoftDelEntity {

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrganizationKind kind;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String icon;

    @Column(nullable = false)
    private String parameters;

    @Column(nullable = false)
    private Integer sort;

    @Column(nullable = false)
    private Long parentId;

    // 为空表示租户级机构
    // TODO 暂不支持租户级机构
    @Column(nullable = false)
    private Long relAppId;

    @Column(nullable = false)
    private Long relTenantId;

}
