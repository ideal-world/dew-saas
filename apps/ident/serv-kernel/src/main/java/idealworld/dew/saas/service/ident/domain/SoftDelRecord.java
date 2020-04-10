package idealworld.dew.saas.service.ident.domain;

import idealworld.dew.saas.common.service.domain.BasicSoftDelEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

/**
 * @author gudaoxuri
 */
@Entity
@Table(name = "ident_del_record", indexes = {
        @Index(columnList = "kind,entityName")
})
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class SoftDelRecord extends BasicSoftDelEntity {


}
