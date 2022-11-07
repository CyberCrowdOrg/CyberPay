package org.cyberpay.crypto.model;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 
 * 加密币节点映射
 */
public class CryptoNodeMapping implements Serializable {
    private Long id;

    /**
     * 加密币代码
     */
    private String cryptoSymbol;

    /**
     * 加密币加密币类型，chain 链原生币   token 代币
     */
    private String cryptoType;

    /**
     * 网络名称
     */
    private String networkName;

    /**
     * 网络代码
     */
    private String networkCode;

    /**
     * 节点接口class
     */
    private String nodeInterfaceName;

    /**
     * 区块确认数
     */
    private Long blockConfirm;

    /**
     * 生成地址数量
     */
    private Long generateAddressNumber;

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

    public String getNetworkName() {
        return networkName;
    }

    public void setNetworkName(String networkName) {
        this.networkName = networkName;
    }

    public String getNetworkCode() {
        return networkCode;
    }

    public void setNetworkCode(String networkCode) {
        this.networkCode = networkCode;
    }

    public String getNodeInterfaceName() {
        return nodeInterfaceName;
    }

    public void setNodeInterfaceName(String nodeInterfaceName) {
        this.nodeInterfaceName = nodeInterfaceName;
    }

    public Long getBlockConfirm() {
        return blockConfirm;
    }

    public void setBlockConfirm(Long blockConfirm) {
        this.blockConfirm = blockConfirm;
    }

    public Long getGenerateAddressNumber() {
        return generateAddressNumber;
    }

    public void setGenerateAddressNumber(Long generateAddressNumber) {
        this.generateAddressNumber = generateAddressNumber;
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
}