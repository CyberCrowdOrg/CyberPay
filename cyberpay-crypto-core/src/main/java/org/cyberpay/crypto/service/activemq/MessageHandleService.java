package org.cyberpay.crypto.service.activemq;

import com.alibaba.fastjson2.JSON;
import org.cyberpay.crypto.constant.RedisLockKeyConstants;
import org.cyberpay.crypto.dto.CryptoSupportNetworkDto;
import org.cyberpay.crypto.dto.SupportNetworkDto;
import org.cyberpay.crypto.enums.CryptoOrderStatusEnum;
import org.cyberpay.crypto.mapper.BlockChainDataMapper;
import org.cyberpay.crypto.mapper.CryptoOrderExtendMapper;
import org.cyberpay.crypto.mapper.CryptoOrderMapper;
import org.cyberpay.crypto.model.BlockChainData;
import org.cyberpay.crypto.model.CryptoOrder;
import org.cyberpay.crypto.model.CryptoOrderExtend;
import org.cyberpay.crypto.model.CryptoSupportNetwork;
import org.cyberpay.crypto.service.CryptoNodeMappingService;
import org.cyberpay.crypto.service.CryptoSupportNetworkService;
import org.cyberpay.crypto.utils.RedissonLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Date;

@Service("messageHandleService")
public class MessageHandleService {

    private Logger logger = LoggerFactory.getLogger(MessageHandleService.class);

    @Autowired
    CryptoOrderMapper cryptoOrderMapper;
    @Autowired
    CryptoOrderExtendMapper cryptoOrderExtendMapper;
    @Autowired
    BlockChainDataMapper blockChainDataMapper;

    @Autowired
    RedissonClient redissonClient;
    @Autowired
    CryptoSupportNetworkService cryptoSupportNetworkService;
    @Autowired
    CryptoNodeMappingService cryptoNodeMappingService;
    @Autowired
    ProducerService producerService;

