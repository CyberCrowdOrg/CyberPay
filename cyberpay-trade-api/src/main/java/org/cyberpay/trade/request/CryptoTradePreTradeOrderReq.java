package org.cyberpay.trade.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "加密币交易预订单接口请求参数")
public class CryptoTradePreTradeOrderReq extends BaseRequest implements Serializable {

    @ApiModelProperty(value = "订单号",required = true)
    private String orderNo;
    @ApiModelProperty(value = "订单币种",required = true)
    private String orderCoin;
    @ApiModelProperty(value = "订单金额",required = true)
    private BigDecimal orderAmount;

    @ApiModelProperty(value = "订单业务类型:payment 支付,withdrawal 提现,refund 退款",required = true)
    private String bizOrderType;

}
