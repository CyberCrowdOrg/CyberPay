package org.cyberpay.trade.service.feign;

import org.cyberpay.crypto.request.CryptoOrderSubmitPayReq;
import org.cyberpay.crypto.request.CryptoPayOrderReq;
import org.cyberpay.crypto.request.CryptoPreOrderReq;
import org.cyberpay.crypto.request.CryptoRefundOrderReq;
import org.cyberpay.crypto.response.CryptoOrderSubmitPayRes;
import org.cyberpay.crypto.response.CryptoPayOrderRes;
import org.cyberpay.crypto.response.CryptoPreOrderRes;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(value = "cyberpay-crypto-web")
public interface CryptoPayOrderFeignClient {

    @RequestMapping(value = "/cyberpay-crypto-web/crypto/v1/pre-order")
    CryptoPreOrderRes preOrder(CryptoPreOrderReq cryptoPreOrderReq);

    @RequestMapping(value = "/cyberpay-crypto-web/crypto/v1/submit-pay")
    CryptoOrderSubmitPayRes orderPaySubmit(CryptoOrderSubmitPayReq cryptoOrderSubmitPayReq);

    @RequestMapping(value = "/cyberpay-crypto-web/crypto/v1/pay-order")
    CryptoPayOrderRes createCryptoPayOrder(CryptoPayOrderReq cryptoPayOrderReq);

    @RequestMapping(value = "/cyberpay-crypto-web/crypto/v1/refund-order")
    CryptoPayOrderRes createCryptoRefundOrder(CryptoRefundOrderReq cryptoRefundOrderReq);

}
