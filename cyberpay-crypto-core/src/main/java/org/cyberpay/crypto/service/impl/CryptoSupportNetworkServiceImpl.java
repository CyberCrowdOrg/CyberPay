package org.cyberpay.crypto.service.impl;

import com.alibaba.fastjson2.JSON;
import org.cyberpay.crypto.constant.RedisCacheKeyConstants;
import org.cyberpay.crypto.dto.CryptoSupportNetworkDto;
import org.cyberpay.crypto.mapper.CryptoSupportNetworkMapper;
import org.cyberpay.crypto.model.CryptoSupportNetwork;
import org.cyberpay.crypto.service.CryptoSupportNetworkService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;

@Service("cryptoSupportNetworkService")
public class CryptoSupportNetworkServiceImpl implements CryptoSupportNetworkService {

    private Logger logger = LoggerFactory.getLogger(CryptoSupportNetworkServiceImpl.class);

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    CryptoSupportNetworkMapper cryptoSupportNetworkMapper;

    @Override
    public CryptoSupportNetworkDto findCryptoSupportNetwork(String symbol,
                                                            String networkCode, String contract) {
        CryptoSupportNetworkDto cryptoSupportNetworkDto = null;
        //缓存
        String cacheValue = redisTemplate.opsForValue().get(
                MessageFormat.format(RedisCacheKeyConstants.CRYPTO_SUPPORT_NETWORK_CACHE,networkCode));
        if(!StringUtils.hasText(cacheValue)){
            cryptoSupportNetworkDto = new CryptoSupportNetworkDto();
            CryptoSupportNetwork cryptoSupportNetwork = cryptoSupportNetworkMapper.findCryptoSupportNetwork(symbol, networkCode, contract);
            if(null != cryptoSupportNetwork){
                BeanUtils.copyProperties(cryptoSupportNetwork,cryptoSupportNetworkDto);
                redisTemplate.opsForValue().set(
                        MessageFormat.format(RedisCacheKeyConstants.CRYPTO_SUPPORT_NETWORK_CACHE,networkCode),JSON.toJSONString(cryptoSupportNetworkDto));
                return cryptoSupportNetworkDto;
            }
        }else {
            cryptoSupportNetworkDto = JSON.parseObject(cacheValue,CryptoSupportNetworkDto.class);
            return cryptoSupportNetworkDto;
        }
        return cryptoSupportNetworkDto;
    }
}
