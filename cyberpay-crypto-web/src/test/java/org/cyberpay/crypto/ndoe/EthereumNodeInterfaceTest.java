package org.cyberpay.crypto.ndoe;

import com.alibaba.fastjson2.JSON;
import org.cyberpay.crypto.node.NodeRequestDto;
import org.cyberpay.crypto.node.NodeResponseDto;
import org.cyberpay.crypto.node.ethereum.EthereumNodeInterface;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

@SpringBootTest
public class EthereumNodeInterfaceTest{

    private Logger logger = LoggerFactory.getLogger(BitcoinNodeInterfaceTest.class);

    @Autowired
    private EthereumNodeInterface nodeInterface;


    private NodeRequestDto nodeRequestDto = new NodeRequestDto();

    @Test
    public void generateAddress(){
        NodeResponseDto nodeResponseDto = nodeInterface.generateAddress(null);
        logger.info(JSON.toJSONString(nodeResponseDto));
    }

    @Test
    public void queryBalance(){
        nodeRequestDto.setAddress("0x34BB184bFb7B7795cc1Ded050deA1344c392A7C1");
        nodeRequestDto.setChainType("token");
        nodeRequestDto.setTokenDecimal(1000000l);
        nodeRequestDto.setContractAddress("0x07865c6E87B9F70255377e024ace6630C1Eaa37F");
        NodeResponseDto nodeResponseDto = nodeInterface.queryBalance(nodeRequestDto);
        logger.info(JSON.toJSONString(nodeResponseDto));
    }

    @Test
    public void sendTransaction(){
        //转账
        nodeRequestDto.setFromAddress("0x34BB184bFb7B7795cc1Ded050deA1344c392A7C1");
        nodeRequestDto.setToAddress("0x12975169Fc543c51b7dB76Bf36938df5bc9dd5db");
        nodeRequestDto.setFromPrivateKey("e3fe2423c0e418304a31d80ae7ba958bd5ce959fe9fe931271ee5c067f1edd83");
        nodeRequestDto.setContractAddress("0x07865c6E87B9F70255377e024ace6630C1Eaa37F");

        nodeRequestDto.setTransferAmount(new BigDecimal("1"));
        nodeRequestDto.setTokenDecimal(1000000L);
        nodeRequestDto.setCoinDecimal(6L);
        NodeResponseDto nodeResponseDto = nodeInterface.sendTransaction(nodeRequestDto);
        logger.info(JSON.toJSONString(nodeResponseDto));
    }

    @Test
    public void confirmTransaction(){
        //确认交易
        nodeRequestDto.setTransHash("0x3f4c8ae58478700faef31d7d3f9469571b2cb1cf99870a08a3736ce034899d4f");
        NodeResponseDto nodeResponseDto = nodeInterface.confirmTransaction(nodeRequestDto);
        logger.info(JSON.toJSONString(nodeResponseDto));
    }
}
