package org.cyberpay.crypto.ndoe;

import com.alibaba.fastjson2.JSON;
import org.cyberpay.crypto.node.NodeRequestDto;
import org.cyberpay.crypto.node.NodeResponseDto;
import org.cyberpay.crypto.node.binance.BinanceNodeInterface;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

@SpringBootTest
public class BinanceNodeInterfaceTest {

    private Logger logger = LoggerFactory.getLogger(BinanceNodeInterfaceTest.class);

    @Autowired
    private BinanceNodeInterface nodeInterface;


    private NodeRequestDto nodeRequestDto = new NodeRequestDto();

    @Test
    public void generateAddress(){
        NodeResponseDto nodeResponseDto = nodeInterface.generateAddress(null);
        logger.info(JSON.toJSONString(nodeResponseDto));
    }

    @Test
    public void queryBalance(){
        nodeRequestDto.setAddress("tbnb1w8xt7zscje6ngrr7rjce0p60muasmdcjrp9uvs");
        nodeRequestDto.setSymbol("3LAU-544");
        NodeResponseDto nodeResponseDto = nodeInterface.queryBalance(nodeRequestDto);
        logger.info(JSON.toJSONString(nodeResponseDto));
    }

    @Test
    public void sendTransaction(){
        //转账
        nodeRequestDto.setFromAddress("tbnb1jk5wnzng8u7futg6raev2ef8ctckj9xmchctp4");
        nodeRequestDto.setToAddress("tbnb16hywxpvvkaz6cecjz89mf2w0da3vfeg6z6yky2");
        nodeRequestDto.setFromPrivateKey("d5c8d6ecee95bb9903a0cb5f51ff78d3726ed7b30f54ac250ccb3c4f9720e25f");
        nodeRequestDto.setTransferAmount(new BigDecimal("0.01"));
        nodeRequestDto.setSymbol("BNB");
        NodeResponseDto nodeResponseDto = nodeInterface.sendTransaction(nodeRequestDto);
        logger.info(JSON.toJSONString(nodeResponseDto));
    }

    @Test
    public void confirmTransaction(){
        //确认交易
        nodeRequestDto.setTransHash("8A4A771C6A9954DDC0960101D4F8E097476713E07FE2198F8FF27842481B92CC");
        NodeResponseDto nodeResponseDto = nodeInterface.confirmTransaction(nodeRequestDto);
        logger.info(JSON.toJSONString(nodeResponseDto));
    }
}