    public void messageHandle(NodeFilterMessageDto nodeFilterMessageDto) throws Exception{
        logger.info("???????????????????????????????????????,????????????:{}",JSON.toJSONString(nodeFilterMessageDto));
        String contract = nodeFilterMessageDto.getContract();
        String symbol = nodeFilterMessageDto.getSymbol();
        String networkCode = nodeFilterMessageDto.getNetworkCode();

        //????????????????????????
        CryptoSupportNetworkDto cryptoSupportNetwork = cryptoSupportNetworkService.findCryptoSupportNetwork(
                symbol, networkCode, contract);

        if(null != cryptoSupportNetwork) {
            //???????????????????????????????????????
            String receiveAddress = nodeFilterMessageDto.getToAddress();
            CryptoOrder cryptoOrderReq = new CryptoOrder();
            cryptoOrderReq.setStatus(CryptoOrderStatusEnum.unpaid.getCode());
            cryptoOrderReq.setReceiveCoin(symbol);
            cryptoOrderReq.setReceiveNetworkCode(networkCode);
            cryptoOrderReq.setReceiveAddress(receiveAddress);

            //???
            RedissonLock redissonLock = null;
            try{
                CryptoOrder cryptoOrder = cryptoOrderMapper.selectCryptoOrder(cryptoOrderReq);

                if (null != cryptoOrder) {
                    String bizFlowNo = cryptoOrder.getBizFlowNo();
                    redissonLock = new RedissonLock(redissonClient,
                            MessageFormat.format(RedisLockKeyConstants.CRYPTO_ORDER_UPDATE_LOCK_KEY,bizFlowNo));
                    //??????????????????
                    if(redissonLock.lock()){
                        cryptoOrder = cryptoOrderMapper.selectCryptoOrder(cryptoOrderReq);
                        if(null != cryptoOrder) {
                            //???????????????????????????
                            updateCryptoOrder(cryptoOrder, cryptoSupportNetwork, nodeFilterMessageDto);
                            //???????????????????????????
                            producerService.cryptoTradeResultMessage(cryptoOrder);
                        }
                    }else {
                        logger.info("???????????????????????????????????????,???????????????:{} ??????Redis?????????",bizFlowNo);
                        throw new Exception("???????????????????????????????????????,??????Redis?????????");
                    }
                }
            }catch (Exception e){
                logger.error("???????????????????????????????????????,????????????:{}",e.getMessage(),e);
                throw e;
            }finally {
                redissonLock.unlock();
            }
            //??????????????? block chain data
            saveBlockChainData(nodeFilterMessageDto,cryptoSupportNetwork);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.REPEATABLE_READ, rollbackFor = Exception.class)
    protected void updateCryptoOrder(CryptoOrder cryptoOrder,
                                     CryptoSupportNetworkDto cryptoSupportNetwork,
                                     NodeFilterMessageDto nodeFilterMessageDto){

        Long coinDecimal = cryptoSupportNetwork.getCoinDecimal();
        Long coinDecimalUnit = cryptoSupportNetwork.getCoinDecimalUnit();

        BigDecimal receiptAmount = new BigDecimal(nodeFilterMessageDto.getTransAmount())
                .divide(new BigDecimal(coinDecimal),
                        coinDecimalUnit.intValue(),BigDecimal.ROUND_HALF_UP);
        //??????????????????
        cryptoOrder.setReceiptAmount(receiptAmount);
        cryptoOrder.setDiffReceiveAmount(receiptAmount.subtract(cryptoOrder.getReceiveAmount()));
        //?????????
        cryptoOrder.setStatus(CryptoOrderStatusEnum.unconfirmed.getCode());
        cryptoOrder.setUpdateTime(new Date());

        //????????????????????????
        CryptoOrderExtend cryptoOrderExtend = new CryptoOrderExtend();
        cryptoOrderExtend.setBizFlowNo(cryptoOrder.getBizFlowNo());
        cryptoOrderExtend.setTransHash(nodeFilterMessageDto.getTxHash());
        cryptoOrderExtend.setPaymentAddress(nodeFilterMessageDto.getFromAddress());
        cryptoOrderExtend.setBlockNumber(nodeFilterMessageDto.getBlockNumber());
        cryptoOrderExtend.setConfirm(nodeFilterMessageDto.getConfirm());
        cryptoOrderExtend.setBlockHash(nodeFilterMessageDto.getBlockHash());

        cryptoOrderExtendMapper.updateCryptoOrderExtendByBizFlowNo(cryptoOrderExtend);
        cryptoOrderMapper.updateByPrimaryKeySelective(cryptoOrder);

    }

    @Async
    protected void saveBlockChainData(NodeFilterMessageDto nodeFilterMessageDto,
                                      CryptoSupportNetworkDto cryptoSupportNetworkDto){
        BlockChainData chainData = new BlockChainData();
        chainData.setCyrptoSymbol(nodeFilterMessageDto.getSymbol());
        chainData.setNetworkCode(nodeFilterMessageDto.getNetworkCode());
        chainData.setBlockHash(nodeFilterMessageDto.getBlockHash());
        chainData.setTransHash(nodeFilterMessageDto.getTxHash());
        chainData.setBlockNumber(nodeFilterMessageDto.getBlockNumber());
        chainData.setReceiveAddress(nodeFilterMessageDto.getToAddress());
        chainData.setCreateTime(new Date());
        chainData.setUpdateTime(new Date());

        BigDecimal receiptAmount = new BigDecimal(nodeFilterMessageDto.getTransAmount())
                .divide(new BigDecimal(cryptoSupportNetworkDto.getCoinDecimal()),
                        cryptoSupportNetworkDto.getCoinDecimalUnit().intValue(),BigDecimal.ROUND_HALF_UP);

        chainData.setTransAmount(receiptAmount);
        blockChainDataMapper.insertSelective(chainData);
    }
}
