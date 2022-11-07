package org.cyberpay.crypto.controller;

import org.cyberpay.crypto.service.CoinExchangeRateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CoinExchangeRateController {

    private Logger logger = LoggerFactory.getLogger(CoinExchangeRateController.class);

    @Autowired
    CoinExchangeRateService coinExchangeRateService;

    @GetMapping(value = "/exchange.rate/v1/fiat.update")
    public void exchangeRateFiatUpdate(){
        logger.info("==== 进入法币汇率更新接口 ====");
        coinExchangeRateService.updateFiatRate();
    }
}
