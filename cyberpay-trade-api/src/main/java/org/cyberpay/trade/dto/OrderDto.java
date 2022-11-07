package org.cyberpay.trade.dto;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

public class OrderDto implements Serializable {

    @ApiModelProperty(value = "订单号",required = true)
    private String orderNo;
    @ApiModelProperty(value = "类型, crypto 加密币  fiat 法币",required = true)
    private String type;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
