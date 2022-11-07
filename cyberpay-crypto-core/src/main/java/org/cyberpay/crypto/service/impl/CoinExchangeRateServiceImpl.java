package org.cyberpay.crypto.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.cyberpay.crypto.constant.RedisCacheKeyConstants;
import org.cyberpay.crypto.dto.FiatRateDto;
import org.cyberpay.crypto.enums.CoinExchangeRateSourceEnum;
import org.cyberpay.crypto.enums.CoinExchangeRateTypeEnum;
import org.cyberpay.crypto.mapper.BaseCryptoCoinMapper;
import org.cyberpay.crypto.mapper.BaseFiatCoinMapper;
import org.cyberpay.crypto.mapper.CoinExchangeRateMapper;
import org.cyberpay.crypto.model.CoinExchangeRate;
import org.cyberpay.crypto.request.LatestCryptoRateQueryReq;
import org.cyberpay.crypto.response.LatestCryptoRateQueryRes;
import org.cyberpay.crypto.service.CoinExchangeRateService;
import org.cyberpay.crypto.utils.HttpClientUtil;
import org.cyberpay.crypto.utils.HttpRequestMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service("coinExchangeRateService")
public class CoinExchangeRateServiceImpl implements CoinExchangeRateService {

    private Logger logger = LoggerFactory.getLogger(CoinExchangeRateServiceImpl.class);

    @Value("${fiatcoin.rate-query-url}")
    private String fiatCoinRateQueryUrl;
    @Value("${fiatcoin.api-key}")
    private String fiatCoinRateApiKey;
    @Value("${cryptocoin.cyptocompare-rate-query-url}")
    private String cryptoCompareRateQueryUrl;

    @Autowired
    BaseFiatCoinMapper baseFiatCoinMapper;
    @Autowired
    BaseCryptoCoinMapper baseCryptoCoinMapper;
    @Autowired
    CoinExchangeRateMapper coinExchangeRateMapper;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Override
    public void updateFiatRate(){
        logger.info("法币汇率更新任务,开始...");
        //查询法币列表
        List<Map<String, String>> basicFiatCoinList = baseFiatCoinMapper.selectBasicExchangeRateCoinList();
        if(null != basicFiatCoinList && basicFiatCoinList.size() >0){
            String symbols = getFiatSymbols(basicFiatCoinList);
            for(Map<String,String> fiatCoinMap:basicFiatCoinList){
                String fiatSymbol = fiatCoinMap.get("fiatSymbol");
                //发送请求查询汇率
                FiatRateDto fiatRateDto = sendFiatRateRequest(fiatSymbol,symbols);
                //法币汇率更新处理
                fiatRateUpdateHandle(fiatSymbol,fiatRateDto);
            }
        }
        logger.info("法币汇率更新任务,结束...");
    }

    @Override
    public LatestCryptoRateQueryRes queryLatestCryptoRate(LatestCryptoRateQueryReq latestCryptoRateQueryReq) {
        LatestCryptoRateQueryRes latestCryptoRateQueryRes = new LatestCryptoRateQueryRes();
        logger.info("最新加密币汇率查询接口,请求入参:{}", JSON.toJSONString(latestCryptoRateQueryReq));
        String fromSymbol = latestCryptoRateQueryReq.getFromSymbol();
        String toSymbol = latestCryptoRateQueryReq.getToSymbol();
        //查询Redis缓存
        String cacheValue = getLatestCryptoRateCache();
        if(StringUtils.hasText(cacheValue)){
            logger.info("最新加密币汇率查询接口,命中缓存数据...");
            //缓存数据
            Map<String,Map<String,BigDecimal>> cryptoRateMaps = JSONObject.parseObject(cacheValue,Map.class);
            Map<String, BigDecimal> rateMaps = cryptoRateMaps.get(fromSymbol);
            BigDecimal realRate = rateMaps.get(toSymbol);

            latestCryptoRateQueryRes.setFromSymbol(fromSymbol);
            latestCryptoRateQueryRes.setToSymbol(toSymbol);
            latestCryptoRateQueryRes.setRealRete(realRate);
        }else{
            //没有缓存，主动去查
            StringBuffer symbols = new StringBuffer();
            //加密币
            List<Map<String, String>> basicCryptoCoinList = baseCryptoCoinMapper.selectBasicExchangeRateCoinList();
            if(null != basicCryptoCoinList && basicCryptoCoinList.size() >0){
                symbols.append(getCryptoSymbols(basicCryptoCoinList));
            }
            //法币
            List<Map<String, String>> basicFiatCoinList = baseFiatCoinMapper.selectBasicExchangeRateCoinList();
            if(null != basicFiatCoinList && basicFiatCoinList.size() >0){
                symbols.append(","+getFiatSymbols(basicFiatCoinList));
            }
            //查询汇率
            Map<String,Map<String,BigDecimal>> cryptoRateMaps = sendCryptoRateRequest(symbols);
            //缓存汇率
            setLatestCryptoRateCache(JSON.toJSONString(cryptoRateMaps));

            Map<String, BigDecimal> rateMaps = cryptoRateMaps.get(fromSymbol);
            BigDecimal realRate = rateMaps.get(toSymbol);

            latestCryptoRateQueryRes.setFromSymbol(fromSymbol);
            latestCryptoRateQueryRes.setToSymbol(toSymbol);
            latestCryptoRateQueryRes.setRealRete(realRate);
        }
        logger.info("最新加密币汇率查询接口,响应结果:{}",JSON.toJSONString(latestCryptoRateQueryRes));
        return latestCryptoRateQueryRes;
    }

