package org.cyberpay.crypto.enums;

public enum BitcoinMethodEnum {

    GET_BLCOK("getblock","获取当前区块数据"),
    GET_BALANCE("getbalance","查询余额"),
    SEND_TO_ADDRESS("sendtoaddress","发送交易到指定地址"),
    GET_RAW_TRANSACTION("getrawtransaction","获取交易信息"),
    SEND_RAW_TRANSACTION("sendrawtransaction","发送已签名交易(广播交易)"),



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

    BitcoinMethodEnum(String code,String name){
        this.code = code;
        this.name = name;
    }
}
