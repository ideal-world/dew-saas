package idealworld.dew.saas.service.ident.dto.post;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
@ApiModel("添加岗位请求")
public class AddPostReq implements Serializable {

    @ApiModelProperty(value = "机构Id")
    private String relOrganizationCode;

    @ApiModelProperty(value = "职位编码", required = true)
    private String relPositionCode;

}
