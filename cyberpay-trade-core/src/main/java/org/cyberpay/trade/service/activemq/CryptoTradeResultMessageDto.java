package org.cyberpay.trade.service.activemq;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class CryptoTradeResultMessageDto implements Serializable {

    private String tradeFlowNo;
    private String status;
    //支付成功时不为空
    private BigDecimal receiptAmount;

}
