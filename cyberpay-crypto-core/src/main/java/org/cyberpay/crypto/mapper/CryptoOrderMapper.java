package org.cyberpay.crypto.mapper;

import org.cyberpay.crypto.model.CryptoOrder;

import java.util.Date;
import java.util.List;

/**
 * CryptoOrderMapper继承基类
 */
public interface CryptoOrderMapper extends MyBatisBaseDao<CryptoOrder, Long> {

    CryptoOrder findOrder(String orderNo);

    CryptoOrder findRefundOrderByOrgOrderNo(String orgOrderNo);

    CryptoOrder selectCryptoOrder(CryptoOrder cryptoOrder);

    List<CryptoOrder> queryPendingCloseOrders(String coin, String networkCode, Date endTime);

}