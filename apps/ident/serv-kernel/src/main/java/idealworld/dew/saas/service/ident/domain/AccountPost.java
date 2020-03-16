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
 * The type Organization Position.
 *
 * @author gudaoxuri
 */
@Entity
@Table(name = "ident_account_post", indexes = {
        @Index(columnList = "relAccountId,relPostId", unique = true)
})
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class AccountPost extends SafeEntity {

    @Column(nullable = false)
    private Long relAccountId;

    @Column(nullable = false)
    private Long relPostId;

}
