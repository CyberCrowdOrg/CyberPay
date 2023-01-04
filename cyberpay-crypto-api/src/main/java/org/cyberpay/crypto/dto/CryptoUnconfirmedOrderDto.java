package org.cyberpay.crypto.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class CryptoUnconfirmedOrderDto implements Serializable {

    private String bizFlowNo;

    private String bizOrderNo;

    private String receiveCoin;

    private String receiveNetworkCode;

    private BigDecimal receiveAmount;

    private BigDecimal receiptAmount;

    private String status;

    private String transHash;

    private Long confirm;
}
