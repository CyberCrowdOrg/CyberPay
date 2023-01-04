package org.cyberpay.crypto.node.zksync;

import com.alibaba.fastjson2.JSON;
import org.cyberpay.crypto.enums.ReturnCodeEnum;
import org.cyberpay.crypto.node.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.web3j.crypto.Bip39Wallet;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;

@Component(value = "zkSyncNodeInterface")
public class ZKSyncNodeInterface extends Web3jNodeFunction implements NodeInterface {

    private Logger logger = LoggerFactory.getLogger(ZKSyncNodeInterface.class);

    @Value("${wallet.file-path.ethereum}")
    private String walletFilePath;

    @Value("${gas-limit.zksync}")
    private String gasLimit;


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
            logger.error("zksync 钱包生成接口执行异常:{}",e.getMessage(),e);
            nodeResponseDto.setReturnCodeEnum(ReturnCodeEnum.FAIL,null);
        }
        return nodeResponseDto;
    }

    @Override
    public NodeResponseDto queryBalance(NodeRequestDto nodeRequestDto) {
        NodeResponseDto nodeResponseDto = new NodeResponseDto();
        logger.info("zksync 余额查询接口,请求入参:{}", JSON.toJSONString(nodeRequestDto));

        String address = nodeRequestDto.getAddress();
        String contractAddress = nodeRequestDto.getContractAddress();
        Long tokenDecimal = nodeRequestDto.getTokenDecimal();
        Long coinDecimal = nodeRequestDto.getCoinDecimal();
        boolean isToken = StringUtils.hasText(contractAddress) ? true:false;
        try {
            this.ZKSyncEnvironment();
            Web3JGetBalanceRes web3JGetBalanceRes = this.queryBalance(address, isToken, contractAddress);

            BigDecimal ethBalance = Convert.fromWei(web3JGetBalanceRes.toString(), Convert.Unit.ETHER);
            nodeResponseDto.setBalance(ethBalance);
            BigDecimal tokenBalance = new BigDecimal(web3JGetBalanceRes.getTokenBalance())
                    .divide(new BigDecimal(tokenDecimal),coinDecimal.intValue(),BigDecimal.ROUND_HALF_DOWN);
            nodeResponseDto.setTokenBalance(tokenBalance);
        }catch (Exception e){
            logger.error("zksync 余额查询接口,执行异常:{}",e.getMessage(),e);
            nodeResponseDto.setReturnCodeEnum(ReturnCodeEnum.FAIL,"余额查询异常");
        }
        return nodeResponseDto;
    }

    @Override
    public NodeResponseDto sendTransaction(NodeRequestDto nodeRequestDto) {
        NodeResponseDto nodeResponseDto = new NodeResponseDto();
        logger.info("zksync 转账交易,请求入参:{}",JSON.toJSONString(nodeRequestDto));
        String fromAddress = nodeRequestDto.getFromAddress();
        String toAddress = nodeRequestDto.getToAddress();
        String privateKey = nodeRequestDto.getFromPrivateKey();
        //合约地址
        String contractAddress = nodeRequestDto.getContractAddress();
        Long tokenDecimal = nodeRequestDto.getTokenDecimal();

        boolean isToken = StringUtils.hasText(contractAddress) ? true:false;

        try {
            BigInteger transferAmount = nodeRequestDto.getTransferAmount().multiply(new BigDecimal(tokenDecimal)).toBigInteger();
            //发起转账
            this.ZKSyncEnvironment();
            String txHash = this.sendTransaction(privateKey, fromAddress, toAddress,
                    transferAmount,isToken, contractAddress,new BigInteger(gasLimit));
            if (!StringUtils.hasText(txHash)) {
                logger.info("zksync 转账交易请求失败");
                nodeResponseDto.setReturnCodeEnum(ReturnCodeEnum.FAIL,"转账交易请求失败");
                return nodeResponseDto;
            }
            //返回交易hash
            nodeResponseDto.setTransHash(txHash);
        }catch (Exception e){
            logger.error("zksync 转账交易,执行异常:{}",e.getMessage(),e);
            nodeResponseDto.setReturnCodeEnum(ReturnCodeEnum.FAIL,"zksync转账交易异常");
        }
        logger.info("zksync 转账交易,响应结果:{}",JSON.toJSONString(nodeResponseDto));
        return nodeResponseDto;
    }

    @Override
    public NodeResponseDto confirmTransaction(NodeRequestDto nodeRequestDto) {
        NodeResponseDto nodeResponseDto = new NodeResponseDto();
        String transHash = nodeRequestDto.getTransHash();
        //zksync 查询一次没有问题就成功
        try {
            this.ZKSyncEnvironment();
            TransactionReceipt receipt = this.confirmTransaction(transHash);
            if (null != receipt) {
                String status = receipt.getStatus();
                if ("0x1".equals(status)) {
                    BigInteger blockNumber = receipt.getBlockNumber();
                    BigDecimal gasPrice = new BigDecimal(Numeric.decodeQuantity(receipt.getEffectiveGasPrice()));
                    BigInteger gasLimit = receipt.getGasUsed();
                    BigDecimal fee = Convert.fromWei(gasPrice.multiply(new BigDecimal(gasLimit)), Convert.Unit.ETHER);
                    String blockHash = receipt.getBlockHash();
                    String transactionHash = receipt.getTransactionHash();
                    String fromAddress = receipt.getFrom();

                    nodeResponseDto.setStatus(true);
                    nodeResponseDto.setTransHash(transactionHash);
                    nodeResponseDto.setBlockHash(blockHash);
                    nodeResponseDto.setConfirm(2);
                    nodeResponseDto.setFromAddress(fromAddress);
                    nodeResponseDto.setFee(fee);
                    logger.info("zksync 交易确认接口,响应结果:{}", JSON.toJSONString(nodeResponseDto));
                }
            }
        }catch (Exception e){
            logger.info("zksync 交易确认接口,执行异常:{}",e.getMessage(),e);
            nodeResponseDto.setReturnCodeEnum(ReturnCodeEnum.FAIL,"zksync交易确认异常");
        }
        return nodeResponseDto;
    }
}
