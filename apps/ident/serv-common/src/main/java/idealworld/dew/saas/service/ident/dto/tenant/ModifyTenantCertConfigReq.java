package idealworld.dew.saas.service.ident.dto.tenant;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.io.Serializable;

@Data
@Builder
@ApiModel("修改租户凭证配置请求")
public class ModifyTenantCertConfigReq implements Serializable {

    @Tolerate
    public ModifyTenantCertConfigReq() {
    }

    @ApiModelProperty(value = "租户凭证校验正则规则说明")
    private String validRuleNote;

    @ApiModelProperty(value = "租户凭证校验正则规则")
    private String validRule;

    @ApiModelProperty(value = "租户凭证过期时间")
    private Long validTimeSec;

}