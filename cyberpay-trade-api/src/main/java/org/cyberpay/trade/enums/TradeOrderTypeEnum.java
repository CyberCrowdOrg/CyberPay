package org.cyberpay.trade.enums;

public enum TradeOrderTypeEnum {

    payment("payment","支付"),
    withdrawal("withdrawal","提现"),
    refund("refund","退款"),
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

    TradeOrderTypeEnum(String code,String name){
        this.code = code;
        this.name = name;
    }
}
