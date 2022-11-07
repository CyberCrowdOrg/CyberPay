package org.cyberpay.crypto.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(description = "加密币钱包协议信息")
public class CryptoWalletProtocolDto implements Serializable {

    @ApiModelProperty(value = "应用名称")
    private String appName;

    @ApiModelProperty(value = "加密币代码")
    private String cryptoSymbol;

    @ApiModelProperty(value = "扫码协议")
    private String scanProtocol;

    @ApiModelProperty(value = "应用调起协议")
    private String schemeProtocol;

    @ApiModelProperty(value = "插件协议")
    private String pluginProtocol;

    private String extend;

    private String extend2;
}
