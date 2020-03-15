package idealworld.dew.saas.service.ident.dto.organization;

import idealworld.dew.saas.service.ident.domain.Organization;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
@ApiModel("修改机构请求")
public class ModifyOrganizationReq implements Serializable {

    @ApiModelProperty(value = "机构类型")
    private Organization.Kind kind;

    @ApiModelProperty(value = "机构名称")
    private String name;

    @ApiModelProperty(value = "机构图标")
    private String icon;

    @ApiModelProperty(value = "机构显示排序")
    private Integer sort;

    @ApiModelProperty(value = "上级机构")
    private Long parentId;

}
