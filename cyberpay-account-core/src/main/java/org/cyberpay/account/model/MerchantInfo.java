package org.cyberpay.account.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 
 * 商户信息
 */
@Data
public class MerchantInfo implements Serializable {
    private Long id;

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 商户名称
     */
    private String merchantName;

    /**
     * 联系人名称
     */
    private String contactName;

    /**
     * 联系人手机号
     */
    private String contactPhone;

    /**
     * 联系人邮箱
     */
    private String contactEmail;

    /**
     * 联系人地址
     */
    private String contactAddress;

    /**
     * 接口私钥
     */
    private String privateKey;

    /**
     * 接口公钥
     */
    private String publicKey;

    /**
     * 商家状态, 1 正常 2 冻结
     */
    private String merchantStatus;

    private Date createTime;

    private Date updateTime;

    private static final long serialVersionUID = 1L;
}