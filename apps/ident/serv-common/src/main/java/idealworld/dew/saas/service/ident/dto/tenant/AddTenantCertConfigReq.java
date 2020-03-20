package idealworld.dew.saas.service.ident.dto.tenant;

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
@ApiModel("添加租户凭证配置请求")
public class AddTenantCertConfigReq implements Serializable {

    @Tolerate
    public AddTenantCertConfigReq() {
    }

    @ApiModelProperty(value = "租户凭证类型名称", required = true)
    private AccountCertKind kind;

    @ApiModelProperty(value = "租户凭证校验正则规则说明")
    private String validRuleNote;

    @ApiModelProperty(value = "租户凭证校验正则规则")
    private String validRule;

    @ApiModelProperty(value = "租户凭证过期时间")
    private Long validTimeSec;

}
