package org.cyberpay.crypto.enums;

public enum CoinExchangeRateSourceEnum {

    APILAYER("apilayer","ApiLayer"),
    COINMARKERCAP("coinmarketcap","CoinMarketCap"),
    BINANCE("binance","币安"),
    ;

    private String code;
    private String name;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    CoinExchangeRateSourceEnum(String code,String name){
        this.code = code;
        this.name = name;
    }
}
