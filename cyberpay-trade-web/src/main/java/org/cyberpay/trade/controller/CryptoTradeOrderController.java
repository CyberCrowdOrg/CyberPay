package org.cyberpay.trade.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.cyberpay.trade.request.CryptoTradeOrderPaySubmitReq;
import org.cyberpay.trade.request.CryptoTradePayOrderReq;
import org.cyberpay.trade.request.CryptoTradePreTradeOrderReq;
import org.cyberpay.trade.response.CryptoTradeOrderPaySubmitRes;
import org.cyberpay.trade.response.CryptoTradePayOrderRes;
import org.cyberpay.trade.response.CryptoTradePreOrderRes;
import org.cyberpay.trade.service.CryptoTradeOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CryptoTradeOrderController {

    private Logger logger = LoggerFactory.getLogger(CryptoTradeOrderController.class);

    @Autowired
    CryptoTradeOrderService cryptoTradeOrderService;


    @RequestMapping(value = "/crypto-trade/v1/pre-order")
    public CryptoTradePreOrderRes cryptoTradePreOrder(@RequestBody CryptoTradePreTradeOrderReq cryptoTradePreTradeOrderReq){
        return cryptoTradeOrderService.tradePreOrder(cryptoTradePreTradeOrderReq);
    }

    @RequestMapping(value = "/crypto-trade/v1/pay-submit")
    public CryptoTradeOrderPaySubmitRes cryptoTradeOrderPaySubmit(@RequestBody CryptoTradeOrderPaySubmitReq cryptoTradeOrderPaySubmitReq){
        return cryptoTradeOrderService.tradeOrderPaySubmit(cryptoTradeOrderPaySubmitReq);
    }

    @RequestMapping(value = "/crypto-trade/v1/pay-order")
    public CryptoTradePayOrderRes cryptoTradePayOrder(@RequestBody CryptoTradePayOrderReq cryptoTradePayOrderReq){
        return cryptoTradeOrderService.tradePayOrder(cryptoTradePayOrderReq);
    }


}
