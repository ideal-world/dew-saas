package idealworld.dew.saas.service.ident.dto.permission;

import idealworld.dew.saas.service.ident.enumeration.ResourceKind;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

/**
 * @author gudaoxuri
 */
@Data
@Builder
@ApiModel("权限信息")
public class PermissionExtInfo {

    @Tolerate
    public PermissionExtInfo() {
    }

    @ApiModelProperty(value = "权限Id", required = true)
    private Long permissionId;

    @ApiModelProperty(value = "关联资源类型", required = true)
    private ResourceKind resKind;

    @ApiModelProperty(value = "关联资源Id", required = true)
    private Long resId;

    @ApiModelProperty(value = "关联资源标识", required = true)
    private String resIdentifier;

    @ApiModelProperty(value = "关联资源方法", required = true)
    private String resMethod;

    @ApiModelProperty(value = "关联机构编码")
    private String organizationCode;

    @ApiModelProperty(value = "关联岗位编码", required = true)
    private String positionCode;

    @ApiModelProperty(value = "权限所属应用", required = true)
    private Long relAppId;

}
