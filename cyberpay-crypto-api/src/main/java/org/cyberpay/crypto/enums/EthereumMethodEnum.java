package org.cyberpay.crypto.enums;

public enum EthereumMethodEnum {

    GET_BLOCKNUMBER("eth_blockNumber","查询最新区块"),
    GET_CALL("eth_call","合约交互"),
    GET_BALANCE("eth_getBalance","查询地址余额"),
    GET_GAS_PRICE("eth_gasPrice","获取Gas价格"),
    GET_CHAINID("eth_chainId","获取chainId"),
    GET_TRANSACTION_COUNT("eth_getTransactionCount","获取nonce"),
    GET_TRANSACTION("eth_getTransactionByHash","获取交易详情数据"),
    GET_TRANSACTION_RECEIPT("eth_getTransactionReceipt","获取交易收据"),
    SEND_RAW_TRANSACTION("eth_sendRawTransaction","发送已签名交易(广播交易)")

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

    EthereumMethodEnum(String code,String name){
        this.code = code;
        this.name = name;
    }
}
