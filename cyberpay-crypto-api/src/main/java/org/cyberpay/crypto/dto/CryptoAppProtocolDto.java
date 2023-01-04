package org.cyberpay.crypto.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value = "加密币应用协议适配")
public class CryptoAppProtocolDto implements Serializable {

    @ApiModelProperty(value = "加密币应用名称")
    private String appName;

    @ApiModelProperty(value = "加密币应用扫码协议")
    private String scanProtocol;

    @ApiModelProperty(value = "加密币应用调起协议")
    private String schemeProtocol;

    @ApiModelProperty(value = "加密币应用插件调起协议")
    private String pluginProtocol;
}
