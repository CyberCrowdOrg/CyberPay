package org.cyberpay.account.mapper;

import org.cyberpay.account.model.MerchantInfo;

/**
 * MerchantInfoMapper继承基类
 */
public interface MerchantInfoMapper extends MyBatisBaseDao<MerchantInfo,Long> {

    MerchantInfo queryMerchant(String merchantId);

    String queryMerchantPublicKey(String merchantId);

    String queryMerchantStatus(String merchantId);
}