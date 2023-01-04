package org.cyberpay.crypto.controller;

import org.cyberpay.crypto.dto.BaseCryptoCoinDto;
import org.cyberpay.crypto.dto.CryptoSupportDto;
import org.cyberpay.crypto.dto.ReceiveCryptoCoinDto;
import org.cyberpay.crypto.service.BaseCryptoCoinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BaseCryptoCoinController {

    @Autowired
    BaseCryptoCoinService baseCryptoCoinService;

    @GetMapping(value = "/crypto/v1/support-list")
    public List<CryptoSupportDto> cryptoSupportList(){
        return baseCryptoCoinService.queryCryptoSupportList();
    }

    @GetMapping(value = "/crypto/v1/find")
    public BaseCryptoCoinDto findCryptoCoin(@RequestParam("cryptoCoinId") String cryptoCoinId, @RequestParam("cryptoCoinSymbol") String cryptoCoinSymbol){
        return baseCryptoCoinService.findCryptoCoin(cryptoCoinId,cryptoCoinSymbol);
    }

    @GetMapping(value = "/crypto/v1/query-receive-coin")
    public ReceiveCryptoCoinDto queryReceiveCryptoCoin(@RequestParam("coin") String coin, @RequestParam("networkCode") String networkCode){
        return baseCryptoCoinService.queryReceiveCryptoCoin(coin,networkCode);
    }
}
