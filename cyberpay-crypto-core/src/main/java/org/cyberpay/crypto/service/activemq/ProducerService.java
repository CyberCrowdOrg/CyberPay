package org.cyberpay.crypto.service.activemq;

import com.alibaba.fastjson2.JSON;
import org.apache.activemq.command.ActiveMQQueue;
import org.cyberpay.crypto.model.CryptoOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class ProducerService {

    private Logger logger = LoggerFactory.getLogger(ProducerService.class);

    @Autowired
    JmsMessagingTemplate jmsMessagingTemplate;

    public void cryptoTradeResultMessage(CryptoOrder cryptoOrder){
        CryptoTradeResultMessageDto cryptoTradeResultMessageDto = new CryptoTradeResultMessageDto();
        cryptoTradeResultMessageDto.setTradeFlowNo(cryptoOrder.getBizOrderNo());
        cryptoTradeResultMessageDto.setStatus(cryptoOrder.getStatus());
        cryptoTradeResultMessageDto.setReceiptAmount(cryptoOrder.getReceiptAmount());
        String message = JSON.toJSONString(cryptoTradeResultMessageDto);
        jmsMessagingTemplate.convertAndSend(new ActiveMQQueue("cryptoTradeResultQueue"), message);
        logger.info("加密币节点交易过滤消息处理, 发送消息到交易模块:{}",message);
    }
}
