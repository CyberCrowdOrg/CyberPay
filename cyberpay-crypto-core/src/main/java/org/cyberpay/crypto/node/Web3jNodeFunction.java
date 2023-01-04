package org.cyberpay.crypto.node;

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
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;

@Component
public class Web3jNodeFunction {

    private Logger logger = LoggerFactory.getLogger(Web3jNodeFunction.class);

    @Value("${web3j.ethereum.rpc-url}")
    private String ethereumRpcUrl;
    @Value("${web3j.arbitrum.rpc-url}")
    private String arbitrumRpcUrl;
    @Value("${web3j.optimism.rpc-url}")
    private String optimismRpcUrl;
    @Value("${web3j.zksync.rpc-url}")
    private String zksyncRpcUrl;

    public Web3j web3j;

    public void ArbitrumEnvironment(){
        web3j = Web3j.build(new HttpService(arbitrumRpcUrl));
    }

    public void OptimismEnvironment(){
        web3j = Web3j.build(new HttpService(optimismRpcUrl));
    }

    public void EthereumEnvironment(){
        web3j = Web3j.build(new HttpService(ethereumRpcUrl));
    }

    public void ZKSyncEnvironment(){
        web3j = Web3j.build(new HttpService(zksyncRpcUrl));
    }


    public Web3JGetBalanceRes queryBalance(String address, boolean isToken, String contractAddr) throws Exception{
        Web3JGetBalanceRes web3JGetBalanceRes = new Web3JGetBalanceRes();
        //查询ETH余额
        BigInteger ethBalance = web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST).send().getBalance();
        logger.info("Web3j余额查询接口,ETH余额:{}",ethBalance);
        web3JGetBalanceRes.setEthBalance(ethBalance);
        if(isToken){
            Transaction transaction = Transaction.createEthCallTransaction(address,contractAddr, FunctionEncoder
                    .encode(new Function("balanceOf", Arrays.<Type>asList(new Address(address)),
                            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {
                            }))));
            EthCall ethCall = web3j.ethCall(transaction, DefaultBlockParameterName.LATEST).send();
            if(null == ethCall.getError() && StringUtils.hasText(ethCall.getResult())){
                String balanceHex = (String) ethCall.getResult();
                BigInteger tokenBalance = new BigInteger("0");
                if (balanceHex.length() > 3) {
                    tokenBalance = Numeric.toBigInt(balanceHex);
                }
                logger.info("Web3j余额查询接口,token余额:{}",tokenBalance);
                web3JGetBalanceRes.setTokenBalance(tokenBalance);
            }
        }
        return web3JGetBalanceRes;
    }

    public String sendTransaction(String fromPrivateKey,String fromAddress,
                                  String toAddress, BigInteger transferAmount,
                                  boolean isToken, String contractAddr,BigInteger gasLimit) throws Exception{

        long chainId = web3j.ethChainId().send().getChainId().longValue();
        String signHexValue = "";
        //转账
        //根据转账私钥生产Credentials对象
        Credentials credentials = Credentials.create(fromPrivateKey);

        BigInteger nonce = web3j.ethGetTransactionCount(
                fromAddress, DefaultBlockParameterName.LATEST).send().getTransactionCount();
        BigInteger gasPrice = web3j.ethGasPrice().send().getGasPrice();

        if(isToken) {
            //代币转账签名
            Function function = new Function("transfer", Arrays.<Type> asList(new Address(toAddress), new Uint256(transferAmount)),
                    Collections.<TypeReference<?>> emptyList());
            RawTransaction rawTransaction = RawTransaction.createTransaction(nonce,gasPrice,gasLimit,contractAddr, FunctionEncoder.encode(function));
            //使用Credentials对象对RawTransaction对象进行签名
            byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction,chainId, credentials);
            signHexValue = Numeric.toHexString(signedMessage);
        }else {
            RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, toAddress, transferAmount, "CyberPay Transaction");//可以额外带数据
            //使用Credentials对象对RawTransaction对象进行签名
            byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction,chainId, credentials);
            signHexValue = Numeric.toHexString(signedMessage);
        }
        //发送转账消息
        EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(signHexValue).send();

        if(null == ethSendTransaction.getError()
                && StringUtils.hasText(ethSendTransaction.getTransactionHash())){
            return ethSendTransaction.getTransactionHash();
        }else{
            logger.info("Web3j转账接口,请求节点异常: {} {}",ethSendTransaction.getError().getCode(),ethSendTransaction.getError().getMessage());
        }
        return null;
    }

    public TransactionReceipt confirmTransaction(String transHash) throws Exception{

        EthGetTransactionReceipt ethGetTransactionReceipt =
                web3j.ethGetTransactionReceipt(transHash).send();

        if(null == ethGetTransactionReceipt.getError()
                && null != ethGetTransactionReceipt.getResult()){
            return ethGetTransactionReceipt.getResult();
        }else{
            logger.info("Web3j确认交易接口,请求节点异常: {} {}",ethGetTransactionReceipt.getError().getCode(),ethGetTransactionReceipt.getError().getMessage());
        }
        return null;
    }

    public BigInteger getLatestBlockNumber() throws Exception{
        return web3j.ethBlockNumber().send().getBlockNumber();
    }
}
