package idealworld.dew.saas.service.ident.dto.tenant;

import idealworld.dew.saas.service.ident.domain.AccountCert;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
@ApiModel("租户注册请求")
public class RegisterTenantReq implements Serializable {

    @ApiModelProperty(value = "租户名称", required = true)
    private String tenantName;

    @ApiModelProperty(value = "账户名称", required = true)
    private String accountName;

    @ApiModelProperty(value = "凭证类型", required = true)
    private AccountCert.Kind certKind;

    @ApiModelProperty(value = "凭证名", required = true)
    private String ak;

    @ApiModelProperty(value = "凭证密钥", required = true)
    private String sk;

}