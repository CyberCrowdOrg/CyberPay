package org.cyberpay.crypto.schedule;

import com.xxl.job.core.handler.annotation.XxlJob;
import org.cyberpay.crypto.service.CryptoSupportNetworkService;
import org.springframework.beans.factory.annotation.Autowired;

//@Component
public class CryptoSupportNetworkJobHandler {

    @Autowired
    CryptoSupportNetworkService cryptoSupportNetworkService;

    @XxlJob(value = "cacheAllContract")
    public void cacheAllContract() throws Exception{
        cryptoSupportNetworkService.cacheAllContarct();
    }
}
