package idealworld.dew.saas.service.ident.domain;

import idealworld.dew.saas.common.service.domain.SafeEntity;
import idealworld.dew.saas.service.ident.enumeration.ResourceKind;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

/**
 * The type Resource.
 *
 * @author gudaoxuri
 */
@Entity
@Table(name = "ident_resource", indexes = {
        @Index(columnList = "relAppId,identifier,method", unique = true),
        @Index(columnList = "relTenantId,relAppId"),
        @Index(columnList = "parentId")
})
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Resource extends SafeEntity {

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ResourceKind kind;

    @Column(nullable = false)
    private String identifier;

    @Column(nullable = false)
    private String method;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String icon;

    @Column(nullable = false)
    private Integer sort;

    @Column(nullable = false)
    private Long parentId;

    // 为空表示是系统或租户控制台资源
    @Column(nullable = false)
    private Long relAppId;

    // 为空表示是系统或租户控制台资源
    @Column(nullable = false)
    private Long relTenantId;

}
