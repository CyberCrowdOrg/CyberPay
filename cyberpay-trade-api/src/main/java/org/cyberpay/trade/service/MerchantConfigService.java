package org.cyberpay.trade.service;

import org.cyberpay.trade.dto.MerchantFeeConfigDto;
import org.cyberpay.trade.enums.CoinTypeEnum;
import org.cyberpay.trade.enums.MerchantFeeTypeEnum;

import java.math.BigDecimal;

public interface MerchantConfigService {

    BigDecimal findMerchantFloatingRate(String merchantId,String coin);

    boolean merchantSupportCoinStatus(String merchantId,String coin,CoinTypeEnum coinTypeEnum);

    MerchantFeeConfigDto findMerchantFee(String merchantId, String coin, String channelCode,
                                         CoinTypeEnum coinTypeEnum, MerchantFeeTypeEnum merchantFeeTypeEnum);
}
