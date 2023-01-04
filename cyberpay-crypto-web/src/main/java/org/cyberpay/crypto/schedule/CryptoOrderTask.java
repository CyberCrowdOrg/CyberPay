package org.cyberpay.crypto.schedule;

import org.cyberpay.crypto.service.CryptoOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CryptoOrderTask {

    @Autowired
    CryptoOrderService cryptoOrderService;


    /**
     * 订单确认交易
     */
    @Scheduled(fixedDelay = 3500L)
    public void ethereumConfirmOrder(){
        cryptoOrderService.confirmOrder("Ethereum");
    }

    @Scheduled(fixedDelay = 60000L)
    public void bitcoinConfirmOrder(){
        cryptoOrderService.confirmOrder("Bitcoin");
    }

    @Scheduled(fixedDelay = 2000L)
    public void bnbChainConfirmOrder(){
        cryptoOrderService.confirmOrder("BNBChain_BEP2");
    }

    @Scheduled(fixedDelay = 2000L)
    public void ArbitrumConfirmOrder(){
        cryptoOrderService.confirmOrder("Arbitrum");
    }

    @Scheduled(fixedDelay = 3000L)
    public void optimismConfirmOrder(){
        cryptoOrderService.confirmOrder("Optimism");
    }

    @Scheduled(fixedDelay = 3000L)
    public void zkSyncConfirmOrder(){
        cryptoOrderService.confirmOrder("zkSync");
    }

    /**
     * 订单关闭
     */
    @Scheduled(fixedDelay = 5000L)
    public void ethereumCloseOrder(){
        cryptoOrderService.closeOrder("Ethereum");
    }

    @Scheduled(fixedDelay = 10000L)
    public void bitcoinCloseOrder(){
        cryptoOrderService.closeOrder("Bitcoin");
    }

    @Scheduled(fixedDelay = 3500L)
    public void bnbChainCloseOrder(){
        cryptoOrderService.closeOrder("BNBChain_BEP2");
    }

    @Scheduled(fixedDelay = 2000L)
    public void ArbitrumCloseOrder(){
        cryptoOrderService.closeOrder("Arbitrum");
    }

    @Scheduled(fixedDelay = 3000L)
    public void optimismCloseOrder(){
        cryptoOrderService.closeOrder("Optimism");
    }

    @Scheduled(fixedDelay = 3000L)
    public void zkSyncCloseOrder(){
        cryptoOrderService.closeOrder("zkSync");
    }
}
