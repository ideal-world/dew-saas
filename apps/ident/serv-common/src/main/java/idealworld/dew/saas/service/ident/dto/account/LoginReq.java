package idealworld.dew.saas.service.ident.dto.account;

import idealworld.dew.saas.service.ident.enumeration.AccountCertKind;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.io.Serializable;

@Data
@Builder
@ApiModel("登录请求")
public class LoginReq implements Serializable {

    @Tolerate
    public LoginReq() {
    }

    @ApiModelProperty(value = "凭证类型", required = true)
    private AccountCertKind certKind;
    @ApiModelProperty(value = "凭证名", required = true)
    private String ak;
    @ApiModelProperty(value = "凭证密钥", required = true)
    private String sk;

}
