package idealworld.dew.saas.service.ident.dto.permission;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.io.Serializable;

@Data
@Builder
@ApiModel("权限信息")
public class PermissionInfoResp implements Serializable {

    @Tolerate
    public PermissionInfoResp() {
    }

    @ApiModelProperty(value = "权限Id", required = true)
    private Long id;

    @ApiModelProperty(value = "关联岗位", required = true)
    private Long relPostId;

    @ApiModelProperty(value = "关联资源（组）", required = true)
    private Long relResourceId;

    @ApiModelProperty(value = "权限所属应用", required = true)
    private Long relAppId;

}
