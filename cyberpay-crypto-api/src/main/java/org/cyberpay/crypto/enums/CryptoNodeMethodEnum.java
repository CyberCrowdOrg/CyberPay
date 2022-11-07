package org.cyberpay.crypto.enums;

public enum CryptoNodeMethodEnum {

    generateAddress("generateAddress","生成地址"),
    sendTransaction("sendTransaction","发送交易"),
    confirmTransaction("confirmTransaction","确认交易"),
    queryBalance("queryBalance","查询余额"),

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

    CryptoNodeMethodEnum(String code, String name){
        this.code = code;
        this.name = name;
    }
}
