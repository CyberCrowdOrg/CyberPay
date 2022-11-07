package org.cyberpay.trade.model;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author 
 * 加密币交易订单
 */
@Data
public class CryptoTradeOrder implements Serializable {
    private Long id;

    /**
     * 商户号
     */
    private String merchantId;

    /**
     * 商户订单号
     */
    private String orderNo;

    /**
     * 交易流水号
     */
    private String tradeFlowNo;

    /**
     * 订单类型,收款 payment 提现 withdrawal 退款 refund 
     */
    private String orderType;

    /**
     * 上游系统流水号
     */
    private String sysFlowNo;

    /**
     * 订单币种名称
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
     * 收款币种名称
     */
    private String recieveCoin;

    /**
     * 收款金额
     */
    private BigDecimal receiveAmount;

    /**
     * 收款到账金额
     */
    private BigDecimal receiptAmount;

    /**
     * 收款地址
     */
    private String receiveAddress;

    /**
     * 收银台地址
     */
    private String cashierAddress;

    /**
     * 交易状态,0 初始状态,预订单  4 订单关闭 5 待付款 6 付款成功 7 付款失败 8 多付成功 9 少付成功
     */
    private String tradeStatus;

    /**
     * 对账状态 0 未对账 1 已对账 2 对账失败
     */
    private String checkStatus;

    /**
     * 账户入账状态
     */
    private String isAccount;

    /**
     * 订单手续费金额
     */
    private BigDecimal orderFee;

    /**
     * 订单支付成功时间
     */
    private Date successTime;

    /**
     * 订单关闭时间
     */
    private Date closeTime;

    /**
     * 对账时间
     */
    private Date checkTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    private static final long serialVersionUID = 1L;

}