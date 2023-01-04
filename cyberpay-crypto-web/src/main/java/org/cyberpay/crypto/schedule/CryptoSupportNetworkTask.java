package org.cyberpay.crypto.schedule;

import org.cyberpay.crypto.service.CryptoSupportNetworkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CryptoSupportNetworkTask {

    @Autowired
    CryptoSupportNetworkService cryptoSupportNetworkService;

    @Scheduled(fixedDelay = 3000000L)
    public void cacheAllContractTask(){
        cryptoSupportNetworkService.cacheAllContarct();
    }
}
