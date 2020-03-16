package idealworld.dew.saas.service.ident.dto.position;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
@ApiModel("添加职位请求")
public class AddPositionReq implements Serializable {

    @ApiModelProperty(value = "职位编码", required = true)
    private String code;

    @ApiModelProperty(value = "职位名称", required = true)
    private String name;

    @ApiModelProperty(value = "职位图标")
    private String icon;

}
