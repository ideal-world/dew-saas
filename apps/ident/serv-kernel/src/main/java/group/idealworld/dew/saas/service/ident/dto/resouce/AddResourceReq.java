package group.idealworld.dew.saas.service.ident.dto.resouce;

import group.idealworld.dew.saas.service.ident.domain.Resource;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
@ApiModel("添加资源请求")
public class AddResourceReq implements Serializable {

    @ApiModelProperty(value = "资源类型", required = true)
    private Resource.Kind kind;

    @ApiModelProperty(value = "资源标识", required = true)
    private String identifier;

    @ApiModelProperty(value = "资源方法")
    private String method;

    @ApiModelProperty(value = "资源名称")
    private String name;

    @ApiModelProperty(value = "资源图标")
    private String icon;

    @ApiModelProperty(value = "资源显示排序")
    private Integer sort;

    @ApiModelProperty(value = "资源所属组", required = true)
    private Long parentId;

}