package org.cyberpay.trade.mapper;

import org.cyberpay.trade.model.CryptoTradeOrder;

/**
 * CryptoTradeOrderMapper继承基类
 */
public interface CryptoTradeOrderMapper extends MyBatisBaseDao<CryptoTradeOrder,Long> {

    CryptoTradeOrder findCryptoTradeOrder(String orderNo);

    CryptoTradeOrder findCryptoTradeOrderByTradeFlowNo(String tradeFlowNo);
}