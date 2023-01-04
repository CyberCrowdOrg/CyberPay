package org.cyberpay.crypto.mapper;

import org.cyberpay.crypto.dto.CryptoSupportDto;
import org.cyberpay.crypto.dto.ReceiveCryptoCoinDto;
import org.cyberpay.crypto.model.BaseCryptoCoin;

import java.util.List;
import java.util.Map;

/**
 * BaseCryptoCoinMapper继承基类
 */
public interface BaseCryptoCoinMapper extends MyBatisBaseDao<BaseCryptoCoin, Long> {

    BaseCryptoCoin findCryptoCoin(String cryptoId,String symbol);

    List<Map<String,String>> selectBasicExchangeRateCoinList();

    List<CryptoSupportDto> queryCryptoSupportList();

    ReceiveCryptoCoinDto queryReceiveCryptoCoin(String symbol, String networkCode);
}