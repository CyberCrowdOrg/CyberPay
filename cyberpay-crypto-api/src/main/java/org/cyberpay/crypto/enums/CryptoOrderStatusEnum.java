package org.cyberpay.crypto.enums;

public enum CryptoOrderStatusEnum {

    init("init","初始状态"),
    unpaid("unpaid","未付款"),
    unconfirmed("unconfirmed","未确认"),
    success("success","付款成功"),
    msuccess("msuccess","多付成功"),
    lsuccess("lsuccess","少付成功"),
    fail("fail","付款失败"),
    close("close","超时关闭"),

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

    CryptoOrderStatusEnum(String code, String name){
        this.code = code;
        this.name = name;
    }
}
