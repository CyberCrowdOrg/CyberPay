package org.cyberpay.crypto.mapper;

import org.cyberpay.crypto.dto.FiatSupportDto;
import org.cyberpay.crypto.model.BaseFiatCoin;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 基础法币 Mapper 接口
 * </p>
 *
 * @author gengchaonan
 * @since 2022-09-26
 */
public interface BaseFiatCoinMapper extends MyBatisBaseDao<BaseFiatCoin,Long> {

    List<Map<String,String>> selectBasicExchangeRateCoinList();

    List<FiatSupportDto> queryFiatSupportList();

    BaseFiatCoin findFiatCoin(String fiatCoinId, String fiatCoinSymbol);
}
