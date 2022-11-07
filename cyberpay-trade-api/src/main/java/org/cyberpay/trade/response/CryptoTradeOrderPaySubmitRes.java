package org.cyberpay.trade.response;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value = "加密币订单支付提交接口响应参数")
public class CryptoTradeOrderPaySubmitRes extends BaseResponse implements Serializable {
}
