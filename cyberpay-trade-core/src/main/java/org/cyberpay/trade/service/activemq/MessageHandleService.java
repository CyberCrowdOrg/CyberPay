package org.cyberpay.trade.service.activemq;

import com.alibaba.fastjson2.JSON;
import org.cyberpay.crypto.enums.CryptoOrderStatusEnum;
import org.cyberpay.trade.constant.RedisLockKeyConstants;
import org.cyberpay.trade.dto.MerchantFeeConfigDto;
import org.cyberpay.trade.enums.CoinTypeEnum;
import org.cyberpay.trade.enums.MerchantFeeTypeEnum;
import org.cyberpay.trade.enums.TradeOrderStatusEnum;
import org.cyberpay.trade.mapper.CryptoTradeOrderMapper;
import org.cyberpay.trade.model.CryptoTradeOrder;
import org.cyberpay.trade.service.MerchantConfigService;
import org.cyberpay.trade.utils.RedissonLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Date;

@Service("messageHandleService")
public class MessageHandleService {

    private Logger logger = LoggerFactory.getLogger(MessageHandleService.class);

    @Autowired
    RedissonClient redissonClient;
    @Autowired
    MerchantConfigService merchantConfigService;

    @Autowired
    CryptoTradeOrderMapper cryptoTradeOrderMapper;


    public void cryptoTradeResultMessageHandle(CryptoTradeResultMessageDto cryptoTradeResultMessageDto) throws Exception{
        logger.info("加密币交易结果消息处理业务,请求入参:{}", JSON.toJSONString(cryptoTradeResultMessageDto));
        String tradeFlowNo = cryptoTradeResultMessageDto.getTradeFlowNo();
        String status = cryptoTradeResultMessageDto.getStatus();
        BigDecimal receiptAmount = cryptoTradeResultMessageDto.getReceiptAmount();

        RedissonLock redissonLock = new RedissonLock(redissonClient, MessageFormat.format(RedisLockKeyConstants.CRYPTO_TRADE_ORDER_UPDATE_LOCK_KEY,tradeFlowNo));

        try{
            if(redissonLock.lock()){
                CryptoTradeOrder cryptoTradeOrder =
                        cryptoTradeOrderMapper.findCryptoTradeOrderByTradeFlowNo(tradeFlowNo);

                //订单不能为空且,订单状态是待付款或者未确认状态才能更新订单信息
                if(null != cryptoTradeOrder
                        && (TradeOrderStatusEnum.pending.getCode().equals(cryptoTradeOrder.getTradeStatus())
                        || TradeOrderStatusEnum.unconfirmed.getCode().equals(cryptoTradeOrder.getTradeStatus()))){

                    logger.info("加密币交易结果消息处理业务,交易流水号:{} 订单状态:{} 待修改状态:{}",
                            tradeFlowNo,cryptoTradeOrder.getTradeStatus(),status);

                    if(TradeOrderStatusEnum.pending.getCode().equals(cryptoTradeOrder.getTradeStatus())
                            && CryptoOrderStatusEnum.unconfirmed.getCode().equals(status)){
                        cryptoTradeOrder.setTradeStatus(TradeOrderStatusEnum.unconfirmed.getCode());
                    }else if(TradeOrderStatusEnum.unconfirmed.getCode().equals(cryptoTradeOrder.getTradeStatus())
                            && CryptoOrderStatusEnum.success.getCode().equals(status)){
                        cryptoTradeOrder.setSuccessTime(new Date());
                        cryptoTradeOrder.setReceiptAmount(receiptAmount);
                        cryptoTradeOrder.setTradeStatus(TradeOrderStatusEnum.success.getCode());
                    }else if(TradeOrderStatusEnum.unconfirmed.getCode().equals(cryptoTradeOrder.getTradeStatus())
                            && CryptoOrderStatusEnum.lsuccess.getCode().equals(status)){
                        cryptoTradeOrder.setSuccessTime(new Date());
                        cryptoTradeOrder.setReceiptAmount(receiptAmount);
                        cryptoTradeOrder.setTradeStatus(TradeOrderStatusEnum.lsuccess.getCode());

                    }else if(TradeOrderStatusEnum.unconfirmed.getCode().equals(cryptoTradeOrder.getTradeStatus())
                            && CryptoOrderStatusEnum.msuccess.getCode().equals(status)){

                        cryptoTradeOrder.setSuccessTime(new Date());
                        cryptoTradeOrder.setReceiptAmount(receiptAmount);
                        cryptoTradeOrder.setTradeStatus(TradeOrderStatusEnum.msuccess.getCode());

                    }else if(( TradeOrderStatusEnum.init.getCode().equals(cryptoTradeOrder.getTradeStatus())
                            || TradeOrderStatusEnum.pending.getCode().equals(cryptoTradeOrder.getTradeStatus())
                            || TradeOrderStatusEnum.unconfirmed.getCode().equals(cryptoTradeOrder.getTradeStatus()))
                            && CryptoOrderStatusEnum.fail.getCode().equals(status)){

                        cryptoTradeOrder.setTradeStatus(TradeOrderStatusEnum.fail.getCode());

                    }else if((TradeOrderStatusEnum.init.getCode().equals(cryptoTradeOrder.getTradeStatus())
                            || TradeOrderStatusEnum.pending.getCode().equals(cryptoTradeOrder.getTradeStatus()))
                            && CryptoOrderStatusEnum.close.getCode().equals(status)){
                        cryptoTradeOrder.setTradeStatus(TradeOrderStatusEnum.close.getCode());
                        cryptoTradeOrder.setCloseTime(new Date());
                    }

                    cryptoTradeOrder.setUpdateTime(new Date());
                    cryptoTradeOrderMapper.updateByPrimaryKeySelective(cryptoTradeOrder);

                    if(TradeOrderStatusEnum.success.getCode().equals(cryptoTradeOrder.getTradeStatus())
                            || TradeOrderStatusEnum.lsuccess.getCode().equals(cryptoTradeOrder.getTradeStatus())
                            || TradeOrderStatusEnum.msuccess.getCode().equals(cryptoTradeOrder.getTradeStatus())){
                        //查询交易手续费信息
                        MerchantFeeConfigDto merchantFee = merchantConfigService.findMerchantFee(cryptoTradeOrder.getMerchantId(),
                                cryptoTradeOrder.getRecieveCoin(), null, CoinTypeEnum.crypto, MerchantFeeTypeEnum.trade);
                        //TODO 商户账户记账
                        
                    }
                }else {
                    logger.info("加密币交易结果消息处理业务,交易流水号:{} 订单状态:{} 不是待付款或者未确认状态,不符合修改条件",
                            tradeFlowNo,cryptoTradeOrder.getTradeStatus());
                }
            }else {
                new Exception("获取订单更新锁失败");
            }
        }catch (Exception e){
            throw  e;
        }finally {
            redissonLock.unlock();
        }
    }
}
