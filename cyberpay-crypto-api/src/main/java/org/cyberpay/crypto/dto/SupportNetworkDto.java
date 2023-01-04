package org.cyberpay.crypto.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value = "加密币支持网络")
public class SupportNetworkDto implements Serializable {

    @ApiModelProperty(value = "加密币网络名称")
    private String networkName;
    @ApiModelProperty(value = "加密币网络代码")
    private String networkCode;
    @ApiModelProperty(value = "加密币网络 chain 一层网络 layer2 二层网络")
    private String networkType;
    @ApiModelProperty(value = "加密币网络Icon")
    private String networkIconImage;
    @ApiModelProperty(value = "加密币网络状态,1 可用 0 暂时不可用")
    private String status;

}
