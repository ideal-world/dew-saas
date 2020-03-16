package idealworld.dew.saas.service.ident.dto.post;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.io.Serializable;

@Data
@Builder
@ApiModel("岗位信息")
public class PostInfoResp implements Serializable {

    @Tolerate
    public PostInfoResp() {
    }

    @ApiModelProperty(value = "岗位Id", required = true)
    private Long id;

    @ApiModelProperty(value = "机构Id")
    private Long relOrganizationId;

    @ApiModelProperty(value = "职位编码", required = true)
    private String relPositionCode;

    @ApiModelProperty(value = "岗位所属应用", required = true)
    private Long relAppId;

}
