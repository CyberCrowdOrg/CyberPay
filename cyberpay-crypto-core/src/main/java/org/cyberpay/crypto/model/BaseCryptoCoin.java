package org.cyberpay.crypto.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 
 * 基础加密币
 */

@Data
public class BaseCryptoCoin implements Serializable {
    private Long id;

    /**
     * 加密币ID
     */
    private String cryptoId;

    private String fullName;

    /**
     * 加密币代码
     */
    private String symbol;

    /**
     * 加密币图片
     */
    private String coinImage;

    /**
     * 加密币icon图片,移动端使用
     */
    private String coinIconImage;

    /**
     * 是否默认币种
     */
    private String isDefault;

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