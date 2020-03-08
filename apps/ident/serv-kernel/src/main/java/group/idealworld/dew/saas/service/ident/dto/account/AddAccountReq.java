package group.idealworld.dew.saas.service.ident.dto.account;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
@ApiModel("添加账号请求")
public class AddAccountReq implements Serializable {

    @ApiModelProperty(value = "账号名称", required = true)
    private String name;

    @ApiModelProperty(value = "账号头像")
    private String avatar;

    @ApiModelProperty(value = "账号扩展信息（Json格式）")
    private String parameters;

    @ApiModelProperty(value = "账号凭证", required = true)
    private AddAccountCertReq certReq;

    @ApiModelProperty(value = "账号岗位", required = true)
    private AddAccountPostReq postReq;

}
