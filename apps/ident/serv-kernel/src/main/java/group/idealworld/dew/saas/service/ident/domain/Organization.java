package group.idealworld.dew.saas.service.ident.domain;

import com.ecfront.dew.common.exception.RTException;
import group.idealworld.dew.saas.basic.common.service.domain.SafeEntity;
import group.idealworld.dew.saas.basic.common.service.domain.SafeSoftDelEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.Arrays;

/**
 * The type Organization.
 *
 * @author gudaoxuri
 */
@Entity
@Table(name = "ident_organization", indexes = {
        @Index(columnList = "relTenantId,relAppId,code")
})
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Organization extends SafeEntity {

    public enum Kind {

        ADMINISTRATION("ADMINISTRATION"), VIRTUAL("VIRTUAL");

        /**
         * 组织编码.
         *
         * 前三位
         *   000 租户级树
         *   XXX 应用级树
         * 后续编码四位一层级
         */
        private String code;

        Kind(String code) {
            this.code = code;
        }

        @Override
        public String toString() {
            return code;
        }

        public static Kind parse(String code) {
            return Arrays.stream(Kind.values())
                    .filter(item -> item.code.equalsIgnoreCase(code))
                    .findFirst()
                    .orElseThrow(() -> new RTException("Resource kind {" + code + "} NOT exist."));
        }
    }

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Kind kind;

    @Column(nullable = false)
    private String name;

    @Column
    private String icon;

    @Column
    private Integer sort;

    @Column
    private Long parentId;

    @Column(nullable = false)
    private Long relAppId;

    @Column(nullable = false)
    private Long relTenantId;

}
