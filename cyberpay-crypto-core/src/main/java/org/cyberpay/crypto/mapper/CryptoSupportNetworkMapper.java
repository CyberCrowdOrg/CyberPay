package org.cyberpay.crypto.mapper;

import org.cyberpay.crypto.dto.SupportNetworkDto;
import org.cyberpay.crypto.model.CryptoSupportNetwork;

import java.util.List;

/**
 * CryptoSupportNetworkMapper继承基类
 */
public interface CryptoSupportNetworkMapper extends MyBatisBaseDao<CryptoSupportNetwork, Long> {

    List<SupportNetworkDto> queryCryptoSupportNetworkList(String symbol);

    CryptoSupportNetwork queryAvailableCryptoSupportNetwork(String symbol,String networkCode);

    CryptoSupportNetwork findCryptoSupportNetwork(String symbol,String networkCode,String contract);
}