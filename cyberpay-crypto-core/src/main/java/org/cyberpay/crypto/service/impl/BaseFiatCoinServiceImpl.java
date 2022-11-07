package org.cyberpay.crypto.service.impl;

import com.alibaba.fastjson2.JSON;
import org.cyberpay.crypto.constant.RedisCacheKeyConstants;
import org.cyberpay.crypto.dto.BaseFiatCoinDto;
import org.cyberpay.crypto.dto.FiatSupportDto;
import org.cyberpay.crypto.mapper.BaseFiatCoinMapper;
import org.cyberpay.crypto.model.BaseFiatCoin;
import org.cyberpay.crypto.service.BaseFiatCoinService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service("baseFiatCoinService")
public class BaseFiatCoinServiceImpl implements BaseFiatCoinService {

    private Logger logger = LoggerFactory.getLogger(BaseFiatCoinServiceImpl.class);

    @Autowired
    BaseFiatCoinMapper baseFiatCoinMapper;

    @Autowired
    StringRedisTemplate redisTemplate;


    @Override
    public List<FiatSupportDto> queryFiatSupportList() {
        List<FiatSupportDto> fiatSupportDtoList = null;
        //查询缓存
        String cachValue = redisTemplate.opsForValue().get(RedisCacheKeyConstants.FIAT_SUPPORT_LIST_CACHE_KEY);
        if(StringUtils.hasText(cachValue)){
            fiatSupportDtoList = JSON.parseArray(cachValue,FiatSupportDto.class);
        }else {
            fiatSupportDtoList = baseFiatCoinMapper.queryFiatSupportList();
            redisTemplate.opsForValue().set(RedisCacheKeyConstants.FIAT_SUPPORT_LIST_CACHE_KEY,
                    JSON.toJSONString(fiatSupportDtoList),30, TimeUnit.MINUTES);
        }
        return fiatSupportDtoList;
    }

    @Override
    public BaseFiatCoinDto findFiatCoin(String fiatCoinId, String fiatCoinSymbol) {
        BaseFiatCoin fiatCoin = baseFiatCoinMapper.findFiatCoin(fiatCoinId, fiatCoinSymbol);
        if(null != fiatCoin){
            BaseFiatCoinDto baseFiatCoinDto = new BaseFiatCoinDto();
            BeanUtils.copyProperties(fiatCoin,baseFiatCoinDto);
            return baseFiatCoinDto;
        }
        return null;
    }
}
