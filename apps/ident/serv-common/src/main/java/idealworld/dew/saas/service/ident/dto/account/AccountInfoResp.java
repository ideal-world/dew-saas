package idealworld.dew.saas.service.ident.dto.account;

import idealworld.dew.saas.service.ident.enumeration.AccountStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@ApiModel("账号信息")
public class AccountInfoResp implements Serializable {

    @Tolerate
    public AccountInfoResp() {
    }

    @ApiModelProperty(value = "账号Id", required = true)
    private Long id;

    @ApiModelProperty(value = "账号名称", required = true)
    private String name;

    @ApiModelProperty(value = "账号头像")
    private String avatar;

    @ApiModelProperty(value = "账号扩展信息（Json格式）")
    private String parameters;

    @ApiModelProperty(value = "账号状态", required = true)
    private AccountStatus status;

    @ApiModelProperty(value = "创建者", required = true)
    protected String createUserName;

    @ApiModelProperty(value = "最后一次更新者", required = true)
    protected String updateUserName;

    @ApiModelProperty(value = "创建时间", required = true)
    protected Date createTime;

    @ApiModelProperty(value = "最后一次更新时间", required = true)
    protected Date updateTime;
}
