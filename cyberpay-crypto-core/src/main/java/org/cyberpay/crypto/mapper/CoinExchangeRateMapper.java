package org.cyberpay.crypto.mapper;

import org.apache.ibatis.annotations.Param;
import org.cyberpay.crypto.model.CoinExchangeRate;

import java.util.List;

/**
 * <p>
 * 币种汇率 Mapper 接口
 * </p>
 *
 * @author gengchaonan
 * @since 2022-09-26
 */
public interface CoinExchangeRateMapper extends MyBatisBaseDao<CoinExchangeRate,Long> {

    CoinExchangeRate findExchangeRate(@Param("fromSymbol") String fromSymbol,@Param("toSymbol")  String toSymbol,@Param("source") String source);

    List<CoinExchangeRate> findExchangeRateList(String fromSymbol,String toSymbol);
}
