package idealworld.dew.saas.service.ident.dto.app;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@ApiModel("修改应用凭证请求")
public class ModifyAppCertReq implements Serializable {

    @ApiModelProperty(value = "应用凭证用途", required = true)
    private String note;

    @ApiModelProperty(value = "应用凭证过期时间")
    private Date validTime;

}
