package org.cyberpay.crypto.service;

import org.cyberpay.crypto.dto.BaseFiatCoinDto;
import org.cyberpay.crypto.dto.FiatSupportDto;

import java.util.List;

public interface BaseFiatCoinService {

    List<FiatSupportDto> queryFiatSupportList();

    BaseFiatCoinDto findFiatCoin(String fiatCoinId, String fiatCoinSymbol);
}
