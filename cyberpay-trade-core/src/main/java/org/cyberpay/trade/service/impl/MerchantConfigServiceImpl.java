package org.cyberpay.trade.service.impl;

import org.cyberpay.trade.dto.MerchantFeeConfigDto;
import org.cyberpay.trade.enums.CoinTypeEnum;
import org.cyberpay.trade.enums.MerchantFeeStatusEnum;
import org.cyberpay.trade.enums.MerchantFeeTypeEnum;
import org.cyberpay.trade.enums.MerchantSupportCoinStatusEnum;
import org.cyberpay.trade.mapper.MerchantFeeConfigMapper;
import org.cyberpay.trade.mapper.MerchantFloatingRateConfigMapper;
import org.cyberpay.trade.mapper.MerchantSupportCoinMapper;
import org.cyberpay.trade.model.MerchantFeeConfig;
import org.cyberpay.trade.model.MerchantFloatingRateConfig;
import org.cyberpay.trade.model.MerchantSupportCoin;
import org.cyberpay.trade.service.MerchantConfigService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service("merchantConfigService")
public class MerchantConfigServiceImpl implements MerchantConfigService {

    @Autowired
    MerchantFloatingRateConfigMapper merchantFloatingRateConfigMapper;
    @Autowired
    MerchantSupportCoinMapper merchantSupportCoinMapper;
    @Autowired
    MerchantFeeConfigMapper merchantFeeConfigMapper;

    @Override
    public BigDecimal findMerchantFloatingRate(String merchantId, String coin) {
        MerchantFloatingRateConfig merchantFloatingRateConfig =
                merchantFloatingRateConfigMapper.findMerchantFloatingRateConfig(merchantId, coin);
        if(null != merchantFloatingRateConfig && "1".equals(merchantFloatingRateConfig.getStatus())){
            return merchantFloatingRateConfig.getFloatingRate();
        }
        return BigDecimal.ZERO;
    }

    @Override
    public boolean merchantSupportCoinStatus(String merchantId, String coin, CoinTypeEnum coinTypeEnum) {
        MerchantSupportCoin merchantSupportCoinReq = new MerchantSupportCoin();
        merchantSupportCoinReq.setMerchantId(merchantId);
        merchantSupportCoinReq.setCoinName(coin);
        merchantSupportCoinReq.setCoinType(coinTypeEnum.getCode());
        merchantSupportCoinReq.setStatus(MerchantSupportCoinStatusEnum.enable.getCode());
        MerchantSupportCoin merchantSupportCoin = merchantSupportCoinMapper.selectOne(merchantSupportCoinReq);
        if(null != merchantSupportCoin){
            return true;
        }
        return false;
    }

    @Override
    public MerchantFeeConfigDto findMerchantFee(String merchantId, String coin, String channelCode,
                                                CoinTypeEnum coinTypeEnum, MerchantFeeTypeEnum merchantFeeTypeEnum) {
        MerchantFeeConfig merchantFeeReq = new MerchantFeeConfig();
        merchantFeeReq.setMerchantId(merchantId);
        merchantFeeReq.setCoinName(coin);
        merchantFeeReq.setCoinType(coinTypeEnum.getCode());
        merchantFeeReq.setChannelCode(channelCode);
        merchantFeeReq.setFeeType(merchantFeeTypeEnum.getCode());
        merchantFeeReq.setFeeStatus(MerchantFeeStatusEnum.enable.getCode());
        MerchantFeeConfig merchantFee = merchantFeeConfigMapper.findMerchantFee(merchantFeeReq);
        if(null != merchantFee){
            MerchantFeeConfigDto merchantFeeConfigDto = new MerchantFeeConfigDto();
            BeanUtils.copyProperties(merchantFee,merchantFeeConfigDto);
            return merchantFeeConfigDto;
        }
        return null;
    }
}
