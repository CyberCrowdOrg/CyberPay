package org.cyberpay.trade.enums;

public enum MerchantFeeTypeEnum {

    trade("1","交易"),
    withdraw("2","提现"),

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

    MerchantFeeTypeEnum(String code, String name){
        this.code = code;
        this.name = name;
    }
}
