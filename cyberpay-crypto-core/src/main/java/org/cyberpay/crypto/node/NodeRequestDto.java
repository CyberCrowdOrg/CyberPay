package org.cyberpay.crypto.node;

import lombok.Data;
import org.cyberpay.crypto.model.CryptoNodeMapping;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class NodeRequestDto implements Serializable {

    //地址生成次数
    private CryptoNodeMapping cryptoNodeMapping;

    //余额查询
    private String address;
    private String symbol;
    private Long tokenDecimal;
    private Long coinDecimal;

    //转账
    private String fromAddress;
    private String fromPrivateKey;
    private String toAddress;
    private BigDecimal transferAmount;
    private String contractAddress;
    private String chainType;

    //交易结果查询
    private String transHash;



}
