package org.cyberpay.crypto.node.binance;

import com.alibaba.fastjson2.JSON;
import com.binance.dex.api.client.*;
import com.binance.dex.api.client.domain.*;
import com.binance.dex.api.client.domain.broadcast.Transaction;
import com.binance.dex.api.client.domain.broadcast.TransactionOption;
import com.binance.dex.api.client.domain.broadcast.Transfer;
import com.binance.dex.api.client.domain.broadcast.TxType;
import com.binance.dex.api.client.encoding.Crypto;
import org.cyberpay.crypto.enums.CoinDecimalEnum;
import org.cyberpay.crypto.enums.ReturnCodeEnum;
import org.cyberpay.crypto.node.BaseNodeFunction;
import org.cyberpay.crypto.node.NodeInterface;
import org.cyberpay.crypto.node.NodeRequestDto;
import org.cyberpay.crypto.node.NodeResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * 币安链
 */
@Component(value = "binanceNodeInterface")
public class BinanceNodeInterface extends BaseNodeFunction implements NodeInterface {

    private Logger logger = LoggerFactory.getLogger(BinanceNodeInterface.class);

    @Value("spring.profiles.active")
    private String active;

    private BinanceDexEnvironment networkParameter =
            "prod".equals(active) ? BinanceDexEnvironment.PROD:BinanceDexEnvironment.TEST_NET;

    @Override
    public NodeResponseDto generateAddress(NodeRequestDto nodeRequestDto) {
        NodeResponseDto nodeResponseDto = new NodeResponseDto();
        try {
            List<String> mnemonicCodeWords = Crypto.generateMnemonicCode();
            Wallet wallet = Wallet.createWalletFromMnemonicCode(mnemonicCodeWords,networkParameter);
            nodeResponseDto.setAddress(wallet.getAddress());
            nodeResponseDto.setPrivateKey(wallet.getPrivateKey());
            nodeResponseDto.setMnemonics(String.join(" ", mnemonicCodeWords));
//            nodeResponseDto.setPublicKey("");
        }catch (Exception e){
            logger.error("binance chain 地址生成,执行异常:{}",e.getMessage(),e);
            nodeResponseDto.setReturnCodeEnum(ReturnCodeEnum.FAIL,null);
        }
        logger.info("binance chain 地址生成,响应结果:{}", JSON.toJSONString(nodeRequestDto));
        return nodeResponseDto;
    }

    @Override
    public NodeResponseDto queryBalance(NodeRequestDto nodeRequestDto) {
        NodeResponseDto nodeResponseDto = new NodeResponseDto();
        logger.info("binance chain 余额查询,请求入参:{}",JSON.toJSONString(nodeRequestDto));
        String address = nodeRequestDto.getAddress();
        String symbol = nodeRequestDto.getSymbol();
        try {
            BinanceDexApiNodeClient client = BinanceDexApiClientFactory.newInstance().newNodeRpcClient(
                    networkParameter.getNodeUrl(), networkParameter.getHrp(), networkParameter.getValHrp());

            Account account = client.getAccount(address);
            if (null == account || (null == account.getBalances() || account.getBalances().size() == 0)) {
                logger.info("binance chain 余额查询失败,返回null值");
                nodeResponseDto.setReturnCodeEnum(ReturnCodeEnum.FAIL, null);
                return nodeResponseDto;
            }

            List<Balance> balances = account.getBalances();
            for (Balance balance : balances) {
                if ("bnb".equalsIgnoreCase(balance.getSymbol())) {
                    nodeResponseDto.setBalance(
                            CoinDecimalEnum.converToBigDecimal(
                                    Long.valueOf(balance.getFree()), CoinDecimalEnum.BNB));
                }
                if (StringUtils.hasText(symbol) && symbol.equalsIgnoreCase(balance.getSymbol())) {
                    nodeResponseDto.setTokenBalance(
                            CoinDecimalEnum.converToBigDecimal(
                                    Long.valueOf(balance.getFree()), CoinDecimalEnum.BNB));
                }
            }
        }catch (Exception e){
            logger.error("binance chain 余额查询,执行异常:{}",e.getMessage(),e);
            nodeResponseDto.setReturnCodeEnum(ReturnCodeEnum.FAIL,null);
        }
        logger.info("binance chain 余额查询,响应结果:{}",JSON.toJSONString(nodeResponseDto));
        return nodeResponseDto;
    }

