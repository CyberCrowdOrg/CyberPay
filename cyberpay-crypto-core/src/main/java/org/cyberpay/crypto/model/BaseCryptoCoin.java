package org.cyberpay.crypto.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 
 * 基础加密币
 */
@Data
@TableName("base_crypto_coin")
public class BaseCryptoCoin implements Serializable {

    @TableId("id")
    private Long id;

    /**
     * 加密币ID
     */
    @TableField("crypto_id")
    private String cryptoId;

    @TableField("full_name")
    private String fullName;

    /**
     * 加密币代码
     */
    @TableField("symbol")
    private String symbol;

    /**
     * 加密币图片
     */
    @TableField("coin_image")
    private String coinImage;

    /**
     * 加密币icon图片,移动端使用
     */
    @TableField("crypto_id")
    private String coinIconImage;

    /**
     * 是否默认币种
     */
    @TableField("is_default")
    private String isDefault;

    @TableField("extend")
    private String extend;

    @TableField("extend2")
    private String extend2;

    @TableField("create_time")
    private Date createTime;

    @TableField("update_time")
    private Date updateTime;

    private static final long serialVersionUID = 1L;

}