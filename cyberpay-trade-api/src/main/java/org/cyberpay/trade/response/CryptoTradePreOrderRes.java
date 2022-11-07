package org.cyberpay.trade.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "加密币预交易订单接口响应参数")
public class CryptoTradePreOrderRes extends BaseResponse implements Serializable {

    @ApiModelProperty(value = "交易系统流水号")
    private String tradeFlowNo;

    @ApiModelProperty(value = "系统收银台访问地址")
    private String link;

}
