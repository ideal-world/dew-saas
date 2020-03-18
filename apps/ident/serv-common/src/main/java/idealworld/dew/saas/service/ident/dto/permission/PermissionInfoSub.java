package idealworld.dew.saas.service.ident.dto.permission;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@ApiModel("权限信息订阅")
public class PermissionInfoSub implements Serializable {

    @Tolerate
    public PermissionInfoSub() {
    }

    @ApiModelProperty(value = "删除的权限Ids")
    private List<Long> removedPermissionIds;

    @ApiModelProperty(value = "变更的权限信息")
    private List<PermissionExtInfo> changedPermissions;

}
