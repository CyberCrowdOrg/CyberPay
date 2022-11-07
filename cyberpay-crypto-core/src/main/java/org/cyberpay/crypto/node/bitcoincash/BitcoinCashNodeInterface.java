package org.cyberpay.crypto.node.bitcoincash;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.script.Script;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.KeyChainGroupStructure;
import org.bitcoinj.wallet.Wallet;
import org.bouncycastle.util.encoders.Hex;
import org.cyberpay.crypto.node.BaseNodeFunction;
import org.cyberpay.crypto.node.NodeInterface;
import org.cyberpay.crypto.node.NodeRequestDto;
import org.cyberpay.crypto.node.NodeResponseDto;
import org.cyberpay.crypto.node.ethereum.EthereumNodeInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component(value = "bitcoinCashNodeInterface")
public class BitcoinCashNodeInterface extends BaseNodeFunction implements NodeInterface {

    @Value("${node.get-block.bitcoin-cash-url}")
    private String bitcoinCashNodeUrl;
    @Value("${node.balance-query.bitcoin-cash}")
    private String bitcoinCashBalanceQueryUrl;

    @Value("spring.profiles.active")
    private String active;


    private Logger logger = LoggerFactory.getLogger(EthereumNodeInterface.class);

    @Override
    public NodeResponseDto generateAddress(NodeRequestDto nodeRequestDto) {

        NetworkParameters networkParameter = TestNet3Params.get();
        DeterministicSeed seed = new DeterministicSeed(new SecureRandom(), 128, "");
        Wallet wallet = Wallet.fromSeed(networkParameter, seed, Script.ScriptType.P2PKH,
                KeyChainGroupStructure.DEFAULT.accountPathFor(Script.ScriptType.P2PKH));
        //私钥
        String privateKey = wallet.currentReceiveKey().getPrivateKeyAsWiF(networkParameter);
        //助记词
        String mnemonics = wallet.getKeyChainSeed().getMnemonicCode().toString();
        String publicKey = Hex.toHexString(ECKey.publicKeyFromPrivate(wallet.currentReceiveKey().getPrivKey(), true));
        //地址
        Address address = wallet.currentReceiveAddress();

        logger.info("私钥:{}",privateKey);
        logger.info("公钥:{}",publicKey);
        logger.info("地址:{}",address.toString());
        logger.info("助记词:{}",mnemonics);
        return null;
    }

    @Override
    public NodeResponseDto queryBalance(NodeRequestDto nodeRequestDto) {
        return null;
    }

    @Override
    public NodeResponseDto sendTransaction(NodeRequestDto nodeRequestDto) {
        return null;
    }

    @Override
    public NodeResponseDto confirmTransaction(NodeRequestDto nodeRequestDto) {
        return null;
    }
}
