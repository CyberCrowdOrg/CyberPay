package org.cyberpay.trade.enums;

public enum TradeOrderStatusEnum{

    init("0","初始状态,预订单"),
    pending("3","待付款"),
    close("4","订单关闭"),
    unconfirmed("5","未确认"),
    success("6","付款成功"),
    fail("7","付款失败"),
    msuccess("8","多付成功"),
    lsuccess("9","少付成功"),
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

    TradeOrderStatusEnum(String code,String name){
        this.code = code;
        this.name = name;
    }
}
