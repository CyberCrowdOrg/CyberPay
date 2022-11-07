package org.cyberpay.trade.enums;

public enum CoinTypeEnum {

    crypto("crypto","加密币"),
    fiat("fiat","法币"),

    ;
    private String code;
    private String name;

    CoinTypeEnum(String code,String name){
        this.code = code;
        this.name = name;
    }

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
}
