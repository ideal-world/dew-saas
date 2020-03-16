package idealworld.dew.saas.service.ident.dto.tenant;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
@ApiModel("租户修改请求")
public class ModifyTenantReq implements Serializable {

    @ApiModelProperty(value = "租户名称", required = true)
    private String name;

    @ApiModelProperty(value = "租户图标")
    private String icon;

    @ApiModelProperty(value = "租户扩展信息（Json格式）")
    private String parameters;

}
