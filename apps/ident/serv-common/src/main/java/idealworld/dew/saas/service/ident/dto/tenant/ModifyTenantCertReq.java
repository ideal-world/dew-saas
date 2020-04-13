package idealworld.dew.saas.service.ident.dto.tenant;

import idealworld.dew.saas.service.ident.enumeration.CommonStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.io.Serializable;

@Data
@Builder
@ApiModel("修改租户凭证请求")
public class ModifyTenantCertReq implements Serializable {

    @Tolerate
    public ModifyTenantCertReq() {
    }

    @ApiModelProperty(value = "租户凭证校验正则规则说明")
    private String validRuleNote;

    @ApiModelProperty(value = "租户凭证校验正则规则")
    private String validRule;

    @ApiModelProperty(value = "租户凭证过期时间")
    private Long validTimeSec;

    @ApiModelProperty(value = "OAuth下的应用密钥ID或是AppId")
    private String oauthAk;

    @ApiModelProperty(value = "OAuth下的应用密钥")
    private String oauthSk;

    @ApiModelProperty(value = "租户凭证状态", required = true)
    private CommonStatus status;

}
