package org.cyberpay.crypto.enums;

import java.math.BigDecimal;

public enum CoinDecimalEnum {

    BTC(100000000L,8),
    ETH(1000000000000000000L,18),
    LTC(100000000L,8),
    BCH(100000000L,8),
    BNB(100000000L,8),

    ;

    private Long decimal;
    private int uint;


    CoinDecimalEnum(Long decimal,int uint){
        this.decimal = decimal;
        this.uint = uint;
    }

    public static BigDecimal converToBigDecimal(Long var,CoinDecimalEnum coinDecimalEnum){
        return new BigDecimal(var).divide(new BigDecimal(coinDecimalEnum.decimal));
    }

    public static Long converToLong(BigDecimal var,CoinDecimalEnum coinDecimalEnum){
        BigDecimal to = var.multiply(new BigDecimal(coinDecimalEnum.decimal));
        return to.longValue();
    }

}
