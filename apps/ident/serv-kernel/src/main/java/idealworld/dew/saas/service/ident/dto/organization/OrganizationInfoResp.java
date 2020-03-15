package idealworld.dew.saas.service.ident.dto.organization;

import idealworld.dew.saas.service.ident.domain.Organization;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@ApiModel("添加机构请求")
public class OrganizationInfoResp implements Serializable {

    @Tolerate
    public OrganizationInfoResp() {
    }

    @ApiModelProperty(value = "机构Id", required = true)
    private Long id;

    @ApiModelProperty(value = "机构类型", required = true)
    private Organization.Kind kind;

    @ApiModelProperty(value = "机构名称", required = true)
    private String name;

    @ApiModelProperty(value = "机构图标")
    private String icon;

    @ApiModelProperty(value = "机构显示排序")
    private Integer sort;

    @ApiModelProperty(value = "上级机构", required = true)
    private Long parentId;

    @ApiModelProperty(value = "机构所属应用", required = true)
    private Long relAppId;

    @ApiModelProperty(value = "机构所属租户", required = true)
    private Long relTenantId;

    @ApiModelProperty(value = "创建者", required = true)
    protected String createUserName;

    @ApiModelProperty(value = "最后一次更新者", required = true)
    protected String updateUserName;

    @ApiModelProperty(value = "创建时间", required = true)
    protected Date createTime;

    @ApiModelProperty(value = "最后一次更新时间", required = true)
    protected Date updateTime;

}
