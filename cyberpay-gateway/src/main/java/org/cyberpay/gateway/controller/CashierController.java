package org.cyberpay.gateway.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CashierController {

    private Logger logger = LoggerFactory.getLogger(CashierController.class);



    @RequestMapping(value = "/cashier/crypto/v1/coins")
    @ResponseBody
    public Object cashierCryptoCoins(String accessKey){
        logger.info("收银台(加密币)交易币种-订单基础数据查询接口,请求入参 :{}",accessKey);
        return null;
    }


    @RequestMapping(value = "/cashier/fiat/v1/coins")
    @ResponseBody
    public Object cashierFiatCoins(String accessKey){

        return null;
    }
}
