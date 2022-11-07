package org.cyberpay.trade.mapper;

import org.cyberpay.trade.model.MerchantSupportCoin;

/**
 * MerchantSupportCoinMapper继承基类
 */
public interface MerchantSupportCoinMapper extends MyBatisBaseDao<MerchantSupportCoin, Long> {

    MerchantSupportCoin selectOne(MerchantSupportCoin merchantSupportCoin);
}