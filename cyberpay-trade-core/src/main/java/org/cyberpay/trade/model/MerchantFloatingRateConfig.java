package org.cyberpay.trade.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author 
 * 商户浮动汇率配置
 */
public class MerchantFloatingRateConfig implements Serializable {
    private Long id;

    /**
     * 商户ID
     */
    private String merchantId;

    private String coinName;

    /**
     * 浮动汇率值
     */
    private BigDecimal floatingRate;

    /**
     * 状态，1 启用 2 废止
     */
    private String status;

    private Date createTime;

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

    public String getCoinName() {
        return coinName;
    }

    public void setCoinName(String coinName) {
        this.coinName = coinName;
    }

    public BigDecimal getFloatingRate() {
        return floatingRate;
    }

    public void setFloatingRate(BigDecimal floatingRate) {
        this.floatingRate = floatingRate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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