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
import java.util.*;

@Component(value = "ethereumNodeInterface")
public class EthereumNodeInterface extends BaseNodeFunction implements NodeInterface {

    @Value("${wallet.file-path.ethereum}")
    private String walletFilePath;
    @Value("${node.gas-limit.ethereum}")
    private String gasLimitStr;
    @Value("${node.get-block.ethereum-url}")
    private String ethereumNodeUrl;


    private Logger logger = LoggerFactory.getLogger(EthereumNodeInterface.class);

    @Override
    public NodeResponseDto generateAddress(NodeRequestDto nodeRequestDto) {
        NodeResponseDto nodeResponseDto = new NodeResponseDto();
        try {
            File walletDirectory = new File(walletFilePath);
            if(!walletDirectory.exists() && !walletDirectory.isDirectory()){
                //不存在目录,创建目录
                walletDirectory.mkdir();
            }
            // 第一个参数是密码,第二个参数是钱包存放的路径
            Bip39Wallet wallet = WalletUtils.generateBip39Wallet("", walletDirectory);
            // 生成12个单词的助记词
            String memorizingWords = wallet.getMnemonic();
            // 通过钱包密码与助记词获得钱包地址、公钥及私钥信息
            Credentials credentials222 = WalletUtils.loadBip39Credentials("", wallet.getMnemonic());
            String address = credentials222.getAddress();
            String publicKey = credentials222.getEcKeyPair().getPublicKey().toString(16);

            BigInteger privkeyBi = credentials222.getEcKeyPair().getPrivateKey();
            BigInteger bi = new BigInteger(privkeyBi.toString());
            String privKey = Numeric.toHexStringWithPrefix(bi);

            logger.info("address = " + address);
            logger.info("publicKey = " + publicKey);
            logger.info("memorizingWords = " + memorizingWords);
            logger.info("privKey = " + privKey);

            nodeResponseDto.setPrivateKey(privKey);
            nodeResponseDto.setPublicKey(publicKey);
            nodeResponseDto.setAddress(address);
            nodeResponseDto.setMnemonics(memorizingWords);
            nodeResponseDto.setWalletFilePath(wallet.getFilename());
        }catch (Exception e){
            logger.error("ethereum 钱包生成接口执行异常:{}",e.getMessage(),e);
            nodeResponseDto.setReturnCodeEnum(ReturnCodeEnum.FAIL,null);
        }
        return nodeResponseDto;
    }

