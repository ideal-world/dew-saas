package idealworld.dew.saas.service.ident.dto.account;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.io.Serializable;

@Data
@Builder
@ApiModel("添加账号岗位请求")
public class AddAccountPostReq implements Serializable {

    @Tolerate
    public AddAccountPostReq() {
    }

    @ApiModelProperty(value = "账号岗位Id", required = true)
    private Long relPostId;

}
