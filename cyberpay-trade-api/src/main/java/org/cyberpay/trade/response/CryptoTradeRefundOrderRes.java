package org.cyberpay.trade.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value = "加密币交易退款订单接口响应参数")
public class CryptoTradeRefundOrderRes extends BaseResponse implements Serializable {

    @ApiModelProperty(value = "交易系统流水号")
    private String tradeFlowNo;
    @ApiModelProperty(value = "交易状态,0 初始状态,预订单  4 订单关闭 5 待付款 6 付款成功 7 付款失败 8 多付成功 9 少付成功")
    private String tradeStatus;

}
