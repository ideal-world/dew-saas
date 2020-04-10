package idealworld.dew.saas.service.ident.domain;

import idealworld.dew.saas.common.service.domain.SafeEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

/**
 * The type Organization Position.
 *
 * @author gudaoxuri
 */
@Entity
@Table(name = "ident_account_post", indexes = {
        @Index(columnList = "relAccountId,relPostId", unique = true),
        @Index(columnList = "relAccountId"),
        @Index(columnList = "relPostId")
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
