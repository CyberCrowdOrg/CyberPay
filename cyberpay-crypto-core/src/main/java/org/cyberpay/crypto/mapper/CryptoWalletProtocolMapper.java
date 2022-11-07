package org.cyberpay.crypto.mapper;

import org.cyberpay.crypto.model.CryptoWalletProtocol;

import java.util.List;

/**
 * <p>
 * 加密币钱包协议 Mapper 接口
 * </p>
 *
 * @author gengchaonan
 * @since 2022-09-26
 */
public interface CryptoWalletProtocolMapper extends MyBatisBaseDao<CryptoWalletProtocol,Long> {

    List<CryptoWalletProtocol> queryCryptoWalletProtocol(String cryptoSymbol);
}
