package org.cyberpay.crypto.model;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 
 * 加密币地址信息
 */
public class CryptoAddressInfo implements Serializable {
    private Long id;

    /**
     * 币种代码
     */
    private String cryptoSymbol;

    /**
     * 网络代码
     */
    private String networkCode;

    /**
     * 地址类型, refund 退款地址 trade 交易地址
     */
    private String addressType;

    /**
     * 钱包地址
     */
    private String address;

    /**
     * 私钥
     */
    private String privateKey;

    /**
     * 公钥
     */
    private String publicKey;

    /**
     * 助记词
     */
    private String mnemonics;

    /**
     * 钱包文件路径
     */
    private String walletFilePath;

    private Long score;

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

    public String getNetworkCode() {
        return networkCode;
    }

    public void setNetworkCode(String networkCode) {
        this.networkCode = networkCode;
    }

    public String getAddressType() {
        return addressType;
    }

    public void setAddressType(String addressType) {
        this.addressType = addressType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getMnemonics() {
        return mnemonics;
    }

    public void setMnemonics(String mnemonics) {
        this.mnemonics = mnemonics;
    }

    public String getWalletFilePath() {
        return walletFilePath;
    }

    public void setWalletFilePath(String walletFilePath) {
        this.walletFilePath = walletFilePath;
    }

    public Long getScore() {
        return score;
    }

    public void setScore(Long score) {
        this.score = score;
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
        CryptoAddressInfo other = (CryptoAddressInfo) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getCryptoSymbol() == null ? other.getCryptoSymbol() == null : this.getCryptoSymbol().equals(other.getCryptoSymbol()))
            && (this.getNetworkCode() == null ? other.getNetworkCode() == null : this.getNetworkCode().equals(other.getNetworkCode()))
            && (this.getAddressType() == null ? other.getAddressType() == null : this.getAddressType().equals(other.getAddressType()))
            && (this.getAddress() == null ? other.getAddress() == null : this.getAddress().equals(other.getAddress()))
            && (this.getPrivateKey() == null ? other.getPrivateKey() == null : this.getPrivateKey().equals(other.getPrivateKey()))
            && (this.getPublicKey() == null ? other.getPublicKey() == null : this.getPublicKey().equals(other.getPublicKey()))
            && (this.getMnemonics() == null ? other.getMnemonics() == null : this.getMnemonics().equals(other.getMnemonics()))
            && (this.getWalletFilePath() == null ? other.getWalletFilePath() == null : this.getWalletFilePath().equals(other.getWalletFilePath()))
            && (this.getScore() == null ? other.getScore() == null : this.getScore().equals(other.getScore()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getCryptoSymbol() == null) ? 0 : getCryptoSymbol().hashCode());
        result = prime * result + ((getNetworkCode() == null) ? 0 : getNetworkCode().hashCode());
        result = prime * result + ((getAddressType() == null) ? 0 : getAddressType().hashCode());
        result = prime * result + ((getAddress() == null) ? 0 : getAddress().hashCode());
        result = prime * result + ((getPrivateKey() == null) ? 0 : getPrivateKey().hashCode());
        result = prime * result + ((getPublicKey() == null) ? 0 : getPublicKey().hashCode());
        result = prime * result + ((getMnemonics() == null) ? 0 : getMnemonics().hashCode());
        result = prime * result + ((getWalletFilePath() == null) ? 0 : getWalletFilePath().hashCode());
        result = prime * result + ((getScore() == null) ? 0 : getScore().hashCode());
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
        sb.append(", networkCode=").append(networkCode);
        sb.append(", addressType=").append(addressType);
        sb.append(", address=").append(address);
        sb.append(", privateKey=").append(privateKey);
        sb.append(", publicKey=").append(publicKey);
        sb.append(", mnemonics=").append(mnemonics);
        sb.append(", walletFilePath=").append(walletFilePath);
        sb.append(", score=").append(score);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}