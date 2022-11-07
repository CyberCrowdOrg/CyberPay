package org.cyberpay.account.service;

import org.cyberpay.account.dto.MerchantInfoDto;

public interface MerchantService {

    /**
     * 查询商户信息
     * @param merchantId
     * @return
     */
    MerchantInfoDto queryMerchant(String merchantId);

    /**
     * 查询商户公钥
     * @param merchantId
     * @return
     */
    String queryMerchantPublicKey(String merchantId);

    /**
     * 查询商户状态
     * @param merchantId
     * @return
     */
    String queryMerchantStatus(String merchantId);

}
