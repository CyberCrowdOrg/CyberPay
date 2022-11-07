package org.cyberpay.trade.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.cyberpay.trade.dto.OrderDto;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel(value = "交易订单状态查询接口请求参数")
public class TradeOrderStatusQueryReq extends BaseRequest implements Serializable {

    @ApiModelProperty(value = "商户订单号,支持批量查询,最多200笔",required = true)
    private List<OrderDto> orders;

}
