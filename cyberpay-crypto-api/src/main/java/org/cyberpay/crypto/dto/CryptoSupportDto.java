package org.cyberpay.crypto.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel(description="加密币支持对象")
public class CryptoSupportDto implements Serializable {

    @ApiModelProperty(value = "加密币ID")
    private String cryptoId;
    @ApiModelProperty(value = "加密币代码")
    private String symbol;
    @ApiModelProperty(value = "加密币全称")
    private String fullName;
    @ApiModelProperty(value = "加密币图片")
    private String coinImage;
    @ApiModelProperty(value = "加密币icon图片,移动端使用")
    private String coinIconImage;

    @ApiModelProperty(value = "加密币支持网络列表")
    private List<SupportNetworkDto> supportNetworks;

}
