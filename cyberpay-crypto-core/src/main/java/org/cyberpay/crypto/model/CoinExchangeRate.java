package org.cyberpay.crypto.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 币种汇率
 * </p>
 *
 * @author gengchaonan
 * @since 2022-09-26
 */
@Data
@TableName("coin_exchange_rate")
public class CoinExchangeRate implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("from_coin_id")
    private String fromCoinId;

    @TableField("to_coin_id")
    private Integer toCoinId;

    @TableField("from_symbol")
    private String fromSymbol;

    @TableField("to_symbol")
    private String toSymbol;

    @TableField("exchange_rate")
    private BigDecimal exchangeRate;

    @TableField("exchange_type")
    private String exchangeType;

    @TableField("exchange_source")
    private String exchangeSource;

    @TableField("extend")
    private String extend;

    @TableField("extend2")
    private String extend2;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;


}
