package idealworld.dew.saas.service.ident.dto.tenant;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@ApiModel("租户信息")
public class TenantInfoResp implements Serializable {

    @Tolerate
    public TenantInfoResp() {
    }

    @ApiModelProperty(value = "租户Id", required = true)
    private Long id;

    @ApiModelProperty(value = "租户名称", required = true)
    private String name;

    @ApiModelProperty(value = "租户图标")
    private String icon;

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
