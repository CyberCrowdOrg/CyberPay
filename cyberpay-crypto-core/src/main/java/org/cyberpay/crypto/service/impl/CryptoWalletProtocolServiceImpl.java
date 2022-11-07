package org.cyberpay.crypto.service.impl;

import org.cyberpay.crypto.constant.RedisCacheKeyConstants;
import org.cyberpay.crypto.dto.CryptoWalletProtocolDto;
import org.cyberpay.crypto.mapper.CryptoWalletProtocolMapper;
import org.cyberpay.crypto.model.CryptoWalletProtocol;
import org.cyberpay.crypto.service.CryptoWalletProtocolService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("cryptoWalletProtocolService")
public class CryptoWalletProtocolServiceImpl implements CryptoWalletProtocolService {

    @Autowired
    CryptoWalletProtocolMapper cryptoWalletProtocolMapper;

    @Override
    @Cacheable(value = RedisCacheKeyConstants.REDIS_CACHE_PREFIX+":CRYPTO_WALLET_PROTOCOL_CACHE" , key = "#cryptoSymbol")
    public List<CryptoWalletProtocolDto> queryCryptoWalletProtocol(String cryptoSymbol) {
        List<CryptoWalletProtocol> cryptoWalletProtocols = cryptoWalletProtocolMapper.queryCryptoWalletProtocol(cryptoSymbol);
        if(null != cryptoWalletProtocols && cryptoWalletProtocols.size() > 0){
            List<CryptoWalletProtocolDto> cryptoWalletProtocolDtos = new ArrayList<>();
            for(CryptoWalletProtocol cryptoWalletProtocol:cryptoWalletProtocols) {
                CryptoWalletProtocolDto cryptoWalletProtocolDto = new CryptoWalletProtocolDto();
                BeanUtils.copyProperties(cryptoWalletProtocol, cryptoWalletProtocolDto);
                cryptoWalletProtocolDtos.add(cryptoWalletProtocolDto);
            }
            return cryptoWalletProtocolDtos;
        }
        return null;
    }
}
