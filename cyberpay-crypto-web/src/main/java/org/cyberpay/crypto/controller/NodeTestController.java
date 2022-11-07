package org.cyberpay.crypto.controller;

import com.alibaba.fastjson2.JSON;
import org.cyberpay.crypto.node.NodeRequestDto;
import org.cyberpay.crypto.node.NodeResponseDto;
import org.cyberpay.crypto.node.ethereum.EthereumNodeInterface;
import org.cyberpay.crypto.node.NodeInterface;
import org.cyberpay.crypto.utils.SpringContextUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
public class NodeTestController {


    @GetMapping(value = "/node.test/v1/bitcoin")
    public String bitcoin(){

        NodeRequestDto nodeRequestDto = new NodeRequestDto();


        NodeInterface nodeInterface = (NodeInterface) SpringContextUtil.getBean(
                "bitcoinNodeInterface");
//        nodeInterface.generateAddress(null);

          //查询余额
//        nodeRequestDto.setAddress("tb1qur8n6eedtlclk7qj5yrp82dqgjss96je6hyks2");
//        nodeInterface.queryBalance(nodeRequestDto);

//        //转账
//        nodeRequestDto.setFromAddress("tb1qur8n6eedtlclk7qj5yrp82dqgjss96je6hyks2");
//        nodeRequestDto.setToAddress("tb1q9z8spa59gh4f0h4sdn0f8d9t2q3ra70ekwwz7k");
//        nodeRequestDto.setFromPrivateKey("cVdBhvtR8NdrSFGtExCtMMt9m8F3tTcdQMzHWP6qGpF4Nx9BczSq");
//        nodeRequestDto.setTransferAmount(new BigDecimal("0.05"));
//        NodeResponseDto nodeResponseDto = nodeInterface.sendTransaction(nodeRequestDto);

        //确认交易
        nodeRequestDto.setTransHash("0a14f73680705d833228847f974b792450c841414d6b88dc1ffea0613e523277");
        NodeResponseDto nodeResponseDto = nodeInterface.confirmTransaction(nodeRequestDto);


//        NodeInterface nodeInterface = (NodeInterface) SpringContextUtil.getBean(
//                "ethereumNodeInterface");
//        nodeInterface.queryBalance(null);
//        nodeInterface.generateAddress(null);
//        nodeInterface.sendTransaction(null);
//        nodeInterface.confirmTransaction(null);

        System.out.println(JSON.toJSONString(nodeResponseDto));
        return "OK";
    }
}
