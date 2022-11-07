package org.cyberpay.crypto.controller;

import org.cyberpay.crypto.dto.BaseFiatCoinDto;
import org.cyberpay.crypto.dto.FiatSupportDto;
import org.cyberpay.crypto.service.BaseFiatCoinService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BaseFiatCoinController {

    private Logger logger = LoggerFactory.getLogger(BaseFiatCoinController.class);

    @Autowired
    BaseFiatCoinService baseFiatCoinService;

    @GetMapping(value = "/fiat/v1/support.list")
    public List<FiatSupportDto> fiatSupportList(){
        return baseFiatCoinService.queryFiatSupportList();
    }

    @GetMapping(value = "/fiat/v1/find")
    public BaseFiatCoinDto findFiatCoin(String fiatCoinId, String fiatCoinSymbol){
        return baseFiatCoinService.findFiatCoin(fiatCoinId,fiatCoinSymbol);
    }


}
