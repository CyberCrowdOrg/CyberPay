package org.cyberpay.crypto.mapper;

import org.cyberpay.crypto.model.CryptoNodeMapping;

/**
 * CryptoNodeMappingMapper继承基类
 */
public interface CryptoNodeMappingMapper extends MyBatisBaseDao<CryptoNodeMapping, Long> {

    CryptoNodeMapping findNodeMapping(String symbol,String networkCode);
}