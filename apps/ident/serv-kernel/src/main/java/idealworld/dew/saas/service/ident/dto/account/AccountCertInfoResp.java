package idealworld.dew.saas.service.ident.dto.account;

import idealworld.dew.saas.service.ident.domain.AccountCert;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@ApiModel("账号凭证信息")
public class AccountCertInfoResp implements Serializable {

    @Tolerate
    public AccountCertInfoResp() {
    }

    @ApiModelProperty(value = "账号凭证Id", required = true)
    private Long id;

    @ApiModelProperty(value = "账号凭证类型名称", required = true)
    private AccountCert.Kind kind;

    @ApiModelProperty(value = "账号凭证名称", required = true)
    private String ak;

    @ApiModelProperty(value = "账号凭证密钥", notes = "手机、邮件的凭证类型对应的sk为验证码", required = true)
    private String sk;

    @ApiModelProperty(value = "账号凭证过期时间")
    private Date validTime;

    @ApiModelProperty(value = "账号凭证有效次数")
    private Long validTimes;

    @ApiModelProperty(value = "是否删除", required = true)
    private boolean delFlag;

    @ApiModelProperty(value = "创建者", required = true)
    protected String createUserName;

    @ApiModelProperty(value = "最后一次更新者", required = true)
    protected String updateUserName;

    @ApiModelProperty(value = "创建时间", required = true)
    protected Date createTime;

    @ApiModelProperty(value = "最后一次更新时间", required = true)
    protected Date updateTime;

}
