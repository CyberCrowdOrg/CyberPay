package org.cyberpay.crypto.request;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class CryptoOrderSubmitPayReq implements Serializable {

    private String orderNo;
    private String receiveCoin;
    private String receiveNetworkCode;
    private BigDecimal floatingRate;
}
