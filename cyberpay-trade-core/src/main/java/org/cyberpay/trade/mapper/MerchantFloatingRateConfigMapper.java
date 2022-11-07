package org.cyberpay.trade.mapper;

import org.cyberpay.trade.model.MerchantFloatingRateConfig;

/**
 * MerchantFloatingRateConfigMapper继承基类
 */
public interface MerchantFloatingRateConfigMapper extends MyBatisBaseDao<MerchantFloatingRateConfig, Long> {

    MerchantFloatingRateConfig findMerchantFloatingRateConfig(String merchantId,String coin);
}