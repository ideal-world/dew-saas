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
@ApiModel("OAuth注册/登录请求")
public class OAuthLoginReq implements Serializable {

    @Tolerate
    public OAuthLoginReq() {
    }

    @ApiModelProperty(value = "凭证类型", notes = "只能是OAuth类型的凭证", required = true)
    private AccountCertKind certKind;

    @ApiModelProperty(value = "授权码", required = true)
    private String code;

}
