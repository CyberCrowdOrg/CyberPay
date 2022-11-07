package org.cyberpay.crypto.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * <p>
 * 基础法币
 * </p>
 *
 * @author gengchaonan
 * @since 2022-09-26
 */
@Data
@TableName("coin_exchange_rate")
public class BaseFiatCoin implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 法币ID
     */
    @TableField("fiat_coin_id")
    private String fiatCoinId;

    /**
     * 法币名称
     */
    @TableField("fiat_coin_name")
    private String fiatCoinName;

    /**
     * 法币单位
     */
    @TableField("fiat_coin_uint")
    private String fiatCoinUint;

    /**
     * 法币代码
     */
    @TableField("symbol")
    private String symbol;

    /**
     * 法币图片
     */
    @TableField("fiat_coin_image")
    private String fiatCoinImage;

    /**
     * 是否可用
     */
    @TableField("is_available")
    private String isAvailable;

    /**
     * 操作人
     */
    @TableField("operator")
    private String operator;

    /**
     * 操作备注
     */
    @TableField("operator_note")
    private String operatorNote;

    @TableField("create_time")
    private Date createTime;

    @TableField("update_time")
    private Date updateTime;


}
