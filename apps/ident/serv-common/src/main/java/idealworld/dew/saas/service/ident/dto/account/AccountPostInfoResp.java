package idealworld.dew.saas.service.ident.dto.account;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.io.Serializable;

@Data
@Builder
@ApiModel("账号岗位信息")
public class AccountPostInfoResp implements Serializable {

    @Tolerate
    public AccountPostInfoResp() {
    }

    @ApiModelProperty(value = "账号岗位Id", required = true)
    private Long id;

    @ApiModelProperty(value = "岗位Id", required = true)
    private Long relPostId;


}
