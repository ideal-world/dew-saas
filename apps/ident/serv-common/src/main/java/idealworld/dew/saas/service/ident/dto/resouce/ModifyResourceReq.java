package idealworld.dew.saas.service.ident.dto.resouce;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.io.Serializable;

@Data
@Builder
@ApiModel("修改资源（组）请求")
public class ModifyResourceReq implements Serializable {

    @Tolerate
    public ModifyResourceReq() {
    }

    @ApiModelProperty(value = "资源标识")
    private String identifier;

    @ApiModelProperty(value = "资源方法")
    private String method;

    @ApiModelProperty(value = "资源（组）名称")
    private String name;

    @ApiModelProperty(value = "资源（组）图标")
    private String icon;

    @ApiModelProperty(value = "资源（组）显示排序")
    private Integer sort;

    @ApiModelProperty(value = "资源所属组")
    private Long parentId;

}