    @Override
    public void updateRateSourceBinance() {

    }

    @Override
    public void updateRateSourceChainLink() {

    }


    private void fiatRateUpdateHandle(String fiatSymbol, FiatRateDto fiatRateDto) {
        if(null != fiatRateDto && fiatRateDto.isSuccess()){
            Map<String, BigDecimal> ratesMap = fiatRateDto.getRates();
            if(null != ratesMap && ratesMap.size() >0){
                for(String fiatSymbol2:ratesMap.keySet()){
                    //查询是否存在数据
                    CoinExchangeRate coinExchangeRate = coinExchangeRateMapper.findExchangeRate(fiatSymbol, fiatSymbol2, CoinExchangeRateSourceEnum.APILAYER.getCode());
                    //保存或者更新数据
                    saveOrUpdateCoinExchangeRate(fiatSymbol,fiatSymbol2, CoinExchangeRateTypeEnum.CTOC.getCode(),
                            CoinExchangeRateSourceEnum.APILAYER.getCode(),coinExchangeRate,ratesMap.get(fiatSymbol2));
                }
            }
        }
    }

    private void saveOrUpdateCoinExchangeRate(String fromSymbol, String toSymbol,String exchangeType,
                                              String exchangeSource, CoinExchangeRate coinExchangeRate,
                                              BigDecimal rate) {
        if(null == coinExchangeRate){
            coinExchangeRate = new CoinExchangeRate();
            coinExchangeRate.setFromSymbol(fromSymbol);
            coinExchangeRate.setToSymbol(toSymbol);
            coinExchangeRate.setExchangeRate(rate);
            coinExchangeRate.setExchangeType(exchangeType);
            coinExchangeRate.setExchangeSource(exchangeSource);
            coinExchangeRate.setCreateTime(LocalDateTime.now());
            coinExchangeRate.setUpdateTime(LocalDateTime.now());
            coinExchangeRateMapper.insert(coinExchangeRate);
        }else {
            coinExchangeRate.setExchangeRate(rate);
            coinExchangeRate.setExchangeSource(exchangeSource);
            coinExchangeRate.setUpdateTime(LocalDateTime.now());
            coinExchangeRateMapper.updateByPrimaryKey(coinExchangeRate);
        }
    }

    private FiatRateDto sendFiatRateRequest(String fiatSymbol, String symbols) {
        String url = MessageFormat.format(fiatCoinRateQueryUrl,fiatSymbol,symbols);

        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("apikey", fiatCoinRateApiKey);
        String result = HttpClientUtil.sendHttp(HttpRequestMethod.HttpRequestMethodEnum.HttpGet, url, null, headerMap);
        if(StringUtils.hasText(result)){
            return JSONObject.parseObject(result,FiatRateDto.class);
        }
        return null;
    }

    private Map<String, Map<String, BigDecimal>> sendCryptoRateRequest(StringBuffer symbols) {
        String url = MessageFormat.format(cryptoCompareRateQueryUrl,symbols,symbols);
        String result = HttpClientUtil.sendHttp(HttpRequestMethod.HttpRequestMethodEnum.HttpGet, url, null, null);
        if(StringUtils.hasText(result)){
            return JSONObject.parseObject(result,Map.class);
        }
        return null;
    }

    private String getFiatSymbols(List<Map<String, String>> basicFiatCoinList) {
        StringBuffer symbols = new StringBuffer();
        for(int i=0;i<basicFiatCoinList.size();i++){
            Map<String,String> fiatCoinMap = basicFiatCoinList.get(i);
            if(i > 0){
                symbols.append(",");
            }
            symbols.append(fiatCoinMap.get("fiatSymbol"));
        }
        return symbols.toString();
    }

    private String getCryptoSymbols(List<Map<String, String>> basicCryptoCoinList) {
        StringBuffer symbols = new StringBuffer();
        for(int i=0;i<basicCryptoCoinList.size();i++){
            Map<String,String> fiatCoinMap = basicCryptoCoinList.get(i);
            if(i > 0){
                symbols.append(",");
            }
            symbols.append(fiatCoinMap.get("cryptoSymbol"));
        }
        return symbols.toString();
    }

    private String getLatestCryptoRateCache() {
        String key = RedisCacheKeyConstants.CRYPTO_RATE_CACHE_KEY;
        Object value = redisTemplate.opsForValue().get(key);
        if(null != value && !"".equals(value)){
            return (String) value;
        }
        return null;
    }

    private void setLatestCryptoRateCache(String value) {
        String key = RedisCacheKeyConstants.CRYPTO_RATE_CACHE_KEY;
//        redisTemplate.opsForValue().set(key,value,5, TimeUnit.SECONDS);
        redisTemplate.opsForValue().set(key,value,10, TimeUnit.MINUTES);
    }
}
