package group.idealworld.dew.saas.service.ident.domain;

import group.idealworld.dew.saas.basic.common.service.domain.SafeEntity;
import group.idealworld.dew.saas.basic.common.service.domain.SafeSoftDelEntity;
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
        @Index(columnList = "relAppId,code")
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

    // relAppId为空时表示是租户或系统级角色
    @Column(nullable = false)
    private Long relAppId;

}
