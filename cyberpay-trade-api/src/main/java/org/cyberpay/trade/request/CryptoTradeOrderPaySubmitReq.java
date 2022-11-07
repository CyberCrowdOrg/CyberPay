package org.cyberpay.trade.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "加密币支付提交请求参数")
public class CryptoTradeOrderPaySubmitReq implements Serializable {

    @ApiModelProperty(value = "收款币种",required = true)
    private String receiveCoin;
    @ApiModelProperty(value = "收款币种网络代码",required = true)
    private String receiveNetworkCode;
    @ApiModelProperty(value = "访问密匙,access_key")
    private String accessKey;

}
