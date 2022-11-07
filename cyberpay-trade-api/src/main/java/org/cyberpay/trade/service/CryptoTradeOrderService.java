package org.cyberpay.trade.service;

import org.cyberpay.trade.request.CryptoTradeOrderPaySubmitReq;
import org.cyberpay.trade.request.CryptoTradePayOrderReq;
import org.cyberpay.trade.request.CryptoTradePreTradeOrderReq;
import org.cyberpay.trade.request.CryptoTradeRefundOrderReq;
import org.cyberpay.trade.response.CryptoTradeOrderPaySubmitRes;
import org.cyberpay.trade.response.CryptoTradePayOrderRes;
import org.cyberpay.trade.response.CryptoTradePreOrderRes;
import org.cyberpay.trade.response.CryptoTradeRefundOrderRes;

public interface CryptoTradeOrderService {

    /**
     * 加密币交易预订单
     * @param cryptoTradePreTradeOrderReq
     * @return
     */
    CryptoTradePreOrderRes tradePreOrder(CryptoTradePreTradeOrderReq cryptoTradePreTradeOrderReq);

    /**
     * 加密币交易订单支付提交
     * @param cryptoTradeOrderPaySubmitReq
     * @return
     */
    CryptoTradeOrderPaySubmitRes tradeOrderPaySubmit(CryptoTradeOrderPaySubmitReq cryptoTradeOrderPaySubmitReq);

    /**
     * 加密币交易支付订单
     * @param cryptoTradePayOrderReq
     * @return
     */
    CryptoTradePayOrderRes tradePayOrder(CryptoTradePayOrderReq cryptoTradePayOrderReq);

    /**
     * 交易退款订单
     * @param cryptoTradeRefundOrderReq
     * @return
     */
    CryptoTradeRefundOrderRes tradeRefundOrder(CryptoTradeRefundOrderReq cryptoTradeRefundOrderReq);

}