    @Override
    public NodeResponseDto sendTransaction(NodeRequestDto nodeRequestDto) {
        NodeResponseDto nodeResponseDto = new NodeResponseDto();
        logger.info("binance chain 转账交易,请求入参:{}",JSON.toJSONString(nodeRequestDto));

        String fromAddress = nodeRequestDto.getFromAddress();
        String fromPrivateKey = nodeRequestDto.getFromPrivateKey();
        String toAddress = nodeRequestDto.getToAddress();
        BigDecimal transferAmount = nodeRequestDto.getTransferAmount();
        String symbol = nodeRequestDto.getSymbol();

        try{
            BinanceDexApiNodeClient client = BinanceDexApiClientFactory.newInstance().newNodeRpcClient(
                    networkParameter.getNodeUrl(), networkParameter.getHrp(), networkParameter.getValHrp());

            Wallet wallet = new Wallet(fromPrivateKey, networkParameter);

            Transfer transfer = new Transfer();
            transfer.setCoin(symbol);
            transfer.setFromAddress(fromAddress);
            transfer.setToAddress(toAddress);
            transfer.setAmount(transferAmount.toString());

            TransactionOption option = new TransactionOption("CyberPay",1,null);
            List<TransactionMetadata> transferRes = client.transfer(transfer, wallet, option, true);
            TransactionMetadata transactionMetadata = transferRes.get(0);
            boolean ok = transactionMetadata.isOk();
            if(ok){
                String hash = transactionMetadata.getHash();
                Long height = transactionMetadata.getHeight();
                nodeResponseDto.setTransHash(hash);
            }else{
                String log = transactionMetadata.getLog();
                logger.info("binance chain 转账交易,转账失败:{}",log);
                nodeResponseDto.setReturnCodeEnum(ReturnCodeEnum.FAIL,log);
            }
        }catch (Exception e){
            logger.error("binance chain 转账交易,执行异常:{}",e.getMessage(),e);
            nodeResponseDto.setReturnCodeEnum(ReturnCodeEnum.FAIL,null);
        }
        logger.info("binance chain 转账交易,响应结果:{}",JSON.toJSONString(nodeResponseDto));
        return nodeResponseDto;
    }

    @Override
    public NodeResponseDto confirmTransaction(NodeRequestDto nodeRequestDto) {
        NodeResponseDto nodeResponseDto = new NodeResponseDto();
        logger.info("binance chain 交易结果确认查询,请求入参:{}",JSON.toJSONString(nodeRequestDto));
        String transHash = nodeRequestDto.getTransHash();

        try {

            BinanceDexApiNodeClient client = BinanceDexApiClientFactory.newInstance().newNodeRpcClient(
                    networkParameter.getNodeUrl(), networkParameter.getHrp(), networkParameter.getValHrp());

            Transaction transaction = client.getTransaction(transHash);

            int code = transaction.getCode().intValue();
            if (0 == code) {
                String hash = transaction.getHash();
                Long height = transaction.getHeight();
                String memo = transaction.getMemo();
                TxType txType = transaction.getTxType();

                nodeResponseDto.setTransHash(hash);
                nodeResponseDto.setMemo(memo);
                nodeResponseDto.setBlockNumber(height);
                nodeResponseDto.setConfirm(1L);//BSC链1个区块确认就行了
            } else {
                String log = transaction.getLog();
                logger.info("binance chain 交易结果确认查询,请求节点失败:{}", log);
                nodeResponseDto.setReturnCodeEnum(ReturnCodeEnum.FAIL, log);
            }
        }catch (Exception e){
            logger.error("binance chain 交易结果确认查询,执行异常:{}",e.getMessage(),e);
            nodeResponseDto.setReturnCodeEnum(ReturnCodeEnum.FAIL, null);
        }
        logger.info("binance chain 交易结果确认查询,响应结果:{}",JSON.toJSONString(nodeResponseDto));
        return nodeResponseDto;
    }
}
