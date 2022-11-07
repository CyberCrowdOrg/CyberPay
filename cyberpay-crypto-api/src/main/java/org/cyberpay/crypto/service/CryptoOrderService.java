package org.cyberpay.crypto.service;

import org.cyberpay.crypto.request.CryptoPayOrderReq;
import org.cyberpay.crypto.request.CryptoPreOrderReq;
import org.cyberpay.crypto.request.CryptoRefundOrderReq;
import org.cyberpay.crypto.request.CryptoOrderSubmitPayReq;
import org.cyberpay.crypto.response.CryptoPayOrderRes;
import org.cyberpay.crypto.response.CryptoPreOrderRes;
import org.cyberpay.crypto.response.CryptoRefundOrderRes;
import org.cyberpay.crypto.response.CryptoOrderSubmitPayRes;

public interface CryptoOrderService {

    //预订单接口
    CryptoPreOrderRes preOrder(CryptoPreOrderReq cryptoPreOrderReq);

    //支付订单接口
    CryptoPayOrderRes payOrder(CryptoPayOrderReq cryptoPayOrderReq);

    //退款订单接口
    CryptoRefundOrderRes refundOrder(CryptoRefundOrderReq cryptoRefundOrderReq);

    //提交付款订单
    CryptoOrderSubmitPayRes submitPayOrder(CryptoOrderSubmitPayReq cryptoOrderSubmitPayReq);

    /**
     * 订单交易确认
     * @param coin
     * @param networkCode
     */
    void confirmOrder(String coin,String networkCode);

    /**
     * 订单关闭
     * @param timeout
     */
    void closeOrder(String coin,String networkCode,long timeout);
}
