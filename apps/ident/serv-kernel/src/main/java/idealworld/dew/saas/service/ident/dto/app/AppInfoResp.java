package idealworld.dew.saas.service.ident.dto.app;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@ApiModel("应用信息")
public class AppInfoResp implements Serializable {

    @Tolerate
    public AppInfoResp() {
    }

    @ApiModelProperty(value = "应用Id", required = true)
    private Long id;

    @ApiModelProperty(value = "应用名称", required = true)
    private String name;

    @ApiModelProperty(value = "应用图标")
    private String icon;

    @ApiModelProperty(value = "应用所属租户", required = true)
    private Long relTenantId;

    @ApiModelProperty(value = "是否删除", required = true)
    private boolean delFlag;

    @ApiModelProperty(value = "创建者", required = true)
    protected String createUserName;

    @ApiModelProperty(value = "最后一次更新者", required = true)
    protected String updateUserName;

    @ApiModelProperty(value = "创建时间", required = true)
    protected Date createTime;

    @ApiModelProperty(value = "最后一次更新时间", required = true)
    protected Date updateTime;
}
