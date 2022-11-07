package org.cyberpay.crypto.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 加密币退款订单请求参数
 */
@Data
public class CryptoRefundOrderReq implements Serializable {

    //原订单号
    private String orgOrderNo;
    //退款订单号
    private String refundOrderNo;

    //退款地址
    private String refundAddress;

}
