/*
 * Copyright 2020. the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package idealworld.dew.saas.service.ident.dto.post;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.io.Serializable;

/**
 * 岗位信息.
 *
 * @author gudaoxuri
 */
@Data
@Builder
@ApiModel("岗位信息")
public class PostInfoResp implements Serializable {

    @ApiModelProperty(value = "岗位Id", required = true)
    private Long id;
    @ApiModelProperty(value = "关联机构编码")
    private String relOrganizationCode;
    @ApiModelProperty(value = "关联职位编码", required = true)
    private String relPositionCode;
    @ApiModelProperty(value = "显示排序，asc", required = true)
    private Integer sort;
    @ApiModelProperty(value = "岗位所属应用", required = true)
    private Long relAppId;

    /**
     * Instantiates a new Post info.
     */
    @Tolerate
    public PostInfoResp() {
    }

}
