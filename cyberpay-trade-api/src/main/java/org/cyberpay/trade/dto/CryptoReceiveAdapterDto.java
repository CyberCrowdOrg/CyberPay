package org.cyberpay.trade.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value = "加密币收款地址适配")
public class CryptoReceiveAdapterDto implements Serializable {

    @ApiModelProperty(value = "应用名称",required = true)
    private String appName;
    @ApiModelProperty(value = "扫码协议",required = true)
    private String scanProtocol;
    @ApiModelProperty(value = "应用调起协议")
    private String schemeProtocol;

}
