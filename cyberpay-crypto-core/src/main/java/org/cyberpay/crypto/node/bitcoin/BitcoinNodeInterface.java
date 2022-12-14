package org.cyberpay.crypto.node.bitcoin;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.bitcoinj.core.*;
import org.bitcoinj.crypto.TransactionSignature;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.KeyChainGroupStructure;
import org.bitcoinj.wallet.Wallet;
import org.bouncycastle.util.encoders.Hex;
import org.cyberpay.crypto.enums.BitcoinMethodEnum;
import org.cyberpay.crypto.enums.CoinDecimalEnum;
import org.cyberpay.crypto.enums.ReturnCodeEnum;
import org.cyberpay.crypto.node.*;
import org.cyberpay.crypto.utils.HttpClientUtil;
import org.cyberpay.crypto.utils.HttpRequestMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;


@Component(value = "bitcoinNodeInterface")
public class BitcoinNodeInterface extends BaseNodeFunction implements NodeInterface {

    private Logger logger = LoggerFactory.getLogger(BitcoinNodeInterface.class);

    @Value("${node.get-block.bitcoin-url}")
    private String bitcoinNodeUrl;
    @Value("${node.balance-query.bitcoin}")
    private String bitcoinBalanceQueryUrl;

    @Value("spring.profiles.active")
    private String active;

    private NetworkParameters networkParameter =
            "prod".equals(active) ? MainNetParams.get():TestNet3Params.get();



    @Override
    public NodeResponseDto generateAddress(NodeRequestDto nodeRequestDto) {
        NodeResponseDto nodeResponseDto = new NodeResponseDto();
        try {
            DeterministicSeed seed = new DeterministicSeed(new SecureRandom(), 128, "");
            Wallet wallet = Wallet.fromSeed(networkParameter, seed, Script.ScriptType.P2WPKH,
                    KeyChainGroupStructure.DEFAULT.accountPathFor(Script.ScriptType.P2WPKH));
            //??????
            String privateKey = wallet.currentReceiveKey().getPrivateKeyAsWiF(networkParameter);
            //?????????
            String mnemonics = wallet.getKeyChainSeed().getMnemonicCode().toString();
            String publicKey = Hex.toHexString(ECKey.publicKeyFromPrivate(wallet.currentReceiveKey().getPrivKey(), true));
            //??????
            Address address = wallet.currentReceiveAddress();

            logger.info("??????:{}",privateKey);
            logger.info("??????:{}",publicKey);
            logger.info("??????:{}",address.toString());
            logger.info("?????????:{}",mnemonics);

            nodeResponseDto.setPrivateKey(privateKey);
            nodeResponseDto.setPublicKey(publicKey);
            nodeResponseDto.setAddress(address.toString());
            nodeResponseDto.setMnemonics(mnemonics);

        }catch (Exception e){
            logger.error("bitcoin ????????????????????????:{}",e.getMessage(),e);
            nodeResponseDto.setReturnCodeEnum(ReturnCodeEnum.FAIL,null);
        }
        logger.info("bitcoin ????????????,????????????:{}",JSON.toJSONString(nodeResponseDto));
        return nodeResponseDto;
    }

    @Override
    public NodeResponseDto queryBalance(NodeRequestDto nodeRequestDto) {
        NodeResponseDto nodeResponseDto = new NodeResponseDto();
        logger.info("bitcoin ??????????????????,????????????:{}",JSON.toJSONString(nodeRequestDto));
        String address = nodeRequestDto.getAddress();

        try {
            String url = MessageFormat.format(bitcoinBalanceQueryUrl,address);
            String balanceResult = HttpClientUtil.sendHttp(HttpRequestMethod.HttpRequestMethodEnum.HttpGet, url, null, null);
            JSONObject jsonObject = JSON.parseObject(balanceResult);
            Long balance = Long.valueOf(jsonObject.get("confirmed").toString());
            BigDecimal balanceBd = BigDecimal.ZERO;
            if(balance.intValue() > 0) {
                balanceBd = CoinDecimalEnum.converToBigDecimal(balance, CoinDecimalEnum.BTC);
            }
            nodeResponseDto.setBalance(balanceBd);
        }catch (Exception e){
            logger.error("bitcoin ??????????????????,????????????:{}",e.getMessage(),e);
            nodeResponseDto.setReturnCodeEnum(ReturnCodeEnum.FAIL,null);
        }
        logger.info("bitcoin ??????????????????,????????????:{}",JSON.toJSONString(nodeResponseDto));
        return nodeResponseDto;
    }

