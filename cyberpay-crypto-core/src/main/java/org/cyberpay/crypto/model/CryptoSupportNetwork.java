package org.cyberpay.crypto.model;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 
 * 
 */
public class CryptoSupportNetwork implements Serializable {
    private Long id;

    /**
     * 加密币代码
     */
    private String cryptoSymbol;

    /**
     * 加密币类型，chain 链原生币   token 代币
     */
    private String cryptoType;

    /**
     * 节点名称，例如 Ethereum 、Polkadot、Bitcoin、BinanceChain
     */
    private String nodeName;

    /**
     * 链上代币资产ID
     */
    private String tokenAssetsId;

    /**
     * 合约地址
     */
    private String contractAddress;

    /**
     * 加密币精度位数, 18
     */
    private Long coinDecimalUnit;

    /**
     * 加密币精度
     */
    private Long coinDecimal;

    /**
     * 网络代码, 代币名称加上网络名称,例如 ETH_Ethereum_ERC20
     */
    private String networkCode;

    /**
     * 网络名称，Ethereum ERC20、BinanceChain BEP2
     */
    private String networkName;

    /**
     * 网络类型, layer  一层网络  layer2 二层网络
     */
    private String networkType;

    /**
     * 是否可用
     */
    private String isAvailable;

    private String extend;

    private String extend2;

    private Date createTime;

    private Date updateTime;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCryptoSymbol() {
        return cryptoSymbol;
    }

    public void setCryptoSymbol(String cryptoSymbol) {
        this.cryptoSymbol = cryptoSymbol;
    }

    public String getCryptoType() {
        return cryptoType;
    }

    public void setCryptoType(String cryptoType) {
        this.cryptoType = cryptoType;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getTokenAssetsId() {
        return tokenAssetsId;
    }

    public void setTokenAssetsId(String tokenAssetsId) {
        this.tokenAssetsId = tokenAssetsId;
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }

    public Long getCoinDecimalUnit() {
        return coinDecimalUnit;
    }

    public void setCoinDecimalUnit(Long coinDecimalUnit) {
        this.coinDecimalUnit = coinDecimalUnit;
    }

    public Long getCoinDecimal() {
        return coinDecimal;
    }

    public void setCoinDecimal(Long coinDecimal) {
        this.coinDecimal = coinDecimal;
    }

    public String getNetworkCode() {
        return networkCode;
    }

    public void setNetworkCode(String networkCode) {
        this.networkCode = networkCode;
    }

    public String getNetworkName() {
        return networkName;
    }

    public void setNetworkName(String networkName) {
        this.networkName = networkName;
    }

    public String getNetworkType() {
        return networkType;
    }

    public void setNetworkType(String networkType) {
        this.networkType = networkType;
    }

    public String getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(String isAvailable) {
        this.isAvailable = isAvailable;
    }

    public String getExtend() {
        return extend;
    }

    public void setExtend(String extend) {
        this.extend = extend;
    }

    public String getExtend2() {
        return extend2;
    }

    public void setExtend2(String extend2) {
        this.extend2 = extend2;
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
        CryptoSupportNetwork other = (CryptoSupportNetwork) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getCryptoSymbol() == null ? other.getCryptoSymbol() == null : this.getCryptoSymbol().equals(other.getCryptoSymbol()))
            && (this.getCryptoType() == null ? other.getCryptoType() == null : this.getCryptoType().equals(other.getCryptoType()))
            && (this.getNodeName() == null ? other.getNodeName() == null : this.getNodeName().equals(other.getNodeName()))
            && (this.getTokenAssetsId() == null ? other.getTokenAssetsId() == null : this.getTokenAssetsId().equals(other.getTokenAssetsId()))
            && (this.getContractAddress() == null ? other.getContractAddress() == null : this.getContractAddress().equals(other.getContractAddress()))
            && (this.getCoinDecimalUnit() == null ? other.getCoinDecimalUnit() == null : this.getCoinDecimalUnit().equals(other.getCoinDecimalUnit()))
            && (this.getCoinDecimal() == null ? other.getCoinDecimal() == null : this.getCoinDecimal().equals(other.getCoinDecimal()))
            && (this.getNetworkCode() == null ? other.getNetworkCode() == null : this.getNetworkCode().equals(other.getNetworkCode()))
            && (this.getNetworkName() == null ? other.getNetworkName() == null : this.getNetworkName().equals(other.getNetworkName()))
            && (this.getNetworkType() == null ? other.getNetworkType() == null : this.getNetworkType().equals(other.getNetworkType()))
            && (this.getIsAvailable() == null ? other.getIsAvailable() == null : this.getIsAvailable().equals(other.getIsAvailable()))
            && (this.getExtend() == null ? other.getExtend() == null : this.getExtend().equals(other.getExtend()))
            && (this.getExtend2() == null ? other.getExtend2() == null : this.getExtend2().equals(other.getExtend2()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getCryptoSymbol() == null) ? 0 : getCryptoSymbol().hashCode());
        result = prime * result + ((getCryptoType() == null) ? 0 : getCryptoType().hashCode());
        result = prime * result + ((getNodeName() == null) ? 0 : getNodeName().hashCode());
        result = prime * result + ((getTokenAssetsId() == null) ? 0 : getTokenAssetsId().hashCode());
        result = prime * result + ((getContractAddress() == null) ? 0 : getContractAddress().hashCode());
        result = prime * result + ((getCoinDecimalUnit() == null) ? 0 : getCoinDecimalUnit().hashCode());
        result = prime * result + ((getCoinDecimal() == null) ? 0 : getCoinDecimal().hashCode());
        result = prime * result + ((getNetworkCode() == null) ? 0 : getNetworkCode().hashCode());
        result = prime * result + ((getNetworkName() == null) ? 0 : getNetworkName().hashCode());
        result = prime * result + ((getNetworkType() == null) ? 0 : getNetworkType().hashCode());
        result = prime * result + ((getIsAvailable() == null) ? 0 : getIsAvailable().hashCode());
        result = prime * result + ((getExtend() == null) ? 0 : getExtend().hashCode());
        result = prime * result + ((getExtend2() == null) ? 0 : getExtend2().hashCode());
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
        sb.append(", cryptoSymbol=").append(cryptoSymbol);
        sb.append(", cryptoType=").append(cryptoType);
        sb.append(", nodeName=").append(nodeName);
        sb.append(", tokenAssetsId=").append(tokenAssetsId);
        sb.append(", contractAddress=").append(contractAddress);
        sb.append(", coinDecimalUnit=").append(coinDecimalUnit);
        sb.append(", coinDecimal=").append(coinDecimal);
        sb.append(", networkCode=").append(networkCode);
        sb.append(", networkName=").append(networkName);
        sb.append(", networkType=").append(networkType);
        sb.append(", isAvailable=").append(isAvailable);
        sb.append(", extend=").append(extend);
        sb.append(", extend2=").append(extend2);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}