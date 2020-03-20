package idealworld.dew.saas.service.ident.dto.account;

import idealworld.dew.saas.service.ident.enumeration.AccountCertKind;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@ApiModel("添加账号凭证请求")
public class AddAccountCertReq implements Serializable {

    @Tolerate
    public AddAccountCertReq() {
    }

    @ApiModelProperty(value = "账号凭证类型名称", required = true)
    private AccountCertKind kind;

    @ApiModelProperty(value = "账号凭证名称", required = true)
    private String ak;

    @ApiModelProperty(value = "账号凭证密钥", notes = "手机、邮件的凭证类型对应的sk为验证码", required = true)
    private String sk;

}
