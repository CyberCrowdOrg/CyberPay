package org.cyberpay.crypto.service;

import org.cyberpay.crypto.dto.BaseCryptoCoinDto;
import org.cyberpay.crypto.dto.CryptoSupportDto;

import java.util.List;

public interface BaseCryptoCoinService {

    List<CryptoSupportDto> queryCryptoSupportList();

    BaseCryptoCoinDto findCryptoCoin(String cryptoCoinId, String cryptoCoinSymbol);

    boolean isSupportCoin(String coin);

}
