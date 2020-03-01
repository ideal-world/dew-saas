package group.idealworld.dew.saas.service.ident.dto;

import group.idealworld.dew.saas.service.ident.domain.CertAccount;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@Builder
public class RegisterTenantReq implements Serializable {
    @NotNull
    private String tenantName;
    @NotNull
    private String accountName;
    @NotNull
    private CertAccount.Kind certKind;
    @NotNull
    private String ak;
    @NotNull
    private String sk;

}
