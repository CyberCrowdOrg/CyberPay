package org.cyberpay.crypto.model;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author 
 * 加密币订单
 */
@Data
public class CryptoOrder implements Serializable {
    private Long id;

    /**
     * 业务订单号
     */
    private String bizOrderNo;

    /**
     * 原业务订单号，仅退款时需要
     */
    private String orgBizOrderNo;

    /**
     * 业务类型,收款 paymen 提现 withdrawal 退款 refund 
     */
    private String bizType;

    /**
     * 业务流水号
     */
    private String bizFlowNo;

    /**
     * 订单币种ID
     */
    private String orderCoin;

    /**
     * 订单金额
     */
    private BigDecimal orderAmount;

    /**
     * 收款网络代码
     */
    private String receiveNetworkCode;

    /**
     * 收款币种类型
     */
    private String receiveCoinType;

    /**
     * 收款币种ID
     */
    private String receiveCoin;

    /**
     * 收款金额，即订单应付款金额
     */
    private BigDecimal receiveAmount;

    /**
     * 收据金额,即实际我们地址收到的金额
     */
    private BigDecimal receiptAmount;

    /**
     * 收款差额
     */
    private BigDecimal diffReceiveAmount;

    /**
     * 收款地址
     */
    private String receiveAddress;

    /**
     * 订单状态,unpaid 未付款 unconfirmed 未确认  success 付款成功 msuccess 多付成功 lsuccess 少付成功 close 超时关闭
     */
    private String status;
    /**
     * 付款成功时间
     */
    private Date successTime;

    /**
     * 超时关闭时间
     */
    private Date closeTime;

    /**
     * 对账状态，0 未对账  1 对账成功 2 对账失败
     */
    private String checkStatus;

    private Date checkTime;

    private Date createTime;

    private Date updateTime;

    private static final long serialVersionUID = 1L;
}