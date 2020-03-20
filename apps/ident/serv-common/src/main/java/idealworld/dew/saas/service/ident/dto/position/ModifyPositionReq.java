package idealworld.dew.saas.service.ident.dto.position;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.io.Serializable;

@Data
@Builder
@ApiModel("修改职位请求")
public class ModifyPositionReq implements Serializable {

    @Tolerate
    public ModifyPositionReq() {
    }

    @ApiModelProperty(value = "职位名称")
    private String name;

    @ApiModelProperty(value = "职位图标")
    private String icon;

}
