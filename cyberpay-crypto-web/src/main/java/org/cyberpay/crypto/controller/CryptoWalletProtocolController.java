package org.cyberpay.crypto.controller;

import org.cyberpay.crypto.dto.CryptoWalletProtocolDto;
import org.cyberpay.crypto.service.CryptoWalletProtocolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CryptoWalletProtocolController {

    @Autowired
    CryptoWalletProtocolService cryptoWalletProtocolService;

    @GetMapping(value = "/crypto.wallet.protocol/v1/query")
    public List<CryptoWalletProtocolDto> queryCryptoWalletProtocol(String cryptoSymbol){
        return cryptoWalletProtocolService.queryCryptoWalletProtocol(cryptoSymbol);
    }

}
