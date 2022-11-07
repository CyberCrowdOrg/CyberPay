package org.cyberpay.crypto.service.activemq;

import com.alibaba.fastjson2.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class CustomerService {

    private Logger logger = LoggerFactory.getLogger(CustomerService.class);

    @Autowired
    MessageHandleService messageHandleService;


    @JmsListener(destination = "ethereumTradeFilterQueue")
    public void subscribeEthereumTradeFilterQueue(String message) throws Exception{
        logger.info("以太坊交易过滤消息队列,订阅到:{}",message);
        NodeFilterMessageDto nodeFilterMessageDto = JSON.parseObject(message,NodeFilterMessageDto.class);
        messageHandleService.messageHandle(nodeFilterMessageDto);
    }

    @JmsListener(destination = "bnbChainTradeFilterQueue")
    public void subscribeBNBTradeFilterQueue(String message) throws Exception{
        logger.info("BNBChain交易过滤消息队列,订阅到:{}",message);
        NodeFilterMessageDto nodeFilterMessageDto = JSON.parseObject(message,NodeFilterMessageDto.class);
        messageHandleService.messageHandle(nodeFilterMessageDto);
    }

    @JmsListener(destination = "bitcoinTradeFilterQueue")
    public void subscribeBitcoinTradeFilterQueue(String message) throws Exception{
        logger.info("比特币交易过滤消息队列,订阅到:{}",message);
        NodeFilterMessageDto nodeFilterMessageDto = JSON.parseObject(message,NodeFilterMessageDto.class);
        messageHandleService.messageHandle(nodeFilterMessageDto);
    }
}
