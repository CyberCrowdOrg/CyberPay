package org.cyberpay.crypto.request;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class CryptoPreOrderReq implements Serializable {

    //订单号,交易模块的trade flow no
    private String orderNo;
    //业务订单类型
    private String bizOrderType;
    //订单币种
    private String orderCoin;
    //订单金额
    private BigDecimal orderAmount;

}