    @Override
    public NodeResponseDto sendTransaction(NodeRequestDto nodeRequestDto) {
        NodeResponseDto nodeResponseDto = new NodeResponseDto();
        logger.info("bitcoin ????????????,????????????:{}",JSON.toJSONString(nodeRequestDto));
        try {
            String fromAddress = nodeRequestDto.getFromAddress();
            String fromPrivateKey = nodeRequestDto.getFromPrivateKey();
            String toAddress = nodeRequestDto.getToAddress();
            //???????????????
            BigDecimal feeRateBigDecimal = queryFeeRate("BTC");
            Long feeRate = CoinDecimalEnum.converToLong(feeRateBigDecimal,CoinDecimalEnum.BTC);
            //????????????
            long transferAmount = CoinDecimalEnum.converToLong(nodeRequestDto.getTransferAmount(),CoinDecimalEnum.BTC);
            //?????????????????????
            List<UTXO> utxoList = getUnspentList(fromAddress,"BTC");
            //????????????
            String sign = transactionMessageSign(fromAddress,
                    toAddress, fromPrivateKey, transferAmount, feeRate, utxoList);
            //?????????????????????
            ChainNodeReqDto chainNodeReqDto = new ChainNodeReqDto();
            chainNodeReqDto.setMethod(BitcoinMethodEnum.SEND_RAW_TRANSACTION.getCode());
            Object [] params = new Object[]{sign,feeRateBigDecimal.multiply(new BigDecimal(100).setScale(8,BigDecimal.ROUND_HALF_DOWN))};
            chainNodeReqDto.setParams(params);
            String transactionResult = HttpClientUtil.postJson(bitcoinNodeUrl, JSON.toJSONString(chainNodeReqDto), this.headers());
            ChainNodeResDto transactionResDto = JSON.parseObject(transactionResult,ChainNodeResDto.class);
            if(null != transactionResDto.getError()){
                ChainNodeResDto.ErrorBean error = transactionResDto.getError();
                logger.info("bitcoin ????????????,????????????:{}",JSON.toJSONString(error));
                nodeResponseDto.setReturnCodeEnum(ReturnCodeEnum.FAIL,error.getMessage());
                return nodeResponseDto;
            }
            nodeResponseDto.setTransHash(transactionResDto.getResult().toString());
            nodeResponseDto.setStatus(true);
        }catch (Exception e){
            logger.error("bitcoin ????????????,????????????:{}",e.getMessage(),e);
            nodeResponseDto.setReturnCodeEnum(ReturnCodeEnum.FAIL,null);
            return nodeResponseDto;
        }
        logger.info("bitcoin ????????????,????????????:{}",JSON.toJSONString(nodeResponseDto));
        return nodeResponseDto;
    }

    @Override
    public NodeResponseDto confirmTransaction(NodeRequestDto nodeRequestDto) {
        NodeResponseDto nodeResponseDto = new NodeResponseDto();
        logger.info("bitcoin ????????????????????????,????????????:{}",JSON.toJSONString(nodeRequestDto));
        try {
            //f36991dc2674639c242b72bc625a664200145a21f2fb837ef53a89b0b14459d4
            String transHash = nodeRequestDto.getTransHash();
            //????????????????????????
            ChainNodeReqDto chainNodeReqDto = new ChainNodeReqDto();
            chainNodeReqDto.setMethod(BitcoinMethodEnum.GET_RAW_TRANSACTION.getCode());
            Object[] params = new Object[]{transHash,true};
            chainNodeReqDto.setParams(params);

            String getRawTransactionResult = HttpClientUtil.postJson(bitcoinNodeUrl, JSON.toJSONString(chainNodeReqDto), this.headers());
            ChainNodeResDto rawTransactionResDto = JSON.parseObject(getRawTransactionResult,ChainNodeResDto.class);

            if(null != rawTransactionResDto.getError()){
                ChainNodeResDto.ErrorBean error = rawTransactionResDto.getError();
                logger.info("bitcoin ????????????????????????,????????????????????????:{}",JSON.toJSONString(error));
                nodeResponseDto.setReturnCodeEnum(ReturnCodeEnum.FAIL,error.getMessage());
                return nodeResponseDto;
            }

            JSONObject jsonObject = JSONObject.parseObject(rawTransactionResDto.getResult().toString());
            List<JSONObject> voutJsonObject = JSON.parseArray(jsonObject.get("vout").toString(),JSONObject.class);
            JSONObject inJsonObject = voutJsonObject.get(0);
            JSONObject outJsonObject = voutJsonObject.get(1);

            String txid = (String)jsonObject.get("txid");
            String blockhash = (String) jsonObject.get("blockhash");
            long confirm = null != jsonObject.get("confirmations") ? Long.valueOf(jsonObject.get("confirmations").toString()):0l;
            //????????????
            BigDecimal receiveAmount = new BigDecimal(inJsonObject.get("value").toString());
            //????????????
            String receiveAddress = (String) JSONObject.parseObject(inJsonObject.get("scriptPubKey").toString()).get("address");
            //????????????
            String paymentAddress = (String) JSONObject.parseObject(outJsonObject.get("scriptPubKey").toString()).get("address");

            nodeResponseDto.setTransHash(txid);
            nodeResponseDto.setConfirm(confirm);
            nodeResponseDto.setReceiptAmount(receiveAmount);
            nodeResponseDto.setFromAddress(paymentAddress);
            nodeResponseDto.setToAddress(receiveAddress);
            nodeResponseDto.setBlockHash(blockhash);
            logger.info("bitcoin ????????????????????????,????????????:{}", JSON.toJSONString(rawTransactionResDto));
        }catch (Exception e){
            logger.error("bitcoin ????????????????????????,????????????:{}",e.getMessage(),e);
            nodeResponseDto.setReturnCodeEnum(ReturnCodeEnum.FAIL,null);
            return nodeResponseDto;
        }
        return nodeResponseDto;
    }

