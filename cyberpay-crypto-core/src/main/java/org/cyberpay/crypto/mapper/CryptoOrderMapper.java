package org.cyberpay.crypto.mapper;

import org.cyberpay.crypto.dto.CryptoUnconfirmedOrderDto;
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

    /**
     * 查询待关闭订单(初始状态、未支付状态订单)
     * @param networkCode
     * @param endTime
     * @return
     */
    List<CryptoOrder> queryPendingCloseOrders(String networkCode, Date endTime);

    List<CryptoUnconfirmedOrderDto> findCryptoUnconfirmedOrder(String networkCode);

}