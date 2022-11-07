package org.cyberpay.trade.service.activemq;

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


    @JmsListener(destination = "cryptoTradeResultQueue")
    public void subscribeCryptoTradeResultQueueQueue(String message) throws Exception{
        logger.info("加密币交易结果消息队列,订阅到:{}",message);
        CryptoTradeResultMessageDto cryptoTradeResultMessageDto = JSON.parseObject(message, CryptoTradeResultMessageDto.class);
        messageHandleService.cryptoTradeResultMessageHandle(cryptoTradeResultMessageDto);
    }
}
