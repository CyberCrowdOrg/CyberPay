package org.cyberpay.crypto.response;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class LatestCryptoRateQueryRes extends BaseResponse implements Serializable {

    private BigDecimal realRete;
    private String fromSymbol;
    private String toSymbol;
}
