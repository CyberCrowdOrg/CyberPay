package org.cyberpay.account.model;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author 
 * 商户法币账户流水
 */
@Data
public class MerchantFiatFlow implements Serializable {
    private Long id;

    private String merchantId;

    private Long accountId;

    /**
     * 记账方向, 0 出账 1 入账
     */
    private String lable;

    /**
     * 币种名
     */
    private String coinName;

    private String tradeFlowNo;

    /**
     * 账务流水号
     */
    private String bizFlowNo;

    private BigDecimal openingBalance;

    private BigDecimal businessAmount;

    private BigDecimal endingBalance;

    /**
     * 记账状态, 0 未入账 1 已入账 2 入账失败
     */
    private String status;

    /**
     * 对账编号
     */
    private String checkNo;

    /**
     * 对账状态
     */
    private String checkStatus;

    /**
     * 对账时间
     */
    private Date checkTime;

    private Date createTime;

    private Date updateTime;

    private static final long serialVersionUID = 1L;

}