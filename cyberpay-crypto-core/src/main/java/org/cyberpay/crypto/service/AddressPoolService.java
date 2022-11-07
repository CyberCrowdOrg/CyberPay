package org.cyberpay.crypto.service;

import com.alibaba.fastjson2.JSON;
import org.cyberpay.crypto.constant.RedisCacheKeyConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service("addressPoolService")
public class AddressPoolService {

    private Logger logger = LoggerFactory.getLogger(AddressPoolService.class);

    @Autowired
    StringRedisTemplate redisTemplate;

    /**
     * 分配地址
     * @param networkCode
     * @return
     */
    public String getAddressFromAddressPoolCache(String networkCode) {
        String cacheKey = MessageFormat.format(RedisCacheKeyConstants.ADDRESS_POOL_AVAILABLE_QUEUE_CACHE, networkCode);
        Set<String> rangeSet = redisTemplate.opsForZSet().range(cacheKey, -1, -1);

        if(null != rangeSet && rangeSet.size() > 0){

            for(String value:rangeSet){
                logger.info("{} 地址池地址分配,分配地址:{}",networkCode,value);
                //添加到不可用地址池缓存中
                double score = redisTemplate.opsForZSet().score(cacheKey, value);
                this.addAddressToUnAvailableAddressPool(networkCode,value,score);
                //删除可用地址池缓存数据
                this.delAddressFromAddressPoolCache(cacheKey,value);
                return value;
            }
        }
        logger.info("{} 地址池地址分配,没有可用的地址",networkCode);
        return null;
    }

    /**
     * 归还地址到地址池
     * @param networkCode
     * @param address
     * @param score
     * @return
     */
    public boolean returnAddressToAddressPoolCache(String networkCode,String address,double score){
        try {
            String cacheKey = MessageFormat.format(RedisCacheKeyConstants.ADDRESS_POOL_UNAVAILABLE_QUEUE_CACHE, networkCode);
            Double score1 = redisTemplate.opsForZSet().score(cacheKey, address);
            if (null != score1) {
                //删除不可用缓存
                delAddressFromAddressPoolCache(cacheKey, address);
            }
            //添加到可用地址池缓存
            addAddressToAvailableAddressPool(networkCode, address, score1 + score);
        }catch (Exception e){
            logger.error("归还地址到地址池,执行异常:{}",e.getMessage(),e);
            return false;
        }
        return true;
    }

    public void addAddressToUnAvailableAddressPool(String networkCode,String address,double score){
        redisTemplate.opsForZSet().add(
                MessageFormat.format(RedisCacheKeyConstants.ADDRESS_POOL_UNAVAILABLE_QUEUE_CACHE,networkCode), address, score);
    }

    public void addAddressToAvailableAddressPool(String networkCode,String address,double score){
        redisTemplate.opsForZSet().add(
                MessageFormat.format(RedisCacheKeyConstants.ADDRESS_POOL_AVAILABLE_QUEUE_CACHE,networkCode), address, score);
    }

    public void addAddressToAllAddressPoolCache(String networkCode,String address) {
        String cacheValue = redisTemplate.opsForValue().get(
                MessageFormat.format(RedisCacheKeyConstants.ADDRESS_POOL_ALL_CACHE,networkCode));
        if(StringUtils.hasText(cacheValue)){
            List<String> addressList = JSON.parseArray(cacheValue,String.class);
            addressList.add(address);
            redisTemplate.opsForValue().set(
                    MessageFormat.format(RedisCacheKeyConstants.ADDRESS_POOL_ALL_CACHE,networkCode),JSON.toJSONString(addressList));
        }else {
            List<String> addressList = new ArrayList<>();
            addressList.add(address);
            redisTemplate.opsForValue().set(
                    MessageFormat.format(RedisCacheKeyConstants.ADDRESS_POOL_ALL_CACHE,networkCode),JSON.toJSONString(addressList));
        }
    }

    private void delAddressFromAddressPoolCache(String cacheKey,String cacheValue){
        redisTemplate.opsForZSet().remove(cacheKey,cacheValue);
    }
}
