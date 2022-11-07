package org.cyberpay.crypto.service;

import org.cyberpay.crypto.constant.RedisCacheKeyConstants;
import org.cyberpay.crypto.mapper.CryptoNodeMappingMapper;
import org.cyberpay.crypto.model.CryptoNodeMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service("cryptoNodeMappingService")
public class CryptoNodeMappingService {

    private Logger logger = LoggerFactory.getLogger(CryptoNodeMappingService.class);

    @Autowired
    CryptoNodeMappingMapper cryptoNodeMappingMapper;

    @Cacheable(value = RedisCacheKeyConstants.REDIS_CACHE_PREFIX + ":CRYPTO_NODE_MAPPING_CACHE", key = "#coin + '['+#networkCode+']' ")
    public CryptoNodeMapping findNodeMapping(String coin,String networkCode) {
        return cryptoNodeMappingMapper.findNodeMapping(coin, networkCode);
    }
}
