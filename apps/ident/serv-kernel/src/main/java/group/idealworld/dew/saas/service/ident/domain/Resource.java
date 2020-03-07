package group.idealworld.dew.saas.service.ident.domain;

import com.ecfront.dew.common.exception.RTException;
import group.idealworld.dew.saas.basic.common.service.domain.SafeEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.Arrays;

/**
 * The type Resource.
 *
 * @author gudaoxuri
 */
@Entity
@Table(name = "ident_resource", indexes = {
        @Index(columnList = "relAppId,identifier,method", unique = true)
})
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Resource extends SafeEntity {

    public enum Kind {

        GROUP("GROUP"), URI("URI"), MENU("MENU"), ACTION("ACTION");

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
    @Enumerated(EnumType.STRING)
    private Kind kind;

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

    @Column(nullable = false)
    private Long relAppId;

}
