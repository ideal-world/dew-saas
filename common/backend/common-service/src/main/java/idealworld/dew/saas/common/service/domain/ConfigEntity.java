package idealworld.dew.saas.common.service.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 * @author gudaoxuri
 */
@MappedSuperclass
@Data
@SuperBuilder
@NoArgsConstructor
public class ConfigEntity extends SafeEntity {

    @Column(nullable = false)
    protected String key;

    @Column(nullable = false)
    protected String value;

}
