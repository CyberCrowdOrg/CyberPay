package org.cyberpay.crypto.mapper;

import org.cyberpay.crypto.model.CryptoOrderExtend;

import java.util.List;

/**
 * CryptoOrderExtendMapper继承基类
 */
public interface CryptoOrderExtendMapper extends MyBatisBaseDao<CryptoOrderExtend, Long> {

    CryptoOrderExtend findCryptoOrderExtend(String bizFlowNo);

    Integer updateCryptoOrderExtendByBizFlowNo(CryptoOrderExtend cryptoOrderExtend);

    /**
     * 查找未确认订单扩展数据
     * @param coin
     * @param networkCode
     * @return
     */
    List<CryptoOrderExtend> findUnconfirmedOrderExtend(String coin, String networkCode);
}