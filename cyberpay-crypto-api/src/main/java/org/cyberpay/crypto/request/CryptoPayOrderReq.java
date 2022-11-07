package org.cyberpay.crypto.request;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 加密币支付订单请求参数
 */
@Data
public class CryptoPayOrderReq implements Serializable {

    //订单号,交易模块的trade flow no
    private String orderNo;
    //业务订单类型
    private String bizOrderType;
    //订单币种
    private String orderCoin;
    //订单金额
    private BigDecimal orderAmount;

    //收款币种
    private String receiveCoin;
    //收款网络代码
    private String receiveNetworkCode;

    //汇率浮动率
    private BigDecimal floatingRate;

}
