package org.cyberpay.crypto.service;

import org.cyberpay.crypto.dto.BaseCryptoCoinDto;
import org.cyberpay.crypto.dto.CryptoSupportDto;
import org.cyberpay.crypto.dto.ReceiveCryptoCoinDto;

import java.util.List;

public interface BaseCryptoCoinService {

    List<CryptoSupportDto> queryCryptoSupportList();

    ReceiveCryptoCoinDto queryReceiveCryptoCoin(String coin,String networkCode);

    BaseCryptoCoinDto findCryptoCoin(String cryptoCoinId, String cryptoCoinSymbol);

    boolean isSupportCoin(String coin);

}
