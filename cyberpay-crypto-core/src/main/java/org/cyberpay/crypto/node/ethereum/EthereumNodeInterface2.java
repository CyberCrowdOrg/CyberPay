package org.cyberpay.crypto.node.ethereum;

import com.alibaba.fastjson2.JSON;
import org.cyberpay.crypto.enums.EthereumMethodEnum;
import org.cyberpay.crypto.enums.ReturnCodeEnum;
import org.cyberpay.crypto.node.*;
import org.cyberpay.crypto.utils.HttpClientUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.*;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

//@Component(value = "ethereumNodeInterface")
public class EthereumNodeInterface2 extends BaseNodeFunction implements NodeInterface {
    @Override
    public NodeResponseDto generateAddress(NodeRequestDto nodeRequestDto) {
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

//    @Value("${wallet.file-path.ethereum}")
//    private String walletFilePath;
//    @Value("${node.gas-limit.ethereum}")
//    private String gasLimitStr;
//    @Value("${node.get-block.ethereum-url}")
//    private String ethereumNodeUrl;
//
//
//    private Logger logger = LoggerFactory.getLogger(EthereumNodeInterface2.class);
//
//    @Override
//    public NodeResponseDto generateAddress(NodeRequestDto nodeRequestDto) {
//        NodeResponseDto nodeResponseDto = new NodeResponseDto();
//        try {
//            File walletDirectory = new File(walletFilePath);
//            if(!walletDirectory.exists() && !walletDirectory.isDirectory()){
//                //???????????????,????????????
//                walletDirectory.mkdir();
//            }
//            // ????????????????????????,???????????????????????????????????????
//            Bip39Wallet wallet = WalletUtils.generateBip39Wallet("", walletDirectory);
//            // ??????12?????????????????????
//            String memorizingWords = wallet.getMnemonic();
//            // ????????????????????????????????????????????????????????????????????????
//            Credentials credentials222 = WalletUtils.loadBip39Credentials("", wallet.getMnemonic());
//            String address = credentials222.getAddress();
//            String publicKey = credentials222.getEcKeyPair().getPublicKey().toString(16);
//
//            BigInteger privkeyBi = credentials222.getEcKeyPair().getPrivateKey();
//            BigInteger bi = new BigInteger(privkeyBi.toString());
//            String privKey = Numeric.toHexStringWithPrefix(bi);
//
//            logger.info("address = " + address);
//            logger.info("publicKey = " + publicKey);
//            logger.info("memorizingWords = " + memorizingWords);
//            logger.info("privKey = " + privKey);
//
//            nodeResponseDto.setPrivateKey(privKey);
//            nodeResponseDto.setPublicKey(publicKey);
//            nodeResponseDto.setAddress(address);
//            nodeResponseDto.setMnemonics(memorizingWords);
//            nodeResponseDto.setWalletFilePath(wallet.getFilename());
//        }catch (Exception e){
//            logger.error("ethereum ??????????????????????????????:{}",e.getMessage(),e);
//            nodeResponseDto.setReturnCodeEnum(ReturnCodeEnum.FAIL,null);
//        }
//        return nodeResponseDto;
//    }
//
//    @Override
//    public NodeResponseDto queryBalance(NodeRequestDto nodeRequestDto) {
//        NodeResponseDto nodeResponseDto = new NodeResponseDto();
//        logger.info("ethereum ??????????????????,????????????:{}",JSON.toJSONString(nodeRequestDto));
//
//        String address = nodeRequestDto.getAddress();
//        String contractAddress = nodeRequestDto.getContractAddress();
//        Long tokenDecimal = nodeRequestDto.getTokenDecimal();
//        Long coinDecimal = nodeRequestDto.getCoinDecimal();
//        String chainType = nodeRequestDto.getChainType();
//
//        ChainNodeReqDto chainNodeReqDto = new ChainNodeReqDto();
//        chainNodeReqDto.setMethod(EthereumMethodEnum.GET_BALANCE.getCode());
//        Object [] params = new Object[]{address,"latest"};
//        chainNodeReqDto.setParams(params);
//        String balanceResult = HttpClientUtil.postJson(ethereumNodeUrl, JSON.toJSONString(chainNodeReqDto), this.headers());
//        ChainNodeResDto ethBalanceRes = JSON.parseObject(balanceResult,ChainNodeResDto.class);
//
//        if(null != ethBalanceRes.getError()){
//            ChainNodeResDto.ErrorBean error = ethBalanceRes.getError();
//            logger.info("ethereum ??????????????????:{}",JSON.toJSONString(error));
//            nodeResponseDto.setReturnCodeEnum(ReturnCodeEnum.FAIL,error.getMessage());
//            return nodeResponseDto;
//        }
//
//        BigInteger ethBalance = Numeric.decodeQuantity((String)ethBalanceRes.getResult());
//        BigDecimal ethbalance = Convert.fromWei(ethBalance.toString(), Convert.Unit.ETHER);
//        nodeResponseDto.setBalance(ethbalance);
//
//        if("token".equals(chainType)){
//             Transaction transaction = Transaction.createEthCallTransaction(address,contractAddress,FunctionEncoder
//                    .encode(new Function("balanceOf", Arrays.<Type>asList(new Address(address)),
//                            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {
//                            }))));
//            params = new Object[]{transaction,"latest"};
//            chainNodeReqDto.setParams(params);
//            chainNodeReqDto.setMethod(EthereumMethodEnum.GET_CALL.getCode());
//            String tokenBalanceResult = HttpClientUtil.postJson(ethereumNodeUrl, JSON.toJSONString(chainNodeReqDto), this.headers());
//
//            ChainNodeResDto tokenBalanceRes = JSON.parseObject(tokenBalanceResult,ChainNodeResDto.class);
//            if(null != tokenBalanceRes.getError()){
//                ChainNodeResDto.ErrorBean error = tokenBalanceRes.getError();
//                logger.info("ethereum token??????????????????:{}",JSON.toJSONString(error));
//                nodeResponseDto.setReturnCodeEnum(ReturnCodeEnum.FAIL,error.getMessage());
//                return nodeResponseDto;
//            }
//            String balanceHex = (String) tokenBalanceRes.getResult();
//            BigInteger tokenBalance = new BigInteger("0");
//            if (balanceHex.length() > 3) {
//                tokenBalance = Numeric.toBigInt(balanceHex);
//            }
//            BigDecimal tokenbalance = new BigDecimal(tokenBalance).divide(new BigDecimal(tokenDecimal),coinDecimal.intValue(),BigDecimal.ROUND_HALF_DOWN);
//            nodeResponseDto.setTokenBalance(tokenbalance);
//        }
//        return nodeResponseDto;
//    }
//
//    @Override
//    public NodeResponseDto sendTransaction(NodeRequestDto nodeRequestDto) {
//        NodeResponseDto nodeResponseDto = new NodeResponseDto();
//        logger.info("ethereum ????????????,????????????:{}",JSON.toJSONString(nodeRequestDto));
//        String fromAddress = nodeRequestDto.getFromAddress();
//        String toAddress = nodeRequestDto.getToAddress();
//        String privateKey = nodeRequestDto.getFromPrivateKey();
//        //????????????
//        String contractAddress = nodeRequestDto.getContractAddress();
//        Long tokenDecimal = nodeRequestDto.getTokenDecimal();
//        Long coinDecimal = nodeRequestDto.getCoinDecimal();
//
//        try {
//            BigInteger transferAmount = nodeRequestDto.getTransferAmount().multiply(new BigDecimal(tokenDecimal)).toBigInteger();
//            //????????????nonce
//            BigInteger nonce = getTransactionNonce(fromAddress);
//            //??????gas??????
//            BigInteger gasPrice = getGasPrice();
//            BigInteger gasLimit = new BigInteger(gasLimitStr);
//            //??????chainId
//            Long chainId = getChainId();
//
//            if (null == nonce || null == gasPrice || null == chainId) {
//                //????????????
//                logger.info("ethereum ????????????,??????Nonce??????GasPrice??????");
//                nodeResponseDto.setReturnCodeEnum(ReturnCodeEnum.FAIL, "??????Nonce??????GasPrice??????");
//                return nodeResponseDto;
//            }
//            //??????????????????
//            String signHexValue = transactionSign(privateKey,
//                    fromAddress,
//                    toAddress,
//                    contractAddress,
//                    transferAmount, chainId, nonce, gasPrice, gasLimit);
//
//            ChainNodeResDto sendRawTransactionRes = sendRawTransaction(signHexValue);
//            if (null != sendRawTransactionRes.getError()) {
//                ChainNodeResDto.ErrorBean error = sendRawTransactionRes.getError();
//                logger.info("ethereum ????????????,???????????????????????????:{}", JSON.toJSONString(error));
//                nodeResponseDto.setReturnCodeEnum(ReturnCodeEnum.FAIL, error.getMessage());
//                return nodeResponseDto;
//            }
//            //????????????hash
//            nodeResponseDto.setTransHash((String) sendRawTransactionRes.getResult());
//        }catch (Exception e){
//            logger.error("ethereum ????????????,????????????:{}",e.getMessage(),e);
//            nodeResponseDto.setReturnCodeEnum(ReturnCodeEnum.FAIL,"ethereum ??????????????????");
//        }
//        logger.info("ethereum ????????????,????????????:{}",JSON.toJSONString(nodeResponseDto));
//        return nodeResponseDto;
//    }
//
//    private Long getChainId() {
//        //??????chainId
//        ChainNodeReqDto chainNodeReqDto = new ChainNodeReqDto();
//        chainNodeReqDto.setMethod(EthereumMethodEnum.GET_CHAINID.getCode());
//        String sendRawTransactionResult = HttpClientUtil.postJson(ethereumNodeUrl, JSON.toJSONString(chainNodeReqDto), this.headers());
//        ChainNodeResDto chainNodeResDto = JSON.parseObject(sendRawTransactionResult,ChainNodeResDto.class);
//        if(null != chainNodeResDto.getError()){
//            ChainNodeResDto.ErrorBean error = chainNodeResDto.getError();
//            logger.info("ethereum ????????????,??????ChainId??????:{}",JSON.toJSONString(error));
//            return null;
//        }
//        return Long.valueOf(Numeric.toBigInt(chainNodeResDto.getResult().toString()).longValue());
//    }
//
//    @Override
//    public NodeResponseDto confirmTransaction(NodeRequestDto nodeRequestDto) {
//        NodeResponseDto nodeResponseDto = new NodeResponseDto();
//        String transHash = nodeRequestDto.getTransHash();
//        //????????????nonce
//        ChainNodeReqDto chainNodeReqDto = new ChainNodeReqDto();
//        chainNodeReqDto.setMethod(EthereumMethodEnum.GET_TRANSACTION_RECEIPT.getCode());
//        Object[] params = new Object[]{transHash};
//        chainNodeReqDto.setParams(params);
//
//        String transactionCountResult = HttpClientUtil.postJson(ethereumNodeUrl,JSON.toJSONString(chainNodeReqDto),this.headers());
//        ChainNodeResDto transactionRes = JSON.parseObject(transactionCountResult,ChainNodeResDto.class);
//        if(null != transactionRes.getError()){
//            ChainNodeResDto.ErrorBean error = transactionRes.getError();
//            logger.info("ethereum ????????????????????????:{}",JSON.toJSONString(error));
//            nodeResponseDto.setReturnCodeEnum(ReturnCodeEnum.FAIL,error.getMessage());
//            return nodeResponseDto;
//        }
//
//        TransactionReceipt receipt = JSON.parseObject(transactionRes.getResult().toString(),TransactionReceipt.class);
//        String status = receipt.getStatus();
//
//        if("0x1".equals(status)){
//            BigInteger blockNumber = receipt.getBlockNumber();
//            BigDecimal gasPrice = new BigDecimal(Numeric.decodeQuantity(receipt.getEffectiveGasPrice()));
//            BigInteger gasLimit = receipt.getGasUsed();
//            BigDecimal fee = Convert.fromWei(gasPrice.multiply(new BigDecimal(gasLimit)), Convert.Unit.ETHER);
//            String blockHash = receipt.getBlockHash();
//            String transactionHash = receipt.getTransactionHash();
//            String fromAddress = receipt.getFrom();
//            List<Log> logs = receipt.getLogs();
//
//            BigInteger latestBlockNumber = getLatestBlockNumber();
//            nodeResponseDto.setStatus(true);
//            nodeResponseDto.setTransHash(transactionHash);
//            nodeResponseDto.setBlockHash(blockHash);
//            nodeResponseDto.setConfirm(latestBlockNumber.subtract(blockNumber).longValue());
//            nodeResponseDto.setFromAddress(fromAddress);
//            nodeResponseDto.setFee(fee);
//            logger.info("ethereum ??????????????????,????????????:{}",JSON.toJSONString(nodeResponseDto));
//        }
//        return nodeResponseDto;
//    }
//
//    private BigInteger getTransactionNonce(String address){
//        //????????????nonce
//        ChainNodeReqDto chainNodeReqDto = new ChainNodeReqDto();
//        chainNodeReqDto.setMethod(EthereumMethodEnum.GET_TRANSACTION_COUNT.getCode());
//        Object[] params = new Object[]{address,"latest"};
//        chainNodeReqDto.setParams(params);
//
//        String transactionCountResult = HttpClientUtil.postJson(ethereumNodeUrl,JSON.toJSONString(chainNodeReqDto),this.headers());
//        ChainNodeResDto transactionCountRes = JSON.parseObject(transactionCountResult,ChainNodeResDto.class);
//        if(null != transactionCountRes.getError()){
//            ChainNodeResDto.ErrorBean error = transactionCountRes.getError();
//            logger.info("ethereum ??????nonce????????????:{}",JSON.toJSONString(error));
//            return null;
//        }
//        return Numeric.decodeQuantity((String)transactionCountRes.getResult());
//    }
//
//    private BigInteger getGasPrice() {
//        //????????????nonce
//        ChainNodeReqDto chainNodeReqDto = new ChainNodeReqDto();
//        chainNodeReqDto.setMethod(EthereumMethodEnum.GET_GAS_PRICE.getCode());
//        String gasPriceResult = HttpClientUtil.postJson(ethereumNodeUrl,JSON.toJSONString(chainNodeReqDto),this.headers());
//
//        ChainNodeResDto gasPriceRes = JSON.parseObject(gasPriceResult,ChainNodeResDto.class);
//        if(null != gasPriceRes.getError()){
//            ChainNodeResDto.ErrorBean error = gasPriceRes.getError();
//            logger.info("ethereum ??????gas price??????:{}",JSON.toJSONString(error));
//            return null;
//        }
//        return Numeric.decodeQuantity((String)gasPriceRes.getResult());
//    }
//
//    private BigInteger getLatestBlockNumber(){
//        //????????????nonce
//        ChainNodeReqDto chainNodeReqDto = new ChainNodeReqDto();
//        chainNodeReqDto.setMethod(EthereumMethodEnum.GET_BLOCKNUMBER.getCode());
//        String gasPriceResult = HttpClientUtil.postJson(ethereumNodeUrl,JSON.toJSONString(chainNodeReqDto),this.headers());
//
//        ChainNodeResDto blockNumberRes = JSON.parseObject(gasPriceResult,ChainNodeResDto.class);
//        if(null != blockNumberRes.getError()){
//            ChainNodeResDto.ErrorBean error = blockNumberRes.getError();
//            logger.info("ethereum ???????????????????????????:{}",JSON.toJSONString(error));
//            return null;
//        }
//        return Numeric.decodeQuantity((String)blockNumberRes.getResult());
//    }
//    private ChainNodeResDto sendRawTransaction(String signHexValue) {
//        //????????????
//        ChainNodeReqDto chainNodeReqDto = new ChainNodeReqDto();
//        chainNodeReqDto.setMethod(EthereumMethodEnum.SEND_RAW_TRANSACTION.getCode());
//        Object[] parmas = new Object[]{signHexValue};
//        chainNodeReqDto.setParams(parmas);
//        String sendRawTransactionResult = HttpClientUtil.postJson(ethereumNodeUrl, JSON.toJSONString(chainNodeReqDto), this.headers());
//        return JSON.parseObject(sendRawTransactionResult,ChainNodeResDto.class);
//    }
//
//    //??????????????????
//    private String transactionSign(String privateKey, String fromAddress, String toAddress,
//                                   String contractAddress, BigInteger transferAmount,
//                                   Long chainId,BigInteger nonce,BigInteger gasPrice,BigInteger gasLimit) {
//        //????????????????????????Credentials??????
//        Credentials credentials = Credentials.create(privateKey);
//        if(StringUtils.hasText(contractAddress)) {
//            //??????????????????
//            Function function = new Function("transfer", Arrays.<Type> asList(new Address(toAddress), new Uint256(transferAmount)),
//                    Collections.<TypeReference<?>> emptyList());
//            RawTransaction rawTransaction = RawTransaction.createTransaction(nonce,gasPrice,gasLimit,contractAddress,FunctionEncoder.encode(function));
//            //??????Credentials?????????RawTransaction??????????????????
//            byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction,chainId, credentials);
//            String hexValue = Numeric.toHexString(signedMessage);
//            return hexValue;
//        }else {
//            RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, toAddress, transferAmount, "CyberPay Transaction");//?????????????????????
//            //??????Credentials?????????RawTransaction??????????????????
//            byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction,chainId, credentials);
//            String hexValue = Numeric.toHexString(signedMessage);
//            return hexValue;
//        }
//    }
}
