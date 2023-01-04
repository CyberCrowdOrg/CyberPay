package org.cyberpay.crypto.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value = "收款币种信息")
public class ReceiveCryptoCoinDto implements Serializable {

    @ApiModelProperty(value = "收款币种")
    private String receiveCoin;

    @ApiModelProperty(value = "收款网络diam")
    private String receiveNetworkCode;

    @ApiModelProperty(value = "收款网络名称")
    private String receiveNetworkName;

    @ApiModelProperty(value = "币种图标")
    private String coinIconImage;

    @ApiModelProperty(value = "网络图标")
    private String networkIconImage;
}
