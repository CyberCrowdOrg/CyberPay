package org.cyberpay.crypto.request;

import java.io.Serializable;

public class LatestCryptoRateQueryReq implements Serializable {

    private String fromSymbol;
    private String toSymbol;

    public String getFromSymbol() {
        return fromSymbol;
    }

    public void setFromSymbol(String fromSymbol) {
        this.fromSymbol = fromSymbol;
    }

    public String getToSymbol() {
        return toSymbol;
    }

    public void setToSymbol(String toSymbol) {
        this.toSymbol = toSymbol;
    }
}
