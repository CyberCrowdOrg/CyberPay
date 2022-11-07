package org.cyberpay.trade.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author 
 * 商户费率配置
 */
public class MerchantFeeConfig implements Serializable {
    private Long id;

    private String merchantId;

    /**
     * 币种类型，crypto 加密币 fiat 法币
     */
    private String coinType;

    /**
     * 币种名称
     */
    private String coinName;

    /**
     * 通道代码
     */
    private String channelCode;

    /**
     * 通道名称
     */
    private String channelName;

    /**
     * 手续费类型, 交易手续费 1 提现手续费 2 退款手续费
     */
    private String feeType;

    /**
     * 计费方式, 1 费率 2 固定手续费金额 3 费率+ 固定手续费金额
     */
    private String feeMethod;

    /**
     * 费率,例如 1% 即 0.1
     */
    private BigDecimal feeRate;

    /**
     * 固定手续金额
     */
    private BigDecimal feeFixed;

    /**
     * 费率状态, 1 启用 2 废止
     */
    private String feeStatus;

    private Date createTime;

    private Date updateTime;

    private String updateUser;

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

    public String getCoinType() {
        return coinType;
    }

    public void setCoinType(String coinType) {
        this.coinType = coinType;
    }

    public String getCoinName() {
        return coinName;
    }

    public void setCoinName(String coinName) {
        this.coinName = coinName;
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

    public String getFeeType() {
        return feeType;
    }

    public void setFeeType(String feeType) {
        this.feeType = feeType;
    }

    public String getFeeMethod() {
        return feeMethod;
    }

    public void setFeeMethod(String feeMethod) {
        this.feeMethod = feeMethod;
    }

    public BigDecimal getFeeRate() {
        return feeRate;
    }

    public void setFeeRate(BigDecimal feeRate) {
        this.feeRate = feeRate;
    }

    public BigDecimal getFeeFixed() {
        return feeFixed;
    }

    public void setFeeFixed(BigDecimal feeFixed) {
        this.feeFixed = feeFixed;
    }

    public String getFeeStatus() {
        return feeStatus;
    }

    public void setFeeStatus(String feeStatus) {
        this.feeStatus = feeStatus;
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

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }
}