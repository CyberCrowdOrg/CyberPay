package org.cyberpay.crypto.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author 
 * 区块链数据
 */
public class BlockChainData implements Serializable {
    private Long id;

    private String cyrptoSymbol;

    private String networkCode;

    private String transHash;

    private String blockHash;

    private Long blockNumber;

    private BigDecimal transAmount;

    private String receiveAddress;

    private String checkStatus;

    private String checkNo;

    private Date checkTime;

    private Date createTime;

    private Date updateTime;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCyrptoSymbol() {
        return cyrptoSymbol;
    }

    public void setCyrptoSymbol(String cyrptoSymbol) {
        this.cyrptoSymbol = cyrptoSymbol;
    }

    public String getNetworkCode() {
        return networkCode;
    }

    public void setNetworkCode(String networkCode) {
        this.networkCode = networkCode;
    }

    public String getTransHash() {
        return transHash;
    }

    public void setTransHash(String transHash) {
        this.transHash = transHash;
    }

    public String getBlockHash() {
        return blockHash;
    }

    public void setBlockHash(String blockHash) {
        this.blockHash = blockHash;
    }

    public Long getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(Long blockNumber) {
        this.blockNumber = blockNumber;
    }

    public BigDecimal getTransAmount() {
        return transAmount;
    }

    public void setTransAmount(BigDecimal transAmount) {
        this.transAmount = transAmount;
    }

    public String getReceiveAddress() {
        return receiveAddress;
    }

    public void setReceiveAddress(String receiveAddress) {
        this.receiveAddress = receiveAddress;
    }

    public String getCheckStatus() {
        return checkStatus;
    }

    public void setCheckStatus(String checkStatus) {
        this.checkStatus = checkStatus;
    }

    public String getCheckNo() {
        return checkNo;
    }

    public void setCheckNo(String checkNo) {
        this.checkNo = checkNo;
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

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        BlockChainData other = (BlockChainData) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getCyrptoSymbol() == null ? other.getCyrptoSymbol() == null : this.getCyrptoSymbol().equals(other.getCyrptoSymbol()))
            && (this.getNetworkCode() == null ? other.getNetworkCode() == null : this.getNetworkCode().equals(other.getNetworkCode()))
            && (this.getTransHash() == null ? other.getTransHash() == null : this.getTransHash().equals(other.getTransHash()))
            && (this.getBlockHash() == null ? other.getBlockHash() == null : this.getBlockHash().equals(other.getBlockHash()))
            && (this.getBlockNumber() == null ? other.getBlockNumber() == null : this.getBlockNumber().equals(other.getBlockNumber()))
            && (this.getTransAmount() == null ? other.getTransAmount() == null : this.getTransAmount().equals(other.getTransAmount()))
            && (this.getReceiveAddress() == null ? other.getReceiveAddress() == null : this.getReceiveAddress().equals(other.getReceiveAddress()))
            && (this.getCheckStatus() == null ? other.getCheckStatus() == null : this.getCheckStatus().equals(other.getCheckStatus()))
            && (this.getCheckNo() == null ? other.getCheckNo() == null : this.getCheckNo().equals(other.getCheckNo()))
            && (this.getCheckTime() == null ? other.getCheckTime() == null : this.getCheckTime().equals(other.getCheckTime()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getCyrptoSymbol() == null) ? 0 : getCyrptoSymbol().hashCode());
        result = prime * result + ((getNetworkCode() == null) ? 0 : getNetworkCode().hashCode());
        result = prime * result + ((getTransHash() == null) ? 0 : getTransHash().hashCode());
        result = prime * result + ((getBlockHash() == null) ? 0 : getBlockHash().hashCode());
        result = prime * result + ((getBlockNumber() == null) ? 0 : getBlockNumber().hashCode());
        result = prime * result + ((getTransAmount() == null) ? 0 : getTransAmount().hashCode());
        result = prime * result + ((getReceiveAddress() == null) ? 0 : getReceiveAddress().hashCode());
        result = prime * result + ((getCheckStatus() == null) ? 0 : getCheckStatus().hashCode());
        result = prime * result + ((getCheckNo() == null) ? 0 : getCheckNo().hashCode());
        result = prime * result + ((getCheckTime() == null) ? 0 : getCheckTime().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getUpdateTime() == null) ? 0 : getUpdateTime().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", cyrptoSymbol=").append(cyrptoSymbol);
        sb.append(", networkCode=").append(networkCode);
        sb.append(", transHash=").append(transHash);
        sb.append(", blockHash=").append(blockHash);
        sb.append(", blockNumber=").append(blockNumber);
        sb.append(", transAmount=").append(transAmount);
        sb.append(", receiveAddress=").append(receiveAddress);
        sb.append(", checkStatus=").append(checkStatus);
        sb.append(", checkNo=").append(checkNo);
        sb.append(", checkTime=").append(checkTime);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}