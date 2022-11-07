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

            nodeResponseDto.setPrivateKey(privateKey);
            nodeResponseDto.setPublicKey(publicKey);
            nodeResponseDto.setAddress(address.toString());
            nodeResponseDto.setMnemonics(mnemonics);

        }catch (Exception e){
            logger.error("bitcoin 地址生成执行异常:{}",e.getMessage(),e);
            nodeResponseDto.setReturnCodeEnum(ReturnCodeEnum.FAIL,null);
        }
        logger.info("bitcoin 地址生成,响应结果:{}",JSON.toJSONString(nodeResponseDto));
        return nodeResponseDto;
    }

    @Override
    public NodeResponseDto queryBalance(NodeRequestDto nodeRequestDto) {
        NodeResponseDto nodeResponseDto = new NodeResponseDto();
        logger.info("bitcoin 地址余额查询,请求入参:{}",JSON.toJSONString(nodeRequestDto));
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
            logger.error("bitcoin 地址余额查询,执行异常:{}",e.getMessage(),e);
            nodeResponseDto.setReturnCodeEnum(ReturnCodeEnum.FAIL,null);
        }
        logger.info("bitcoin 地址余额查询,响应结果:{}",JSON.toJSONString(nodeResponseDto));
        return nodeResponseDto;
    }

    @Override
    public NodeResponseDto sendTransaction(NodeRequestDto nodeRequestDto) {
        NodeResponseDto nodeResponseDto = new NodeResponseDto();
        logger.info("bitcoin 转账交易,请求入参:{}",JSON.toJSONString(nodeRequestDto));
        try {
            String fromAddress = nodeRequestDto.getFromAddress();
            String fromPrivateKey = nodeRequestDto.getFromPrivateKey();
            String toAddress = nodeRequestDto.getToAddress();
            //获取手续费
            BigDecimal feeRateBigDecimal = queryFeeRate("BTC");
            Long feeRate = CoinDecimalEnum.converToLong(feeRateBigDecimal,CoinDecimalEnum.BTC);
            //转账金额
            long transferAmount = CoinDecimalEnum.converToLong(nodeRequestDto.getTransferAmount(),CoinDecimalEnum.BTC);
            //获取未花费列表
            List<UTXO> utxoList = getUnspentList(fromAddress,"BTC");
            //离线签名
            String sign = transactionMessageSign(fromAddress,
                    toAddress, fromPrivateKey, transferAmount, feeRate, utxoList);
            //发送已签名交易
            ChainNodeReqDto chainNodeReqDto = new ChainNodeReqDto();
            chainNodeReqDto.setMethod(BitcoinMethodEnum.SEND_RAW_TRANSACTION.getCode());
            Object [] params = new Object[]{sign,feeRateBigDecimal.multiply(new BigDecimal(100).setScale(8,BigDecimal.ROUND_HALF_DOWN))};
            chainNodeReqDto.setParams(params);
            String transactionResult = HttpClientUtil.postJson(bitcoinNodeUrl, JSON.toJSONString(chainNodeReqDto), this.headers());
            ChainNodeResDto transactionResDto = JSON.parseObject(transactionResult,ChainNodeResDto.class);
            if(null != transactionResDto.getError()){
                ChainNodeResDto.ErrorBean error = transactionResDto.getError();
                logger.info("bitcoin 转账交易,请求失败:{}",JSON.toJSONString(error));
                nodeResponseDto.setReturnCodeEnum(ReturnCodeEnum.FAIL,error.getMessage());
                return nodeResponseDto;
            }
            nodeResponseDto.setTransHash(transactionResDto.getResult().toString());
            nodeResponseDto.setStatus(true);
        }catch (Exception e){
            logger.error("bitcoin 转账交易,执行异常:{}",e.getMessage(),e);
            nodeResponseDto.setReturnCodeEnum(ReturnCodeEnum.FAIL,null);
            return nodeResponseDto;
        }
        logger.info("bitcoin 转账交易,响应结果:{}",JSON.toJSONString(nodeResponseDto));
        return nodeResponseDto;
    }

    @Override
    public NodeResponseDto confirmTransaction(NodeRequestDto nodeRequestDto) {
        NodeResponseDto nodeResponseDto = new NodeResponseDto();
        logger.info("bitcoin 交易结果确认查询,请求入参:{}",JSON.toJSONString(nodeRequestDto));
        try {
            //f36991dc2674639c242b72bc625a664200145a21f2fb837ef53a89b0b14459d4
            String transHash = nodeRequestDto.getTransHash();
            //查询交易详细信息
            ChainNodeReqDto chainNodeReqDto = new ChainNodeReqDto();
            chainNodeReqDto.setMethod(BitcoinMethodEnum.GET_RAW_TRANSACTION.getCode());
            Object[] params = new Object[]{transHash,true};
            chainNodeReqDto.setParams(params);

            String getRawTransactionResult = HttpClientUtil.postJson(bitcoinNodeUrl, JSON.toJSONString(chainNodeReqDto), this.headers());
            ChainNodeResDto rawTransactionResDto = JSON.parseObject(getRawTransactionResult,ChainNodeResDto.class);

            if(null != rawTransactionResDto.getError()){
                ChainNodeResDto.ErrorBean error = rawTransactionResDto.getError();
                logger.info("bitcoin 交易结果确认查询,查询交易详情失败:{}",JSON.toJSONString(error));
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
            //收款金额
            BigDecimal receiveAmount = new BigDecimal(inJsonObject.get("value").toString());
            //收款地址
            String receiveAddress = (String) JSONObject.parseObject(inJsonObject.get("scriptPubKey").toString()).get("address");
            //转账地址
            String paymentAddress = (String) JSONObject.parseObject(outJsonObject.get("scriptPubKey").toString()).get("address");

            nodeResponseDto.setTransHash(txid);
            nodeResponseDto.setConfirm(confirm);
            nodeResponseDto.setReceiptAmount(receiveAmount);
            nodeResponseDto.setFromAddress(paymentAddress);
            nodeResponseDto.setToAddress(receiveAddress);
            nodeResponseDto.setBlockHash(blockhash);
            logger.info("bitcoin 交易结果确认查询,响应结果:{}", JSON.toJSONString(rawTransactionResDto));
        }catch (Exception e){
            logger.error("bitcoin 交易结果确认查询,执行异常:{}",e.getMessage(),e);
            nodeResponseDto.setReturnCodeEnum(ReturnCodeEnum.FAIL,null);
            return nodeResponseDto;
        }
        return nodeResponseDto;
    }

    public String transactionMessageSign(String from,String to,String fromPrk, long amount, long fee,List<UTXO> utxos){
        if (utxos.size() == 0) {
            throw new RuntimeException("utxo为空 BTC INSUFFICIENT FEE");
        }
        //组装转账所需的UTXO
        long utxoAmount = 0L;
        List<UTXO> needUtxos = new ArrayList<UTXO>();
        //遍历未花费列表，组装合适的item
        for (UTXO utxo : utxos) {
            if (utxoAmount >= (amount + fee)) {
                break;
            } else {
                needUtxos.add(utxo);
                utxoAmount += utxo.getValue().value;
            }
        }
        //找零 找零金额必须大于等于546聪 否则会视为粉尘攻击
        long changeAmount = utxoAmount - (amount + fee);
        if (changeAmount < 0L) {
            logger.info("BTC转账地址余额不足 地址={}, 余额={}", from, utxoAmount);
            throw new RuntimeException("BTC INSUFFICIENT FEE");
        }

        //BTC交易构建
        Transaction tx = new Transaction(networkParameter);
        //添加未签名交易输入
        for (UTXO utxo : needUtxos) {
            //bc1开头地址 即P2WPKH 交易输入不能加入 Script
            if (getAddressType(networkParameter, from).equals(Script.ScriptType.P2WPKH)) {
                tx.addInput(utxo.getHash(), utxo.getIndex(), new Script(Hex.decode("")));
            } else {
                tx.addInput(utxo.getHash(), utxo.getIndex(), utxo.getScript());
            }
        }
        //添加交易输出
        //收款地址
        tx.addOutput(Coin.valueOf(amount), Address.fromString(networkParameter, to));
        if (changeAmount >= 546L) {
            //找零
            tx.addOutput(Coin.valueOf(changeAmount), Address.fromString(networkParameter, from));
        }
        //未签名交易16进制格式
        String txHex = Hex.toHexString(tx.bitcoinSerialize());

        tx = new Transaction(networkParameter, Hex.decode(txHex));
        DumpedPrivateKey dumpedPrivateKey = DumpedPrivateKey.fromBase58(networkParameter, fromPrk);
        ECKey ecKey = dumpedPrivateKey.getKey();

        //对交易输入进行签名
        int size = needUtxos.size();
        for (int i = 0; i < size; i++) {
            if (getAddressType(networkParameter, from).equals(Script.ScriptType.P2PKH)) {
                //1开头地址 普通地址 手续费较高
                tx.clearInputs();
                TransactionOutPoint outPoint = new TransactionOutPoint(networkParameter, needUtxos.get(i).getIndex(), needUtxos.get(i).getHash());
                tx.addSignedInput(outPoint, needUtxos.get(i).getScript(), ecKey, Transaction.SigHash.ALL, true);
            } else if (getAddressType(networkParameter, from).equals(Script.ScriptType.P2SH)) {
                //3开头 隔离见证兼容地址 手续费较低
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
                //bc1开头 隔离见证原生地址 手续费最低
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
                //TODO 多重签名地址
                throw new RuntimeException("暂不支持该地址转账");
            } else {
                //其他
                throw new RuntimeException("暂不支持该地址转账");
            }
        }
        String txSigned = Hex.toHexString(tx.bitcoinSerialize());
        return txSigned;
    }

    /**
     * 校验地址类型
     *
     * @param networkParameters 网络类型
     * @param address           地址
     * @return
     */
    public Script.ScriptType getAddressType(NetworkParameters networkParameters, String address) {
        try {
            return Address.fromString(networkParameters, address).getOutputScriptType();
        } catch (Exception e) {
            logger.info("地址格式错误 address={}", address);
            throw new RuntimeException("地址格式错误");
        }
    }
}
