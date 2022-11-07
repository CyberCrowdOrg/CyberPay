package org.cyberpay.crypto.controller;

import com.alibaba.fastjson2.JSON;
import org.cyberpay.crypto.request.CryptoOrderSubmitPayReq;
import org.cyberpay.crypto.request.CryptoPayOrderReq;
import org.cyberpay.crypto.request.CryptoPreOrderReq;
import org.cyberpay.crypto.request.CryptoRefundOrderReq;
import org.cyberpay.crypto.response.CryptoPayOrderRes;
import org.cyberpay.crypto.response.CryptoPreOrderRes;
import org.cyberpay.crypto.response.CryptoOrderSubmitPayRes;
import org.cyberpay.crypto.response.CryptoRefundOrderRes;
import org.cyberpay.crypto.service.CryptoOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;


@RestController
public class CryptoOrderController {

    private Logger logger = LoggerFactory.getLogger(CryptoOrderController.class);

    @Autowired
    CryptoOrderService cryptoOrderService;

    /**
     * 加密币预订单
     * @param cryptoPreOrderReq
     * @return
     */
    @RequestMapping(value = "/crypto/v1/pre-order")
    public CryptoPreOrderRes preOrder(CryptoPreOrderReq cryptoPreOrderReq){
        logger.info("加密币预订单创建接口,请求入参:{}", JSON.toJSONString(cryptoPreOrderReq));
        return cryptoOrderService.preOrder(cryptoPreOrderReq);
    }

    /**
     * 加密币订单提交支付
     * @param cryptoOrderSubmitPayReq
     * @return
     */
    @RequestMapping(value = "/crypto/v1/pay-submit")
    public CryptoOrderSubmitPayRes orderPaySubmit(CryptoOrderSubmitPayReq cryptoOrderSubmitPayReq){
        logger.info("加密币订单提交支付接口,请求入参:{}", JSON.toJSONString(cryptoOrderSubmitPayReq));
        return cryptoOrderService.submitPayOrder(cryptoOrderSubmitPayReq);
    }

    /**
     * 加密币支付订单
     * @param cryptoPayOrderReq
     * @return
     */
    @RequestMapping(value = "/crypto/v1/pay-order")
    public CryptoPayOrderRes createCryptoPayOrder(CryptoPayOrderReq cryptoPayOrderReq){
        logger.info("加密币订单创建接口,请求入参:{}", JSON.toJSONString(cryptoPayOrderReq));
        return cryptoOrderService.payOrder(cryptoPayOrderReq);
    }

    /**
     * 加密币退款订单
     * @param cryptoRefundOrderReq
     * @return
     */
    @RequestMapping(value = "/crypto/v1/refund-order")
    public CryptoRefundOrderRes createCryptoRefundOrder(@RequestBody CryptoRefundOrderReq cryptoRefundOrderReq){
        logger.info("加密币退款订单创建接口,请求入参:{}", JSON.toJSONString(cryptoRefundOrderReq));
        return cryptoOrderService.refundOrder(cryptoRefundOrderReq);
    }

    /**
     *
     * @param coin
     * @param networkCode
     */
    @RequestMapping(value = "/crypto/v1/confirm-order")
    public void confirmOrder(String coin,String networkCode){
        cryptoOrderService.confirmOrder(coin,networkCode);
    }

}
