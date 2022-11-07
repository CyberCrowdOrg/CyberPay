package org.cyberpay.crypto.node;

import lombok.Data;
import org.cyberpay.crypto.enums.ReturnCodeEnum;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class NodeResponseDto implements Serializable {

    private String returnCode = "SUCCESS";
    private String returnMsg = "success";
    private String errorMsg;

    //余额查询
    private BigDecimal balance;
    private BigDecimal tokenBalance;

    //钱包地址生成
    private String privateKey;
    private String publicKey;
    private String mnemonics;
    private String address;
    private String walletFilePath;

    //转账
    private String transHash;

    //确认交易结果
    private boolean status;
    private String blockHash;
    private Long blockNumber;
    private long confirm;
    private String fromAddress;
    private String toAddress;
    private BigDecimal receiptAmount;
    private BigDecimal fee;
    private String memo;

    public void setReturnCodeEnum(ReturnCodeEnum returnCodeEnum,String errorMsg){
        this.returnCode = returnCodeEnum.getCode();
        this.returnMsg = returnCodeEnum.getName();
        this.errorMsg = errorMsg;
    }

}
