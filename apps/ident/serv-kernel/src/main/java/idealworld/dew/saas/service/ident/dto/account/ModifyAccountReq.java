package idealworld.dew.saas.service.ident.dto.account;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
@ApiModel("修改账号请求")
public class ModifyAccountReq implements Serializable {

    @ApiModelProperty(value = "账号名称")
    private String name;

    @ApiModelProperty(value = "账号头像")
    private String avatar;

    @ApiModelProperty(value = "账号扩展信息（Json格式）")
    private String parameters;

}
