package org.cyberpay.trade.model;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 
 * 商户币种支持列表
 */
public class MerchantSupportCoin implements Serializable {
    private Long id;

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 币种名称
     */
    private String coinName;

    /**
     * 币种类型，crypto 加密币 fiat 法币
     */
    private String coinType;

    /**
     * 展示顺序
     */
    private Long displayOrder;

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

    public String getCoinType() {
        return coinType;
    }

    public void setCoinType(String coinType) {
        this.coinType = coinType;
    }

    public Long getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Long displayOrder) {
        this.displayOrder = displayOrder;
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