    public String transactionMessageSign(String from,String to,String fromPrk, long amount, long fee,List<UTXO> utxos){
        if (utxos.size() == 0) {
            throw new RuntimeException("utxo?????? BTC INSUFFICIENT FEE");
        }
        //?????????????????????UTXO
        long utxoAmount = 0L;
        List<UTXO> needUtxos = new ArrayList<UTXO>();
        //???????????????????????????????????????item
        for (UTXO utxo : utxos) {
            if (utxoAmount >= (amount + fee)) {
                break;
            } else {
                needUtxos.add(utxo);
                utxoAmount += utxo.getValue().value;
            }
        }
        //?????? ??????????????????????????????546??? ???????????????????????????
        long changeAmount = utxoAmount - (amount + fee);
        if (changeAmount < 0L) {
            logger.info("BTC???????????????????????? ??????={}, ??????={}", from, utxoAmount);
            throw new RuntimeException("BTC INSUFFICIENT FEE");
        }

        //BTC????????????
        Transaction tx = new Transaction(networkParameter);
        //???????????????????????????
        for (UTXO utxo : needUtxos) {
            //bc1???????????? ???P2WPKH ???????????????????????? Script
            if (getAddressType(networkParameter, from).equals(Script.ScriptType.P2WPKH)) {
                tx.addInput(utxo.getHash(), utxo.getIndex(), new Script(Hex.decode("")));
            } else {
                tx.addInput(utxo.getHash(), utxo.getIndex(), utxo.getScript());
            }
        }
        //??????????????????
        //????????????
        tx.addOutput(Coin.valueOf(amount), Address.fromString(networkParameter, to));
        if (changeAmount >= 546L) {
            //??????
            tx.addOutput(Coin.valueOf(changeAmount), Address.fromString(networkParameter, from));
        }
        //???????????????16????????????
        String txHex = Hex.toHexString(tx.bitcoinSerialize());

        tx = new Transaction(networkParameter, Hex.decode(txHex));
        DumpedPrivateKey dumpedPrivateKey = DumpedPrivateKey.fromBase58(networkParameter, fromPrk);
        ECKey ecKey = dumpedPrivateKey.getKey();

        //???????????????????????????
        int size = needUtxos.size();
        for (int i = 0; i < size; i++) {
            if (getAddressType(networkParameter, from).equals(Script.ScriptType.P2PKH)) {
                //1???????????? ???????????? ???????????????
                tx.clearInputs();
                TransactionOutPoint outPoint = new TransactionOutPoint(networkParameter, needUtxos.get(i).getIndex(), needUtxos.get(i).getHash());
                tx.addSignedInput(outPoint, needUtxos.get(i).getScript(), ecKey, Transaction.SigHash.ALL, true);
            } else if (getAddressType(networkParameter, from).equals(Script.ScriptType.P2SH)) {
                //3?????? ???????????????????????? ???????????????
                TransactionInput txIn = tx.getInput(i);
                Script redeemScript = ScriptBuilder.createP2WPKHOutputScript(ecKey);
                Script witnessScript = ScriptBuilder.createP2PKHOutputScript(ecKey);
                TransactionSignature txSig = tx.calculateWitnessSignature(
                        i,
                        ecKey,
                        witnessScript,
                        needUtxos.get(i).getValue(),
                        Transaction.SigHash.ALL,
                        false
                );
                txIn.setWitness(TransactionWitness.redeemP2WPKH(txSig, ecKey));
                txIn.setScriptSig(new ScriptBuilder().data(redeemScript.getProgram()).build());
            } else if (getAddressType(networkParameter, from).equals(Script.ScriptType.P2WPKH)) {
                //bc1?????? ???????????????????????? ???????????????
                TransactionInput txIn = tx.getInput(i);
                Script witnessScript = ScriptBuilder.createP2PKHOutputScript(ecKey);
                TransactionSignature txSig = tx.calculateWitnessSignature(
                        i,
                        ecKey,
                        witnessScript,
                        needUtxos.get(i).getValue(),
                        Transaction.SigHash.ALL,
                        false
                );
                txIn.setWitness(TransactionWitness.redeemP2WPKH(txSig, ecKey));
            } else if (getAddressType(networkParameter, from).equals(Script.ScriptType.P2WSH)) {
                //TODO ??????????????????
                throw new RuntimeException("???????????????????????????");
            } else {
                //??????
                throw new RuntimeException("???????????????????????????");
            }
        }
        String txSigned = Hex.toHexString(tx.bitcoinSerialize());
        return txSigned;
    }

    /**
     * ??????????????????
     *
     * @param networkParameters ????????????
     * @param address           ??????
     * @return
     */
    public Script.ScriptType getAddressType(NetworkParameters networkParameters, String address) {
        try {
            return Address.fromString(networkParameters, address).getOutputScriptType();
        } catch (Exception e) {
            logger.info("?????????????????? address={}", address);
            throw new RuntimeException("??????????????????");
        }
    }
}
