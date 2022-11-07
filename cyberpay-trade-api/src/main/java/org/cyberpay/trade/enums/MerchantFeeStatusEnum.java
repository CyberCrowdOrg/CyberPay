package org.cyberpay.trade.enums;

public enum MerchantFeeStatusEnum {

    enable("1","启用"),
    abolish("2","费止"),

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

    MerchantFeeStatusEnum(String code, String name){
        this.code = code;
        this.name = name;
    }
}
