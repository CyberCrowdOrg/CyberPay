package org.cyberpay.crypto.enums;

public enum CoinExchangeRateTypeEnum {

    FTOF("F-TO-F","法币兑法币"),
    CTOF("C-TO-F","加密币兑法币"),
    CTOC("C-TO-C","加密币兑加密币"),
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

    CoinExchangeRateTypeEnum(String code,String name){
        this.code = code;
        this.name = name;
    }
}
