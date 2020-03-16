package idealworld.dew.saas.service.ident.dto.position;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.io.Serializable;

@Data
@Builder
@ApiModel("职位信息")
public class PositionInfoResp implements Serializable {

    @Tolerate
    public PositionInfoResp() {
    }

    @ApiModelProperty(value = "职位Id", required = true)
    private Long id;

    @ApiModelProperty(value = "职位编码", required = true)
    private String code;

    @ApiModelProperty(value = "职位名称", required = true)
    private String name;

    @ApiModelProperty(value = "职位图标")
    private String icon;

    @ApiModelProperty(value = "职位所属应用", required = true)
    private Long relAppId;

}
