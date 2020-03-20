package idealworld.dew.saas.service.ident.dto.app;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.io.Serializable;

@Data
@Builder
@ApiModel("添加应用请求")
public class AddAppReq implements Serializable {

    @Tolerate
    public AddAppReq() {
    }

    @ApiModelProperty(value = "应用名称", required = true)
    private String name;

    @ApiModelProperty(value = "应用图标")
    private String icon;

    @ApiModelProperty(value = "应用扩展信息（Json格式）")
    private String parameters;

}
