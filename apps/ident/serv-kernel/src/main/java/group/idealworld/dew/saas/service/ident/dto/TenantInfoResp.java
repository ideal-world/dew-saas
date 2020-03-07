package group.idealworld.dew.saas.service.ident.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("租户信息")
public class TenantInfoResp implements Serializable {
    @ApiModelProperty(value = "租户Id",required = true)
    private Long id;
    @ApiModelProperty(value = "租户名称",required = true)
    private String name;
    @ApiModelProperty(value = "租户图标")
    private String icon;

}
