package idealworld.dew.saas.service.ident.dto.app;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@ApiModel("应用凭证信息")
public class AppCertInfoResp implements Serializable {

    @Tolerate
    public AppCertInfoResp() {
    }

    @ApiModelProperty(value = "应用凭证Id", required = true)
    private Long id;

    @ApiModelProperty(value = "应用凭证用途", required = true)
    private String note;

    @ApiModelProperty(value = "应用凭证名称", required = true)
    private String ak;

    @ApiModelProperty(value = "应用凭证密钥", required = true)
    private String sk;

    @ApiModelProperty(value = "应用凭证过期时间")
    private Date validTime;

    @ApiModelProperty(value = "创建者", required = true)
    protected String createUserName;

    @ApiModelProperty(value = "最后一次更新者", required = true)
    protected String updateUserName;

    @ApiModelProperty(value = "创建时间", required = true)
    protected Date createTime;

    @ApiModelProperty(value = "最后一次更新时间", required = true)
    protected Date updateTime;

}
