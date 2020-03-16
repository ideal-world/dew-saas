package idealworld.dew.saas.service.ident.dto.account;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@ApiModel("修改账号凭证请求")
public class ModifyAccountCertReq implements Serializable {

    @ApiModelProperty(value = "账号凭证过期时间")
    private Date validTime;

}
