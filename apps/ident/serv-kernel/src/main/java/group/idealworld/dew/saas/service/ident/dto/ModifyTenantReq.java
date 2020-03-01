package group.idealworld.dew.saas.service.ident.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@Builder
public class ModifyTenantReq implements Serializable {
    @NotNull
    private String tenantName;
    @NotNull
    private String tenantIcon;

}
