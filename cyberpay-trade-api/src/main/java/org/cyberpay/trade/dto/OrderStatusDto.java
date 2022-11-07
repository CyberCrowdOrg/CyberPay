package org.cyberpay.trade.dto;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value = "订单状态信息")
public class OrderStatusDto implements Serializable {

    @ApiModelProperty(value = "商户订单号",required = true)
    private String orderNo;
    @ApiModelProperty(value = "交易系统流水号",required = true)
    private String tradeFlowNo;
    @ApiModelProperty(value = "交易状态,0 初始状态,预订单  4 订单关闭 5 待付款 6 付款成功 7 付款失败 8 多付成功 9 少付成功",required = true)
    private String tradeStatus;

    @ApiModelProperty(value = "收据金额,即实际收款金额,收款成功时字段不为空")
    private String receiptAmount;
    @ApiModelProperty(value = "收款差额,多付成功或者少付成功时字段不为空")
    private String diffReceiveAmount;
    @ApiModelProperty(value = "交易成功时间")
    private String successTime;

}
