package idealworld.dew.saas.service.ident.dto.resouce;

import idealworld.dew.saas.service.ident.domain.Resource;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.io.Serializable;

@Data
@Builder
@ApiModel("资源信息")
public class ResourceInfoResp implements Serializable {

    @Tolerate
    public ResourceInfoResp() {
    }

    @ApiModelProperty(value = "资源（组）Id", required = true)
    private Long id;

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

    @ApiModelProperty(value = "资源所属组")
    private Long parentId;

    @ApiModelProperty(value = "资源所属应用", required = true)
    private Long relAppId;

}
