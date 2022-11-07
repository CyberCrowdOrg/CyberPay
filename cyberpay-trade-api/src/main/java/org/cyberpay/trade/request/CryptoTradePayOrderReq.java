package org.cyberpay.trade.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel(value = "加密币交易支付订单接口请求参数")
public class CryptoTradePayOrderReq extends BaseRequest implements Serializable {

    @ApiModelProperty(value = "订单号",required = true)
    private String orderNo;
    @ApiModelProperty(value = "订单币种",required = true)
    private String orderCoin;
    @ApiModelProperty(value = "订单金额",required = true)
    private BigDecimal orderAmount;
    @ApiModelProperty(value = "订单业务类型,",required = true)
    private String bizOrderType;

    @ApiModelProperty(value = "收款币种",required = true)
    private String receiveCoin;
    @ApiModelProperty(value = "收款网络代码",required = true)
    private String receiveNetworkCode;

}
