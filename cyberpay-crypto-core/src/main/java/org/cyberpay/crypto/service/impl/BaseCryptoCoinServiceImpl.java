package org.cyberpay.crypto.service.impl;

import com.alibaba.fastjson2.JSON;
import org.cyberpay.crypto.constant.RedisCacheKeyConstants;
import org.cyberpay.crypto.dto.BaseCryptoCoinDto;
import org.cyberpay.crypto.dto.CryptoSupportDto;
import org.cyberpay.crypto.dto.SupportNetworkDto;
import org.cyberpay.crypto.mapper.BaseCryptoCoinMapper;
import org.cyberpay.crypto.mapper.BaseFiatCoinMapper;
import org.cyberpay.crypto.mapper.CryptoSupportNetworkMapper;
import org.cyberpay.crypto.model.BaseCryptoCoin;
import org.cyberpay.crypto.model.BaseFiatCoin;
import org.cyberpay.crypto.service.BaseCryptoCoinService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service("baseCryptoCoinService")
public class BaseCryptoCoinServiceImpl implements BaseCryptoCoinService {

    private Logger logger = LoggerFactory.getLogger(BaseCryptoCoinServiceImpl.class);

    @Autowired
    BaseCryptoCoinMapper baseCryptoCoinMapper;
    @Autowired
    BaseFiatCoinMapper baseFiatCoinMapper;
    @Autowired
    CryptoSupportNetworkMapper cryptoSupportNetworkMapper;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Override
    public List<CryptoSupportDto> queryCryptoSupportList() {
        List<CryptoSupportDto> cryptoSupportDtoList = null;
        //查询缓存
        String cachValue = redisTemplate.opsForValue().get(RedisCacheKeyConstants.CRYPTO_SUPPORT_LIST_CACHE_KEY);
        if(StringUtils.hasText(cachValue)){
            cryptoSupportDtoList = JSON.parseArray(cachValue,CryptoSupportDto.class);
        }else {
            cryptoSupportDtoList = baseCryptoCoinMapper.queryCryptoSupportList();
            //查询币种支持的网络数据,保存到列表数据中
            if(null != cryptoSupportDtoList && cryptoSupportDtoList.size() >0){
                setCryptoSupportNetwork(cryptoSupportDtoList);
            }
            redisTemplate.opsForValue().set(RedisCacheKeyConstants.CRYPTO_SUPPORT_LIST_CACHE_KEY,
                    JSON.toJSONString(cryptoSupportDtoList),30, TimeUnit.MINUTES);
        }
        return cryptoSupportDtoList;
    }

    private void setCryptoSupportNetwork(List<CryptoSupportDto> cryptoSupportList) {
        for(CryptoSupportDto cryptoSupportDto:cryptoSupportList){
            cryptoSupportDto.setSupportNetworks(
                    cryptoSupportNetworkMapper.queryCryptoSupportNetworkList(
                            cryptoSupportDto.getSymbol()));
        }
    }

    @Override
    public BaseCryptoCoinDto findCryptoCoin(String cryptoCoinId, String cryptoCoinSymbol) {
        BaseCryptoCoin cryptoCoin = baseCryptoCoinMapper.findCryptoCoin(cryptoCoinId, cryptoCoinSymbol);
        if(null != cryptoCoin){
            BaseCryptoCoinDto baseCryptoCoinDto = new BaseCryptoCoinDto();
            BeanUtils.copyProperties(cryptoCoin,baseCryptoCoinDto);
            return baseCryptoCoinDto;
        }
        return null;
    }

    @Override
    public boolean isSupportCoin(String coin) {
        logger.info("加密币支付,交易币种支持查询 coin:{}",coin);
        //缓存结果
        String cacheResult = redisTemplate.opsForValue().get(
                MessageFormat.format(RedisCacheKeyConstants.CRYPTO_SUPPORT_COIN_RESULT_CACHE_KEY, coin));

        if(StringUtils.hasText(cacheResult)){
            logger.info("加密币支付,交易币种支持查询 coin:{} 是否支持: true");
            return true;
        }else {
            BaseCryptoCoin cryptoCoin =
                    baseCryptoCoinMapper.findCryptoCoin(null, coin);
            if (null != cryptoCoin) {
                redisTemplate.opsForValue().set(
                        MessageFormat.format(RedisCacheKeyConstants.CRYPTO_SUPPORT_COIN_RESULT_CACHE_KEY, coin),
                        "true",10,TimeUnit.MINUTES);
                logger.info("加密币支付,交易币种支持查询 coin:{} 是否支持: true");
                return true;
            } else {
                BaseFiatCoin fiatCoin = baseFiatCoinMapper.findFiatCoin(null, coin);
                if (null != fiatCoin) {
                    redisTemplate.opsForValue().set(
                            MessageFormat.format(RedisCacheKeyConstants.CRYPTO_SUPPORT_COIN_RESULT_CACHE_KEY, coin),
                            "true",10,TimeUnit.MINUTES);
                    logger.info("加密币支付,交易币种支持查询 coin:{} 是否支持: true");
                    return true;
                }
            }
        }
        logger.info("加密币支付,交易币种支持查询 coin:{} 是否支持: false");
        return false;
    }
}
