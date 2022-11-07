package org.cyberpay.crypto.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author 
 * 加密币订单扩展数据
 */
public class CryptoOrderExtend implements Serializable {

    private Long id;

    /**
     * 业务流水号
     */
    private String bizFlowNo;

    /**
     * 付款地址，即付款人地址
     */
    private String paymentAddress;

    /**
     * 真实收款金额,即真实汇率计算的金额
     */
    private BigDecimal realReceiveAmount;

    /**
     * 真实汇率,原始汇率数据
     */
    private BigDecimal realExchangeRate;

    /**
     * 订单汇率,根据原始汇率+ 其他费用后的汇率,收款金额是用它来计算的
     */
    private BigDecimal orderExchangeRate;

    /**
     * 区块hash
     */
    private String blockHash;

    /**
     * 交易hash
     */
    private String transHash;

    /**
     * 区块号
     */
    private Long blockNumber;

    /**
     * 确认数量
     */
    private Long confirm;

    private Date createTime;

    private Date updateTime;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBizFlowNo() {
        return bizFlowNo;
    }

    public void setBizFlowNo(String bizFlowNo) {
        this.bizFlowNo = bizFlowNo;
    }

    public String getPaymentAddress() {
        return paymentAddress;
    }

    public void setPaymentAddress(String paymentAddress) {
        this.paymentAddress = paymentAddress;
    }

    public BigDecimal getRealReceiveAmount() {
        return realReceiveAmount;
    }

    public void setRealReceiveAmount(BigDecimal realReceiveAmount) {
        this.realReceiveAmount = realReceiveAmount;
    }

    public BigDecimal getRealExchangeRate() {
        return realExchangeRate;
    }

    public void setRealExchangeRate(BigDecimal realExchangeRate) {
        this.realExchangeRate = realExchangeRate;
    }

    public BigDecimal getOrderExchangeRate() {
        return orderExchangeRate;
    }

    public void setOrderExchangeRate(BigDecimal orderExchangeRate) {
        this.orderExchangeRate = orderExchangeRate;
    }

    public String getBlockHash() {
        return blockHash;
    }

    public void setBlockHash(String blockHash) {
        this.blockHash = blockHash;
    }

    public String getTransHash() {
        return transHash;
    }

    public void setTransHash(String transHash) {
        this.transHash = transHash;
    }

    public Long getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(Long blockNumber) {
        this.blockNumber = blockNumber;
    }

    public Long getConfirm() {
        return confirm;
    }

    public void setConfirm(Long confirm) {
        this.confirm = confirm;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}