package idealworld.dew.saas.service.ident.dto.app;

import idealworld.dew.saas.service.ident.enumeration.CommonStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.io.Serializable;

@Data
@Builder
@ApiModel("修改应用请求")
public class ModifyAppReq implements Serializable {

    @Tolerate
    public ModifyAppReq() {
    }

    @ApiModelProperty(value = "应用名称")
    private String name;

    @ApiModelProperty(value = "应用图标")
    private String icon;

    @ApiModelProperty(value = "应用扩展信息（Json格式）")
    private String parameters;

    @ApiModelProperty(value = "应用状态")
    private CommonStatus status;

}
