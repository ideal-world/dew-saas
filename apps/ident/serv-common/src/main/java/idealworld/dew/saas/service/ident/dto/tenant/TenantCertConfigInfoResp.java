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
@ApiModel("租户凭证配置信息")
public class TenantCertConfigInfoResp implements Serializable {

    @Tolerate
    public TenantCertConfigInfoResp() {
    }

    @ApiModelProperty(value = "租户凭证Id", required = true)
    private Long id;

    @ApiModelProperty(value = "租户凭证类型名称", required = true)
    private AccountCertKind kind;

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

    @ApiModelProperty(value = "创建者", required = true)
    protected String createUserName;

    @ApiModelProperty(value = "最后一次更新者", required = true)
    protected String updateUserName;

    @ApiModelProperty(value = "创建时间", required = true)
    protected Date createTime;

    @ApiModelProperty(value = "最后一次更新时间", required = true)
    protected Date updateTime;

}
