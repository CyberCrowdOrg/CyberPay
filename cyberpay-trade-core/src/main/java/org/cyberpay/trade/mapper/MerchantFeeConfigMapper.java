package org.cyberpay.trade.mapper;

import org.cyberpay.trade.model.MerchantFeeConfig;

/**
 * MerchantFeeConfigMapper继承基类
 */
public interface MerchantFeeConfigMapper extends MyBatisBaseDao<MerchantFeeConfig, Long> {

    MerchantFeeConfig findMerchantFee(MerchantFeeConfig merchantFeeReq);
}