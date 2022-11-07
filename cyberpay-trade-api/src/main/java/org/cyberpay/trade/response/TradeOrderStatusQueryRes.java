package org.cyberpay.trade.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.cyberpay.trade.dto.OrderStatusDto;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel(value = "交易订单状态查询接口响应参数")
public class TradeOrderStatusQueryRes extends BaseResponse implements Serializable {

    @ApiModelProperty(value = "订单状态列表")
    private List<OrderStatusDto> orders;

}
