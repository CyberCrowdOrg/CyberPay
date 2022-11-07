package org.cyberpay.crypto.ndoe;

import com.alibaba.fastjson2.JSON;
import org.cyberpay.crypto.node.NodeRequestDto;
import org.cyberpay.crypto.node.NodeResponseDto;
import org.cyberpay.crypto.node.bitcoincash.BitcoinCashNodeInterface;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class BitcoinCashNodeInterfaceTest {

    private Logger logger = LoggerFactory.getLogger(BitcoinCashNodeInterfaceTest.class);

    @Autowired
    private BitcoinCashNodeInterface nodeInterface;

    private NodeRequestDto nodeRequestDto = new NodeRequestDto();

    @Test
    public void generateAddress(){
        NodeResponseDto nodeResponseDto = nodeInterface.generateAddress(null);
        logger.info(JSON.toJSONString(nodeResponseDto));
    }
}
