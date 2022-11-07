package org.cyberpay.crypto.service;

import org.cyberpay.crypto.request.LatestCryptoRateQueryReq;
import org.cyberpay.crypto.response.LatestCryptoRateQueryRes;

public interface CoinExchangeRateService {

    void updateFiatRate();

    LatestCryptoRateQueryRes queryLatestCryptoRate(LatestCryptoRateQueryReq latestCryptoRateQueryReq);

    void updateRateSourceBinance();

    void updateRateSourceChainLink();
}
