package group.idealworld.dew.saas.service.ident.dto.account;

import group.idealworld.dew.saas.service.ident.domain.AccountCert;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@ApiModel("添加账号凭证请求")
public class AddAccountCertReq implements Serializable {

    @ApiModelProperty(value = "账号凭证类型名称", required = true)
    private AccountCert.Kind kind;

    @ApiModelProperty(value = "账号凭证名称", required = true)
    private String ak;

    @ApiModelProperty(value = "账号凭证密钥", notes = "手机、邮件的凭证类型对应的sk为验证码", required = true)
    private String sk;

    @ApiModelProperty(value = "账号凭证过期时间")
    private Date validTime;

    @ApiModelProperty(value = "账号凭证有效次数")
    private Integer validTimes;

}
