package org.cyberpay.crypto.enums;

public enum ReturnCodeEnum {

    FAIL("SYSTEM_ERROR","internal system error"),
    SUCCESS("SUCCESS","success"),


    //加密币订单错误
    CRYPTO_ORDER_NOT_FOUND_ERROR("ORDER_NOT_FOUND_ERROR","订单未找到"),
    CRYPTO_ORDER_ALREADY_EXISTS_ERROR("ORDER_ALREADY_EXISTS_ERROR","订单已存在"),
    TRADE_COIN_NOT_SUPPORT_ERROR("TRADE_COIN_NOT_SUPPORT_ERROR","不支持的交易币种"),
    CRYPTO_ORDER_PAYMENT_OR_CLOSE_ERROR("ORDER_PAYMENT_OR_CLOSE_ERROR","订单已支付或者已关闭"),
    CRYPTO_ORDER_PAYING_ERROR("ORDER_PAYING_ERROR","订单付款中"),
    TRADE_NOT_SUPPORT_ERROR("TRADE_NOT_SUPPORT_ERROR","暂不支持交易"),
    CRYPTO_ORDER_NOT_PAY_REFUND_ERROR("ORDER_NOT_PAY_REFUND_ERROR","退款失败,原订单未付款成功"),
    CRYPTO_ORDER_REPEAT_REFUND_ERROR("ORDER_REPEAT_REFUND_ERROR","重复退款"),
    CRYPTO_ORDER_ORG_NOT_FOUND_ERROR("ORDER_ORG_NOT_FOUND_ERROR","退款原订单未找到"),

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

    ReturnCodeEnum(String code,String name){
        this.code = code;
        this.name = name;
    }
}
