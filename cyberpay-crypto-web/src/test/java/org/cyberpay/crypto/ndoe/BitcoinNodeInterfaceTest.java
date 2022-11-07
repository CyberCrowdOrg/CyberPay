package org.cyberpay.crypto.ndoe;

import com.alibaba.fastjson2.JSON;
import org.cyberpay.crypto.node.NodeRequestDto;
import org.cyberpay.crypto.node.NodeResponseDto;
import org.cyberpay.crypto.node.bitcoin.BitcoinNodeInterface;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

@SpringBootTest
public class BitcoinNodeInterfaceTest {

    private Logger logger = LoggerFactory.getLogger(BitcoinNodeInterfaceTest.class);

    @Autowired
    private BitcoinNodeInterface nodeInterface;


    private NodeRequestDto nodeRequestDto = new NodeRequestDto();

    @Test
    public void generateAddress(){
        NodeResponseDto nodeResponseDto = nodeInterface.generateAddress(null);
        logger.info(JSON.toJSONString(nodeResponseDto));
    }

    @Test
    public void queryBalance(){
        nodeRequestDto.setAddress("tb1qur8n6eedtlclk7qj5yrp82dqgjss96je6hyks2");
        NodeResponseDto nodeResponseDto = nodeInterface.queryBalance(nodeRequestDto);
        logger.info(JSON.toJSONString(nodeResponseDto));
    }

    @Test
    public void sendTransaction(){
        //转账
        nodeRequestDto.setFromAddress("tb1qur8n6eedtlclk7qj5yrp82dqgjss96je6hyks2");
        nodeRequestDto.setToAddress("tb1q9z8spa59gh4f0h4sdn0f8d9t2q3ra70ekwwz7k");
        nodeRequestDto.setFromPrivateKey("cVdBhvtR8NdrSFGtExCtMMt9m8F3tTcdQMzHWP6qGpF4Nx9BczSq");
        nodeRequestDto.setTransferAmount(new BigDecimal("0.05"));
        NodeResponseDto nodeResponseDto = nodeInterface.sendTransaction(nodeRequestDto);
        logger.info(JSON.toJSONString(nodeResponseDto));
    }

    @Test
    public void confirmTransaction(){
        //确认交易
        nodeRequestDto.setTransHash("0a14f73680705d833228847f974b792450c841414d6b88dc1ffea0613e523277");
        NodeResponseDto nodeResponseDto = nodeInterface.confirmTransaction(nodeRequestDto);
        logger.info(JSON.toJSONString(nodeResponseDto));
    }

}
