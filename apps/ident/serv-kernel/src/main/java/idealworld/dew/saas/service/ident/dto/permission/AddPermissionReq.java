package idealworld.dew.saas.service.ident.dto.permission;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
@ApiModel("添加权限请求")
public class AddPermissionReq implements Serializable {

    @ApiModelProperty(value = "关联岗位", required = true)
    private Long relPostId;

    @ApiModelProperty(value = "关联资源（组）", required = true)
    private Long relResourceId;

}
