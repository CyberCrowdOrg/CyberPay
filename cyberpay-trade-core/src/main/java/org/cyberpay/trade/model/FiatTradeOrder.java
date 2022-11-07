package org.cyberpay.trade.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author 
 * 法币交易订单
 */
public class FiatTradeOrder implements Serializable {
    private Long id;

    /**
     * 商户号
     */
    private String merchantId;

    /**
     * 商户订单号
     */
    private String orderNo;

    /**
     * 订单类型,收款 payment 提现 withdrawal 退款 refund 
     */
    private String orderType;

    /**
     * 交易流水号
     */
    private String tradeFlowNo;

    /**
     * 上游系统流水号
     */
    private String sysFlowNo;

    /**
     * 交易方式，online 在线支付 wallet 钱包支付 credit 信用卡
     */
    private String tradeMethod;

    private BigDecimal orderAmount;

    private String orderCoin;

    private String channelCode;

    private String channelName;

    private BigDecimal channelFee;

    private BigDecimal orderFee;

    /**
     * 支付链接
     */
    private String payLink;

    /**
     * 订单状态，0 初始状态 4 订单关闭 预订单 5 待付款 6 付款成功 7 付款失败 
     */
    private String tradeStatus;

    /**
     * 对账状态 0 未对账 1 已对账 2 对账失败
     */
    private String checkStatus;

    /**
     * 账户入账状态
     */
    private String isAccount;

    /**
     * 订单支付成功时间
     */
    private Date successTime;

    /**
     * 订单关闭时间
     */
    private Date closeTime;

    /**
     * 对账时间
     */
    private Date checkTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getTradeFlowNo() {
        return tradeFlowNo;
    }

    public void setTradeFlowNo(String tradeFlowNo) {
        this.tradeFlowNo = tradeFlowNo;
    }

    public String getSysFlowNo() {
        return sysFlowNo;
    }

    public void setSysFlowNo(String sysFlowNo) {
        this.sysFlowNo = sysFlowNo;
    }

    public String getTradeMethod() {
        return tradeMethod;
    }

    public void setTradeMethod(String tradeMethod) {
        this.tradeMethod = tradeMethod;
    }

    public BigDecimal getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(BigDecimal orderAmount) {
        this.orderAmount = orderAmount;
    }

    public String getOrderCoin() {
        return orderCoin;
    }

    public void setOrderCoin(String orderCoin) {
        this.orderCoin = orderCoin;
    }

    public String getChannelCode() {
        return channelCode;
    }

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public BigDecimal getChannelFee() {
        return channelFee;
    }

    public void setChannelFee(BigDecimal channelFee) {
        this.channelFee = channelFee;
    }

    public BigDecimal getOrderFee() {
        return orderFee;
    }

    public void setOrderFee(BigDecimal orderFee) {
        this.orderFee = orderFee;
    }

    public String getPayLink() {
        return payLink;
    }

    public void setPayLink(String payLink) {
        this.payLink = payLink;
    }

    public String getTradeStatus() {
        return tradeStatus;
    }

    public void setTradeStatus(String tradeStatus) {
        this.tradeStatus = tradeStatus;
    }

    public String getCheckStatus() {
        return checkStatus;
    }

    public void setCheckStatus(String checkStatus) {
        this.checkStatus = checkStatus;
    }

    public String getIsAccount() {
        return isAccount;
    }

    public void setIsAccount(String isAccount) {
        this.isAccount = isAccount;
    }

    public Date getSuccessTime() {
        return successTime;
    }

    public void setSuccessTime(Date successTime) {
        this.successTime = successTime;
    }

    public Date getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(Date closeTime) {
        this.closeTime = closeTime;
    }

    public Date getCheckTime() {
        return checkTime;
    }

    public void setCheckTime(Date checkTime) {
        this.checkTime = checkTime;
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