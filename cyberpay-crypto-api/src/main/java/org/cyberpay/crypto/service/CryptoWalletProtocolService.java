package org.cyberpay.crypto.service;

import org.cyberpay.crypto.dto.CryptoWalletProtocolDto;

import java.util.List;


public interface CryptoWalletProtocolService {

    List<CryptoWalletProtocolDto> queryCryptoWalletProtocol(String cryptoSymbol);
}
