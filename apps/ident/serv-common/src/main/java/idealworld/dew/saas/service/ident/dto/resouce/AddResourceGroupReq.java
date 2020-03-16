package idealworld.dew.saas.service.ident.dto.resouce;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
@ApiModel("添加资源组请求")
public class AddResourceGroupReq implements Serializable {

    @ApiModelProperty(value = "资源组名称", required = true)
    private String name;

    @ApiModelProperty(value = "资源组图标")
    private String icon;

    @ApiModelProperty(value = "资源组显示排序")
    private Integer sort;

    @ApiModelProperty(value = "资源组所属节点")
    private Long parentId;

}
