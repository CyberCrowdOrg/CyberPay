package org.cyberpay.gateway.service.feign;

import org.cyberpay.trade.request.CryptoTradeOrderPaySubmitReq;
import org.cyberpay.trade.request.CryptoTradePayOrderReq;
import org.cyberpay.trade.request.CryptoTradePreTradeOrderReq;
import org.cyberpay.trade.response.CryptoTradeOrderPaySubmitRes;
import org.cyberpay.trade.response.CryptoTradePayOrderRes;
import org.cyberpay.trade.response.CryptoTradePreOrderRes;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(value = "crypto-trade-web")
public interface CryptoTradeOrderFeign {

    @RequestMapping(value = "/crypto-trade-web/crypto-trade/v1/pre-order")
    CryptoTradePreOrderRes cryptoTradePreOrder(CryptoTradePreTradeOrderReq cryptoTradePreTradeOrderReq);

    @RequestMapping(value = "/crypto-trade-web/crypto-trade/v1/pay-submit")
    CryptoTradeOrderPaySubmitRes cryptoTradeOrderPaySubmit(CryptoTradeOrderPaySubmitReq cryptoTradeOrderPaySubmitReq);

    @RequestMapping(value = "/crypto-trade-web/crypto-trade/v1/pay-order")
    CryptoTradePayOrderRes cryptoTradePayOrder(CryptoTradePayOrderReq cryptoTradePayOrderReq);
}
