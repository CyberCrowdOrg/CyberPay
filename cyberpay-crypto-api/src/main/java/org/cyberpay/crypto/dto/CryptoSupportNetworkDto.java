package org.cyberpay.crypto.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 
 * 
 */
@Data
public class CryptoSupportNetworkDto implements Serializable {
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

}