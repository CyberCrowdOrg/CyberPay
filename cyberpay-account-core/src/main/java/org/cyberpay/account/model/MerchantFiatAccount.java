package org.cyberpay.account.model;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author 
 * 商户法币账户
 */
@Data
public class MerchantFiatAccount implements Serializable {
    private Long id;

    private String merchantId;

    private String coinName;

    /**
     * 累计入账金额
     */
    private BigDecimal totalInAmount;

    /**
     * 累计充值金额
     */
    private BigDecimal totalRechargeAmount;

    /**
     * 累计出账金额
     */
    private BigDecimal totalOutAmount;

    /**
     * 累计手续费
     */
    private BigDecimal totalFee;

    /**
     * 可用余额
     */
    private BigDecimal availableBalance;

    /**
     * 待结算余额
     */
    private BigDecimal pendingSettleBalance;

    private String extend;

    /**
     * 账户状态，1 正常 2 冻结
     */
    private String status;

    private Date createTime;

    private Date updateTime;

    private static final long serialVersionUID = 1L;

}