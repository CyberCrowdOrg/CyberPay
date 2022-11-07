package org.cyberpay.trade.enums;

public enum TradeReturnCodeEnum {

    FAIL("SYSTEM_ERROR","internal system error"),
    SUCCESS("SUCCESS","success"),

    PARAMETER_NULL_ERROR("PARAMETER_NULL_ERROR","参数空错误 [ {0} ]"),
    ORDER_AMOUNT_ERROR("ORDER_AMOUNT_ERROR","订单金额错误"),

    ORDER_NOT_FOUND_ERROR("ORDER_NOT_FOUND_ERROR","订单未找到"),
    ORDER_ALREADY_EXISTS_ERROR("ORDER_ALREADY_EXISTS_ERROR","订单已存在"),
    TRADE_COIN_NOT_SUPPORT_ERROR("TRADE_COIN_NOT_SUPPORT_ERROR","不支持的交易币种"),
    ORDER_PAYMENT_OR_CLOSE_ERROR("ORDER_PAYMENT_OR_CLOSE_ERROR","订单已支付或者已关闭"),
    ORDER_PAYING_ERROR("ORDER_PAYING_ERROR","订单付款中"),
    TRADE_NOT_SUPPORT_ERROR("TRADE_NOT_SUPPORT_ERROR","暂不支持交易"),
    ORDER_NOT_PAY_REFUND_ERROR("ORDER_NOT_PAY_REFUND_ERROR","退款失败,原订单未付款成功"),
    ORDER_REPEAT_REFUND_ERROR("ORDER_REPEAT_REFUND_ERROR","重复退款"),
    ORDER_ORG_NOT_FOUND_ERROR("ORDER_ORG_NOT_FOUND_ERROR","退款原订单未找到"),

    INVALID_ORDER_LINK_ERROR("INVALID_LINK_ERROR","链接已失效"),
    INVALID_ACCESS_KEY_ERROR("INVALID_ACCESS_KEY_ERROR","无效的访问密匙"),

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

    TradeReturnCodeEnum(String code,String name){
        this.code = code;
        this.name = name;
    }
}