    @Override
    public NodeResponseDto queryBalance(NodeRequestDto nodeRequestDto) {
        NodeResponseDto nodeResponseDto = new NodeResponseDto();
        logger.info("ethereum 余额查询接口,请求入参:{}",JSON.toJSONString(nodeRequestDto));

        String address = nodeRequestDto.getAddress();
        String contractAddress = nodeRequestDto.getContractAddress();
        Long tokenDecimal = nodeRequestDto.getTokenDecimal();
        Long coinDecimal = nodeRequestDto.getCoinDecimal();
        String chainType = nodeRequestDto.getChainType();

        ChainNodeReqDto chainNodeReqDto = new ChainNodeReqDto();
        chainNodeReqDto.setMethod(EthereumMethodEnum.GET_BALANCE.getCode());
        Object [] params = new Object[]{address,"latest"};
        chainNodeReqDto.setParams(params);
        String balanceResult = HttpClientUtil.postJson(ethereumNodeUrl, JSON.toJSONString(chainNodeReqDto), this.headers());
        ChainNodeResDto ethBalanceRes = JSON.parseObject(balanceResult,ChainNodeResDto.class);

        if(null != ethBalanceRes.getError()){
            ChainNodeResDto.ErrorBean error = ethBalanceRes.getError();
            logger.info("ethereum 余额查询失败:{}",JSON.toJSONString(error));
            nodeResponseDto.setReturnCodeEnum(ReturnCodeEnum.FAIL,error.getMessage());
            return nodeResponseDto;
        }

        BigInteger ethBalance = Numeric.decodeQuantity((String)ethBalanceRes.getResult());
        BigDecimal ethbalance = Convert.fromWei(ethBalance.toString(), Convert.Unit.ETHER);
        nodeResponseDto.setBalance(ethbalance);

        if("token".equals(chainType)){
             Transaction transaction = Transaction.createEthCallTransaction(address,contractAddress,FunctionEncoder
                    .encode(new Function("balanceOf", Arrays.<Type>asList(new Address(address)),
                            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {
                            }))));
            params = new Object[]{transaction,"latest"};
            chainNodeReqDto.setParams(params);
            chainNodeReqDto.setMethod(EthereumMethodEnum.GET_CALL.getCode());
            String tokenBalanceResult = HttpClientUtil.postJson(ethereumNodeUrl, JSON.toJSONString(chainNodeReqDto), this.headers());

            ChainNodeResDto tokenBalanceRes = JSON.parseObject(tokenBalanceResult,ChainNodeResDto.class);
            if(null != tokenBalanceRes.getError()){
                ChainNodeResDto.ErrorBean error = tokenBalanceRes.getError();
                logger.info("ethereum token余额查询失败:{}",JSON.toJSONString(error));
                nodeResponseDto.setReturnCodeEnum(ReturnCodeEnum.FAIL,error.getMessage());
                return nodeResponseDto;
            }
            String balanceHex = (String) tokenBalanceRes.getResult();
            BigInteger tokenBalance = new BigInteger("0");
            if (balanceHex.length() > 3) {
                tokenBalance = Numeric.toBigInt(balanceHex);
            }
            BigDecimal tokenbalance = new BigDecimal(tokenBalance).divide(new BigDecimal(tokenDecimal),coinDecimal.intValue(),BigDecimal.ROUND_HALF_DOWN);
            nodeResponseDto.setTokenBalance(tokenbalance);
        }
        return nodeResponseDto;
    }

    @Override
    public NodeResponseDto sendTransaction(NodeRequestDto nodeRequestDto) {
        NodeResponseDto nodeResponseDto = new NodeResponseDto();
        logger.info("ethereum 转账交易,请求入参:{}",JSON.toJSONString(nodeRequestDto));
        String fromAddress = nodeRequestDto.getFromAddress();
        String toAddress = nodeRequestDto.getToAddress();
        String privateKey = nodeRequestDto.getFromPrivateKey();
        //合约地址
        String contractAddress = nodeRequestDto.getContractAddress();
        Long tokenDecimal = nodeRequestDto.getTokenDecimal();
        Long coinDecimal = nodeRequestDto.getCoinDecimal();

        try {
            BigInteger transferAmount = nodeRequestDto.getTransferAmount().multiply(new BigDecimal(tokenDecimal)).toBigInteger();
            //获取转账nonce
            BigInteger nonce = getTransactionNonce(fromAddress);
            //获得gas价格
            BigInteger gasPrice = getGasPrice();
            BigInteger gasLimit = new BigInteger(gasLimitStr);
            //获取chainId
            Long chainId = getChainId();

            if (null == nonce || null == gasPrice || null == chainId) {
                //返回异常
                logger.info("ethereum 转账交易,查询Nonce或者GasPrice失败");
                nodeResponseDto.setReturnCodeEnum(ReturnCodeEnum.FAIL, "查询Nonce或者GasPrice失败");
                return nodeResponseDto;
            }
            //离线签名交易
            String signHexValue = transactionSign(privateKey,
                    fromAddress,
                    toAddress,
                    contractAddress,
                    transferAmount, chainId, nonce, gasPrice, gasLimit);

            ChainNodeResDto sendRawTransactionRes = sendRawTransaction(signHexValue);
            if (null != sendRawTransactionRes.getError()) {
                ChainNodeResDto.ErrorBean error = sendRawTransactionRes.getError();
                logger.info("ethereum 转账交易,发送已签名交易失败:{}", JSON.toJSONString(error));
                nodeResponseDto.setReturnCodeEnum(ReturnCodeEnum.FAIL, error.getMessage());
                return nodeResponseDto;
            }
            //返回交易hash
            nodeResponseDto.setTransHash((String) sendRawTransactionRes.getResult());
        }catch (Exception e){
            logger.error("ethereum 转账交易,执行异常:{}",e.getMessage(),e);
            nodeResponseDto.setReturnCodeEnum(ReturnCodeEnum.FAIL,"ethereum 转账交易异常");
        }
        logger.info("ethereum 转账交易,响应结果:{}",JSON.toJSONString(nodeResponseDto));
        return nodeResponseDto;
    }

    private Long getChainId() {
        //查询chainId
        ChainNodeReqDto chainNodeReqDto = new ChainNodeReqDto();
        chainNodeReqDto.setMethod(EthereumMethodEnum.GET_CHAINID.getCode());
        String sendRawTransactionResult = HttpClientUtil.postJson(ethereumNodeUrl, JSON.toJSONString(chainNodeReqDto), this.headers());
        ChainNodeResDto chainNodeResDto = JSON.parseObject(sendRawTransactionResult,ChainNodeResDto.class);
        if(null != chainNodeResDto.getError()){
            ChainNodeResDto.ErrorBean error = chainNodeResDto.getError();
            logger.info("ethereum 转账接口,查询ChainId失败:{}",JSON.toJSONString(error));
            return null;
        }
        return Long.valueOf(Numeric.toBigInt(chainNodeResDto.getResult().toString()).longValue());
    }

    @Override
    public NodeResponseDto confirmTransaction(NodeRequestDto nodeRequestDto) {
        NodeResponseDto nodeResponseDto = new NodeResponseDto();
        String transHash = nodeRequestDto.getTransHash();
        //获取转账nonce
        ChainNodeReqDto chainNodeReqDto = new ChainNodeReqDto();
        chainNodeReqDto.setMethod(EthereumMethodEnum.GET_TRANSACTION_RECEIPT.getCode());
        Object[] params = new Object[]{transHash};
        chainNodeReqDto.setParams(params);

        String transactionCountResult = HttpClientUtil.postJson(ethereumNodeUrl,JSON.toJSONString(chainNodeReqDto),this.headers());
        ChainNodeResDto transactionRes = JSON.parseObject(transactionCountResult,ChainNodeResDto.class);
        if(null != transactionRes.getError()){
            ChainNodeResDto.ErrorBean error = transactionRes.getError();
            logger.info("ethereum 交易详情查询失败:{}",JSON.toJSONString(error));
            nodeResponseDto.setReturnCodeEnum(ReturnCodeEnum.FAIL,error.getMessage());
            return nodeResponseDto;
        }

        TransactionReceipt receipt = JSON.parseObject(transactionRes.getResult().toString(),TransactionReceipt.class);
        String status = receipt.getStatus();

        if("0x1".equals(status)){
            BigInteger blockNumber = receipt.getBlockNumber();
            BigDecimal gasPrice = new BigDecimal(Numeric.decodeQuantity(receipt.getEffectiveGasPrice()));
            BigInteger gasLimit = receipt.getGasUsed();
            BigDecimal fee = Convert.fromWei(gasPrice.multiply(new BigDecimal(gasLimit)), Convert.Unit.ETHER);
            String blockHash = receipt.getBlockHash();
            String transactionHash = receipt.getTransactionHash();
            String fromAddress = receipt.getFrom();
            List<Log> logs = receipt.getLogs();

            BigInteger latestBlockNumber = getLatestBlockNumber();
            nodeResponseDto.setStatus(true);
            nodeResponseDto.setTransHash(transactionHash);
            nodeResponseDto.setBlockHash(blockHash);
            nodeResponseDto.setConfirm(latestBlockNumber.subtract(blockNumber).longValue());
            nodeResponseDto.setFromAddress(fromAddress);
            nodeResponseDto.setFee(fee);
            logger.info("ethereum 交易确认接口,响应结果:{}",JSON.toJSONString(nodeResponseDto));
        }
        return nodeResponseDto;
    }

    private BigInteger getTransactionNonce(String address){
        //获取转账nonce
        ChainNodeReqDto chainNodeReqDto = new ChainNodeReqDto();
        chainNodeReqDto.setMethod(EthereumMethodEnum.GET_TRANSACTION_COUNT.getCode());
        Object[] params = new Object[]{address,"latest"};
        chainNodeReqDto.setParams(params);

        String transactionCountResult = HttpClientUtil.postJson(ethereumNodeUrl,JSON.toJSONString(chainNodeReqDto),this.headers());
        ChainNodeResDto transactionCountRes = JSON.parseObject(transactionCountResult,ChainNodeResDto.class);
        if(null != transactionCountRes.getError()){
            ChainNodeResDto.ErrorBean error = transactionCountRes.getError();
            logger.info("ethereum 交易nonce查询失败:{}",JSON.toJSONString(error));
            return null;
        }
        return Numeric.decodeQuantity((String)transactionCountRes.getResult());
    }

    private BigInteger getGasPrice() {
        //获取转账nonce
        ChainNodeReqDto chainNodeReqDto = new ChainNodeReqDto();
        chainNodeReqDto.setMethod(EthereumMethodEnum.GET_GAS_PRICE.getCode());
        String gasPriceResult = HttpClientUtil.postJson(ethereumNodeUrl,JSON.toJSONString(chainNodeReqDto),this.headers());

        ChainNodeResDto gasPriceRes = JSON.parseObject(gasPriceResult,ChainNodeResDto.class);
        if(null != gasPriceRes.getError()){
            ChainNodeResDto.ErrorBean error = gasPriceRes.getError();
            logger.info("ethereum 获取gas price失败:{}",JSON.toJSONString(error));
            return null;
        }
        return Numeric.decodeQuantity((String)gasPriceRes.getResult());
    }

    private BigInteger getLatestBlockNumber(){
        //获取转账nonce
        ChainNodeReqDto chainNodeReqDto = new ChainNodeReqDto();
        chainNodeReqDto.setMethod(EthereumMethodEnum.GET_BLOCKNUMBER.getCode());
        String gasPriceResult = HttpClientUtil.postJson(ethereumNodeUrl,JSON.toJSONString(chainNodeReqDto),this.headers());

        ChainNodeResDto blockNumberRes = JSON.parseObject(gasPriceResult,ChainNodeResDto.class);
        if(null != blockNumberRes.getError()){
            ChainNodeResDto.ErrorBean error = blockNumberRes.getError();
            logger.info("ethereum 获取最新区块数失败:{}",JSON.toJSONString(error));
            return null;
        }
        return Numeric.decodeQuantity((String)blockNumberRes.getResult());
    }
    private ChainNodeResDto sendRawTransaction(String signHexValue) {
        //广播交易
        ChainNodeReqDto chainNodeReqDto = new ChainNodeReqDto();
        chainNodeReqDto.setMethod(EthereumMethodEnum.SEND_RAW_TRANSACTION.getCode());
        Object[] parmas = new Object[]{signHexValue};
        chainNodeReqDto.setParams(parmas);
        String sendRawTransactionResult = HttpClientUtil.postJson(ethereumNodeUrl, JSON.toJSONString(chainNodeReqDto), this.headers());
        return JSON.parseObject(sendRawTransactionResult,ChainNodeResDto.class);
    }

    //离线交易签名
    private String transactionSign(String privateKey, String fromAddress, String toAddress,
                                   String contractAddress, BigInteger transferAmount,
                                   Long chainId,BigInteger nonce,BigInteger gasPrice,BigInteger gasLimit) {
        //根据转账私钥生产Credentials对象
        Credentials credentials = Credentials.create(privateKey);
        if(StringUtils.hasText(contractAddress)) {
            //代币转账签名
            Function function = new Function("transfer", Arrays.<Type> asList(new Address(toAddress), new Uint256(transferAmount)),
                    Collections.<TypeReference<?>> emptyList());
            RawTransaction rawTransaction = RawTransaction.createTransaction(nonce,gasPrice,gasLimit,contractAddress,FunctionEncoder.encode(function));
            //使用Credentials对象对RawTransaction对象进行签名
            byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction,chainId, credentials);
            String hexValue = Numeric.toHexString(signedMessage);
            return hexValue;
        }else {
            RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, toAddress, transferAmount, "CyberPay Transaction");//可以额外带数据
            //使用Credentials对象对RawTransaction对象进行签名
            byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction,chainId, credentials);
            String hexValue = Numeric.toHexString(signedMessage);
            return hexValue;
        }
    }
}
