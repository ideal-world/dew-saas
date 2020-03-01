package group.idealworld.dew.saas.service.ident.dto;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantInfoResp implements Serializable {

    @NotNull
    private Long id;
    @NotNull
    private String name;
    @NotNull
    private String icon;

}
