package org.cyberpay.account.service.impl;

import com.alibaba.fastjson2.JSON;
import org.cyberpay.account.constant.RedisCacheKeyConstants;
import org.cyberpay.account.dto.MerchantInfoDto;
import org.cyberpay.account.mapper.MerchantInfoMapper;
import org.cyberpay.account.model.MerchantInfo;
import org.cyberpay.account.service.MerchantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;

@Service("merchantService")
public class MerchantServiceImpl implements MerchantService {

    private Logger logger = LoggerFactory.getLogger(MerchantServiceImpl.class);

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    MerchantInfoMapper merchantInfoMapper;

    @Override
    public MerchantInfoDto queryMerchant(String merchantId) {

        //缓存
        String cacheValue = redisTemplate.opsForValue().get(
                MessageFormat.format(RedisCacheKeyConstants.MERCHANT_CACHE_KEY, merchantId));
        if (StringUtils.hasText(cacheValue)) {
            logger.info("商户信息查询接口,merchant id:{} 命中缓存",merchantId);
            MerchantInfoDto merchantInfoDto = JSON.parseObject(cacheValue,MerchantInfoDto.class);
            return merchantInfoDto;
        } else{
            logger.info("商户信息查询接口,merchant id:{} 数据库查询",merchantId);
            MerchantInfo merchantInfo = merchantInfoMapper.queryMerchant(merchantId);
            if (null != merchantInfo) {
                logger.info("商户信息查询接口,merchant id:{} 数据库查询结果:{}",
                        merchantId,JSON.toJSONString(merchantInfo));
                MerchantInfoDto merchantInfoDto = new MerchantInfoDto();
                BeanUtils.copyProperties(merchantInfo, merchantInfoDto);
                redisTemplate.opsForValue().set(
                        MessageFormat.format(RedisCacheKeyConstants.MERCHANT_CACHE_KEY, merchantId)
                        ,JSON.toJSONString(merchantInfoDto),5, TimeUnit.MINUTES);
                return merchantInfoDto;
            }
        }
        return null;
    }


    @Override
    @Cacheable(value = RedisCacheKeyConstants.REDIS_CACHE_PREFIX, key = "'MERCHANT_PUBLIC_KEY'")
    public String queryMerchantPublicKey(String merchantId) {
        return merchantInfoMapper.queryMerchantPublicKey(merchantId);
    }

    @Override
    public String queryMerchantStatus(String merchantId) {
        return merchantInfoMapper.queryMerchantStatus(merchantId);
    }
}
