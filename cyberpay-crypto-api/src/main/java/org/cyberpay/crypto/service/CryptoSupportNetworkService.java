package org.cyberpay.crypto.service;


import org.cyberpay.crypto.dto.CryptoSupportNetworkDto;

public interface CryptoSupportNetworkService {

    CryptoSupportNetworkDto findCryptoSupportNetwork(String symbol,String networkCode,String contract);

    void cacheAllContarct();

}
