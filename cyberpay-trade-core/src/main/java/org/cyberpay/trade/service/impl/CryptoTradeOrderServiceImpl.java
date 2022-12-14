package org.cyberpay.trade.service.impl;

import com.alibaba.fastjson2.JSON;
import org.cyberpay.crypto.dto.CryptoAppProtocolDto;
import org.cyberpay.crypto.enums.ReturnCodeEnum;
import org.cyberpay.crypto.request.CryptoOrderSubmitPayReq;
import org.cyberpay.crypto.request.CryptoPayOrderReq;
import org.cyberpay.crypto.request.CryptoPreOrderReq;
import org.cyberpay.crypto.response.CryptoOrderSubmitPayRes;
import org.cyberpay.crypto.response.CryptoPayOrderRes;
import org.cyberpay.crypto.response.CryptoPreOrderRes;
import org.cyberpay.trade.constant.RedisCacheKeyConstants;
import org.cyberpay.trade.constant.RedisLockKeyConstants;
import org.cyberpay.trade.dto.CryptoReceiveAdapterDto;
import org.cyberpay.trade.enums.CoinTypeEnum;
import org.cyberpay.trade.enums.TradeOrderStatusEnum;
import org.cyberpay.trade.enums.TradeReturnCodeEnum;
import org.cyberpay.trade.mapper.CryptoTradeOrderMapper;
import org.cyberpay.trade.mapper.MerchantFloatingRateConfigMapper;
import org.cyberpay.trade.model.CryptoTradeOrder;
import org.cyberpay.trade.request.CryptoTradeOrderPaySubmitReq;
import org.cyberpay.trade.request.CryptoTradePayOrderReq;
import org.cyberpay.trade.request.CryptoTradePreTradeOrderReq;
import org.cyberpay.trade.request.CryptoTradeRefundOrderReq;
import org.cyberpay.trade.response.CryptoTradeOrderPaySubmitRes;
import org.cyberpay.trade.response.CryptoTradePayOrderRes;
import org.cyberpay.trade.response.CryptoTradePreOrderRes;
import org.cyberpay.trade.response.CryptoTradeRefundOrderRes;
import org.cyberpay.trade.service.CryptoTradeOrderService;
import org.cyberpay.trade.service.MerchantConfigService;
import org.cyberpay.trade.service.feign.CryptoPayOrderFeignClient;
import org.cyberpay.trade.utils.IDUtils;
import org.cyberpay.trade.utils.RedissonLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service("cryptoTradeOrderService")
public class CryptoTradeOrderServiceImpl implements CryptoTradeOrderService {

    private Logger logger = LoggerFactory.getLogger(CryptoTradeOrderServiceImpl.class);

    @Autowired
    CryptoTradeOrderMapper cryptoTradeOrderMapper;
    @Autowired
    MerchantFloatingRateConfigMapper merchantFloatingRateConfigMapper;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    RedissonClient redissonClient;
//    @Autowired
    CryptoPayOrderFeignClient cryptoPayOrderFeignClient;
    @Autowired
    MerchantConfigService merchantConfigService;

    @Override
    public CryptoTradePreOrderRes tradePreOrder(CryptoTradePreTradeOrderReq cryptoTradePreTradeOrderReq) {
        CryptoTradePreOrderRes cryptoTradePreOrderRes = new CryptoTradePreOrderRes();
        String orderNo = cryptoTradePreTradeOrderReq.getOrderNo();
        logger.info("??????????????????????????????,????????????:{}",JSON.toJSONString(cryptoTradePreTradeOrderReq));

        //???
        RedissonLock redissonLock = new RedissonLock(
                redissonClient,MessageFormat.format(RedisLockKeyConstants.CRYPTO_TRADE_ORDER_LOCK_KEY,orderNo));
        try {
            if(redissonLock.lock()) {
                CryptoTradeOrder cryptoTradeOrder = cryptoTradeOrderMapper.findCryptoTradeOrder(orderNo);
                if (null != cryptoTradeOrder) {
                    logger.info("??????????????????????????????,?????????:{} ???????????????", orderNo);
                    cryptoTradePreOrderRes.setReturnEnum(TradeReturnCodeEnum.ORDER_NOT_FOUND_ERROR);
                    return cryptoTradePreOrderRes;
                }

                //?????????????????????
                cryptoTradeOrder = createTradePreOrder(cryptoTradePreTradeOrderReq);
                //??????????????????????????????
                CryptoPreOrderReq cryptoPreOrderReq = new CryptoPreOrderReq();
                cryptoPreOrderReq.setOrderNo(cryptoTradeOrder.getOrderNo());
                cryptoPreOrderReq.setOrderAmount(cryptoTradeOrder.getOrderAmount());
                cryptoPreOrderReq.setBizOrderType(cryptoTradeOrder.getOrderType());
                cryptoPreOrderReq.setOrderCoin(cryptoTradeOrder.getOrderCoin());
                CryptoPreOrderRes cryptoPreOrderRes = cryptoPayOrderFeignClient.preOrder(cryptoPreOrderReq);
                logger.info("??????????????????????????????,?????????:{} ???????????????????????????????????????:{}", JSON.toJSONString(cryptoPreOrderRes));
                //???????????????????????????
                return cryptoTradePreOrderResponseHandle(cryptoTradeOrder, cryptoPreOrderRes);
            }else {
                logger.error("??????????????????????????????,?????????:{} ??????Redis?????????",orderNo);
            }
        }catch (Exception e){
            logger.error("??????????????????????????????,?????????:{} ????????????:{}",orderNo,e.getMessage(),e);
            cryptoTradePreOrderRes.setReturnEnum(TradeReturnCodeEnum.FAIL);
        }finally {
            redissonLock.unlock();
        }
        return cryptoTradePreOrderRes;
    }

    @Override
    public CryptoTradeOrderPaySubmitRes tradeOrderPaySubmit(CryptoTradeOrderPaySubmitReq cryptoTradeOrderPaySubmitReq) {
        CryptoTradeOrderPaySubmitRes cryptoTradeOrderPaySubmitRes = new CryptoTradeOrderPaySubmitRes();
        String accessKey = cryptoTradeOrderPaySubmitReq.getAccessKey();
        logger.info("???????????????????????????????????????,????????????:{}",JSON.toJSONString(cryptoTradeOrderPaySubmitReq));
        //???
        RedissonLock redissonLock = null;
        try {
            //??????accessKey ?????????????????????????????????
            String cacheValue = redisTemplate.opsForValue().get(
                    MessageFormat.format(RedisCacheKeyConstants.CRYPTO_WEB_CASHIER_API_ACCESS_KEY_CACHE, accessKey));
            if (!StringUtils.hasText(cacheValue)) {
                logger.info("???????????????????????????????????????,??????????????????????????? :{}", accessKey);
                cryptoTradeOrderPaySubmitRes.setReturnEnum(TradeReturnCodeEnum.INVALID_ACCESS_KEY_ERROR);
                return cryptoTradeOrderPaySubmitRes;
            }
            //???????????????
            String tradeFlowNo = cacheValue;

            //???
            redissonLock = new RedissonLock(
                    redissonClient,MessageFormat.format(RedisLockKeyConstants.CRYPTO_TRADE_ORDER_UPDATE_LOCK_KEY,tradeFlowNo));

            if(redissonLock.lock()) {

                //??????????????????
                CryptoTradeOrder cryptoTradeOrder = cryptoTradeOrderMapper.findCryptoTradeOrderByTradeFlowNo(tradeFlowNo);
                if (null == cryptoTradeOrder) {
                    logger.info("???????????????????????????????????????,?????????????????????:{}", tradeFlowNo);
                    cryptoTradeOrderPaySubmitRes.setReturnEnum(TradeReturnCodeEnum.ORDER_NOT_FOUND_ERROR);
                    return cryptoTradeOrderPaySubmitRes;
                }

                //??????????????????,?????????????????????????????????
                String tradeStatus = cryptoTradeOrder.getTradeStatus();
                String merchantId = cryptoTradeOrder.getMerchantId();
                String receiveCoin = cryptoTradeOrderPaySubmitReq.getReceiveCoin();
                cryptoTradeOrderPaySubmitRes = checkPaySubmitCondition(merchantId,receiveCoin,
                        tradeFlowNo,tradeStatus);
                if(!TradeReturnCodeEnum.SUCCESS.getCode().equals(cryptoTradeOrderPaySubmitRes.getCode())){
                    return cryptoTradeOrderPaySubmitRes;
                }

                //??????????????????????????????????????????????????????????????????,??????????????????
                String orderRecieveCoin = cryptoTradeOrder.getRecieveCoin();
                String orderReceiveNetworkCode = cryptoTradeOrder.getReceiveNetworkCode();
                String receiveAddress = cryptoTradeOrder.getReceiveAddress();
                String receiveNetworkCode = cryptoTradeOrderPaySubmitReq.getReceiveNetworkCode();
                if (receiveCoin.equals(orderRecieveCoin)
                        && orderReceiveNetworkCode.equals(receiveNetworkCode)) {
                    logger.info("???????????????????????????????????????,???????????????:{} ????????????????????????????????????????????????????????????", tradeFlowNo);
                    return cryptoTradeOrderPaySubmitRes;
                }

                //??????????????????????????????
                BigDecimal floatingRate = merchantConfigService.findMerchantFloatingRate(merchantId, receiveCoin);

                //?????????????????????????????????
                CryptoOrderSubmitPayReq cryptoOrderSubmitPayReq = new CryptoOrderSubmitPayReq();
                cryptoOrderSubmitPayReq.setOrderNo(cryptoTradeOrder.getOrderNo());
                cryptoOrderSubmitPayReq.setReceiveCoin(receiveCoin);
                cryptoOrderSubmitPayReq.setReceiveNetworkCode(receiveNetworkCode);
                cryptoOrderSubmitPayReq.setFloatingRate(floatingRate);
                CryptoOrderSubmitPayRes cryptoOrderSubmitPayRes = cryptoPayOrderFeignClient.orderPaySubmit(cryptoOrderSubmitPayReq);
                logger.info("???????????????????????????????????????,???????????????:{} ???????????????????????????????????????:{}", JSON.toJSONString(cryptoOrderSubmitPayRes));

                return cryptoTradeOrderPaySubmitResponseHandle(cryptoTradeOrder, cryptoOrderSubmitPayRes, receiveCoin, receiveNetworkCode);
            }else {
                logger.info("???????????????????????????????????????,???????????????:{} ??????Redis?????????",tradeFlowNo);
                cryptoTradeOrderPaySubmitRes.setReturnEnum(TradeReturnCodeEnum.FAIL);
            }
        }catch (Exception e){
            cryptoTradeOrderPaySubmitRes.setReturnEnum(TradeReturnCodeEnum.FAIL);
            logger.error("???????????????????????????????????????,???????????????:{} ????????????:{}",e.getMessage(),e);
        }finally {
            if(null != redissonLock){
                redissonLock.unlock();
            }
        }
        return cryptoTradeOrderPaySubmitRes;
    }

    @Override
    public CryptoTradePayOrderRes tradePayOrder(CryptoTradePayOrderReq cryptoTradePayOrderReq) {
        CryptoTradePayOrderRes cryptoTradePayOrderRes = new CryptoTradePayOrderRes();
        String orderNo = cryptoTradePayOrderReq.getOrderNo();
        logger.info("???????????????????????????,????????????:{}",JSON.toJSONString(cryptoTradePayOrderReq));

        //???
        RedissonLock redissonLock = new RedissonLock(
                redissonClient,MessageFormat.format(RedisLockKeyConstants.CRYPTO_TRADE_ORDER_LOCK_KEY,orderNo));
        try{
            if(redissonLock.lock()){
                CryptoTradeOrder cryptoTradeOrder = cryptoTradeOrderMapper.findCryptoTradeOrder(orderNo);
                if (null != cryptoTradeOrder) {
                    logger.info("??????????????????????????????,?????????:{} ???????????????", orderNo);
                    cryptoTradePayOrderRes.setReturnEnum(TradeReturnCodeEnum.ORDER_NOT_FOUND_ERROR);
                    return cryptoTradePayOrderRes;
                }

                //???????????????????????????????????????
                //??????????????????????????????
                String merchantId = cryptoTradePayOrderReq.getMerchantId();
                String receiveCoin = cryptoTradePayOrderReq.getReceiveCoin();
                boolean status = merchantConfigService.merchantSupportCoinStatus(merchantId, receiveCoin, CoinTypeEnum.crypto);
                if(!status){
                    logger.info("???????????????????????????????????????,?????????:{} ???????????????????????????:{} ", orderNo,receiveCoin);
                    cryptoTradePayOrderRes.setReturnEnum(TradeReturnCodeEnum.TRADE_COIN_NOT_SUPPORT_ERROR);
                    return cryptoTradePayOrderRes;
                }

                //????????????
                cryptoTradeOrder = createTradePayOrder(cryptoTradePayOrderReq);

                //???????????????????????????
                BigDecimal floatingRate = merchantConfigService.findMerchantFloatingRate(merchantId, receiveCoin);

                //?????????????????????????????????
                CryptoPayOrderReq cryptoPayOrderReq = new CryptoPayOrderReq();
                cryptoPayOrderReq.setOrderNo(cryptoTradeOrder.getOrderNo());
                cryptoPayOrderReq.setOrderCoin(cryptoTradeOrder.getOrderCoin());
                cryptoPayOrderReq.setOrderAmount(cryptoTradeOrder.getOrderAmount());
                cryptoPayOrderReq.setBizOrderType(cryptoTradeOrder.getOrderType());
                cryptoPayOrderReq.setReceiveCoin(cryptoTradeOrder.getRecieveCoin());
                cryptoPayOrderReq.setReceiveNetworkCode(cryptoTradeOrder.getReceiveNetworkCode());
                cryptoPayOrderReq.setFloatingRate(floatingRate);
                CryptoPayOrderRes cryptoPayOrderRes = cryptoPayOrderFeignClient.createCryptoPayOrder(cryptoPayOrderReq);
                logger.info("???????????????????????????,?????????:{} ?????????????????????????????????:{}",JSON.toJSONString(cryptoPayOrderRes));
                return payOrderResponseHandle(cryptoTradeOrder,cryptoPayOrderRes);
            }else {
                logger.info("???????????????????????????,?????????:{} ??????Redis?????????");
                cryptoTradePayOrderRes.setReturnEnum(TradeReturnCodeEnum.FAIL);
            }
        }catch (Exception e){
            logger.info("???????????????????????????,?????????:{} ????????????:{}",orderNo);
            cryptoTradePayOrderRes.setReturnEnum(TradeReturnCodeEnum.FAIL);
        }finally {
            redissonLock.unlock();
        }
        return cryptoTradePayOrderRes;
    }

    @Override
    public CryptoTradeRefundOrderRes tradeRefundOrder(CryptoTradeRefundOrderReq cryptoTradeRefundOrderReq) {
        CryptoTradeRefundOrderRes cryptoTradeRefundOrderRes = new CryptoTradeRefundOrderRes();
        String refundOrderNo = cryptoTradeRefundOrderReq.getRefundOrderNo();
        String orgOrderNo = cryptoTradeRefundOrderReq.getOrgOrderNo();

        logger.info("??????");

        try{

        }catch (Exception e){
            logger.error("",e.getMessage(),e);
            cryptoTradeRefundOrderRes.setReturnEnum(TradeReturnCodeEnum.FAIL);
        }
        return cryptoTradeRefundOrderRes;
    }

    private CryptoTradePayOrderRes payOrderResponseHandle(CryptoTradeOrder cryptoTradeOrder, CryptoPayOrderRes cryptoPayOrderRes) {
        CryptoTradePayOrderRes cryptoTradePayOrderRes = new CryptoTradePayOrderRes();
        if(TradeReturnCodeEnum.SUCCESS.getCode().equals(cryptoPayOrderRes.getCode())){
            //??????????????????
            cryptoTradeOrder.setReceiveAmount(cryptoPayOrderRes.getReceiveAmount());
            cryptoTradeOrder.setReceiveAddress(cryptoPayOrderRes.getReceiveAddress());
            cryptoTradeOrder.setSysFlowNo(cryptoPayOrderRes.getBizFlowNo());
            cryptoTradeOrder.setCashierAddress(cryptoPayOrderRes.getLink());
            cryptoTradeOrder.setUpdateTime(new Date());
            cryptoTradeOrder.setTradeStatus(TradeOrderStatusEnum.pending.getCode());
            cryptoTradeOrderMapper.updateByPrimaryKeySelective(cryptoTradeOrder);

            cryptoTradePayOrderRes.setTradeFlowNo(cryptoTradeOrder.getTradeFlowNo());
            cryptoTradePayOrderRes.setReceiveCoin(cryptoTradeOrder.getRecieveCoin());
            cryptoTradePayOrderRes.setReceiveAddress(cryptoPayOrderRes.getReceiveAddress());
            cryptoTradePayOrderRes.setReceiveAmount(cryptoPayOrderRes.getReceiveAmount());
            cryptoTradePayOrderRes.setLink(cryptoPayOrderRes.getLink());

            List<CryptoAppProtocolDto> cryptoAppProtocolList = cryptoPayOrderRes.getCryptoAppProtocolList();
            if(null != cryptoAppProtocolList && cryptoAppProtocolList.size() > 0){
                List<CryptoReceiveAdapterDto> cryptoReceiveAdapters = new ArrayList<>();
                for(CryptoAppProtocolDto cryptoAppProtocolDto:cryptoAppProtocolList){
                    CryptoReceiveAdapterDto cryptoReceiveAdapterDto = new CryptoReceiveAdapterDto();
                    BeanUtils.copyProperties(cryptoAppProtocolDto,cryptoReceiveAdapterDto);
                }
                cryptoTradePayOrderRes.setCryptoReceiveAdapters(cryptoReceiveAdapters);
            }
        }else {
            cryptoTradePayOrderRes.setCode(cryptoPayOrderRes.getCode());
            cryptoTradePayOrderRes.setMessage(cryptoPayOrderRes.getMessage());
        }
        logger.info("???????????????????????????,?????????:{} ????????????:{}",JSON.toJSONString(cryptoTradePayOrderRes));
        return cryptoTradePayOrderRes;
    }

    private CryptoTradeOrder createTradePayOrder(CryptoTradePayOrderReq cryptoTradePayOrderReq) {
        CryptoTradeOrder cryptoTradeOrder = new CryptoTradeOrder();
        cryptoTradeOrder.setMerchantId(cryptoTradePayOrderReq.getMerchantId());
        cryptoTradeOrder.setOrderNo(cryptoTradePayOrderReq.getOrderNo());
        cryptoTradeOrder.setTradeFlowNo(IDUtils.generateTradeFlowNo());
        cryptoTradeOrder.setOrderAmount(cryptoTradePayOrderReq.getOrderAmount());
        cryptoTradeOrder.setOrderCoin(cryptoTradePayOrderReq.getOrderCoin());
        cryptoTradeOrder.setOrderType(cryptoTradePayOrderReq.getBizOrderType());
        cryptoTradeOrder.setRecieveCoin(cryptoTradePayOrderReq.getReceiveCoin());
        cryptoTradeOrder.setReceiveNetworkCode(cryptoTradePayOrderReq.getReceiveNetworkCode());
        cryptoTradeOrder.setTradeStatus(TradeOrderStatusEnum.init.getCode());

        cryptoTradeOrder.setCreateTime(new Date());
        cryptoTradeOrder.setUpdateTime(new Date());
        cryptoTradeOrderMapper.insertSelective(cryptoTradeOrder);
        return cryptoTradeOrder;
    }

    private CryptoTradePreOrderRes cryptoTradePreOrderResponseHandle(CryptoTradeOrder cryptoTradeOrder,
                                                                     CryptoPreOrderRes cryptoPreOrderRes) {
        CryptoTradePreOrderRes cryptoTradePreOrderRes = new CryptoTradePreOrderRes();
        String orderNo = cryptoTradeOrder.getOrderNo();

        if(!ReturnCodeEnum.SUCCESS.getCode().equals(cryptoPreOrderRes.getCode())){
            logger.info("???????????????????????????,?????????:{} ?????????????????????????????????,???????????????????????????", JSON.toJSONString(cryptoPreOrderRes));
            cryptoTradeOrder.setTradeStatus(TradeOrderStatusEnum.fail.getCode());
            cryptoTradeOrder.setUpdateTime(new Date());
            cryptoTradeOrderMapper.updateByPrimaryKeySelective(cryptoTradeOrder);
            cryptoTradePreOrderRes.setCode(cryptoPreOrderRes.getCode());
            cryptoTradePreOrderRes.setMessage(cryptoPreOrderRes.getMessage());
        }else {
            //???????????????????????????
            cryptoTradeOrder.setUpdateTime(new Date());
            cryptoTradeOrder.setSysFlowNo(cryptoPreOrderRes.getBizFlowNo());
            cryptoTradePreOrderRes.setTradeFlowNo(cryptoTradeOrder.getTradeFlowNo());
            cryptoTradePreOrderRes.setLink(cryptoPreOrderRes.getLink());
        }
        cryptoTradeOrderMapper.updateByPrimaryKeySelective(cryptoTradeOrder);
        logger.info("???????????????????????????,?????????:{} ????????????:{}",orderNo,JSON.toJSONString(cryptoPreOrderRes));
        return cryptoTradePreOrderRes;
    }

    private CryptoTradeOrder createTradePreOrder(CryptoTradePreTradeOrderReq cryptoTradePreTradeOrderReq) {
        CryptoTradeOrder cryptoTradeOrder = new CryptoTradeOrder();
        cryptoTradeOrder.setMerchantId(cryptoTradePreTradeOrderReq.getMerchantId());
        cryptoTradeOrder.setOrderNo(cryptoTradePreTradeOrderReq.getOrderNo());
        cryptoTradeOrder.setTradeFlowNo(IDUtils.generateTradeFlowNo());
        cryptoTradeOrder.setOrderAmount(cryptoTradePreTradeOrderReq.getOrderAmount());
        cryptoTradeOrder.setOrderCoin(cryptoTradePreTradeOrderReq.getOrderCoin());
        cryptoTradeOrder.setOrderType(cryptoTradePreTradeOrderReq.getBizOrderType());
        cryptoTradeOrder.setTradeStatus(TradeOrderStatusEnum.init.getCode());

        cryptoTradeOrder.setCreateTime(new Date());
        cryptoTradeOrder.setUpdateTime(new Date());
        cryptoTradeOrderMapper.insertSelective(cryptoTradeOrder);
        return cryptoTradeOrder;
    }

    private CryptoTradeOrderPaySubmitRes cryptoTradeOrderPaySubmitResponseHandle(CryptoTradeOrder cryptoTradeOrder,
                                                                                 CryptoOrderSubmitPayRes cryptoOrderSubmitPayRes,
                                                                                 String receiveCoin,String receiveNetworkCode) {
        CryptoTradeOrderPaySubmitRes cryptoTradeOrderPaySubmitRes = new CryptoTradeOrderPaySubmitRes();
        if(!TradeOrderStatusEnum.success.getCode().equals(cryptoOrderSubmitPayRes.getCode())){
            //????????????
            cryptoTradeOrderPaySubmitRes.setCode(cryptoOrderSubmitPayRes.getCode());
            cryptoTradeOrderPaySubmitRes.setMessage(cryptoOrderSubmitPayRes.getMessage());
        }else {
            //????????????
            cryptoTradeOrder.setRecieveCoin(receiveCoin);
            cryptoTradeOrder.setReceiveNetworkCode(receiveNetworkCode);
            cryptoTradeOrder.setUpdateTime(new Date());
            cryptoTradeOrder.setReceiveAddress(cryptoTradeOrder.getReceiveAddress());
            cryptoTradeOrder.setCashierAddress(cryptoOrderSubmitPayRes.getLink());
            cryptoTradeOrderMapper.updateByPrimaryKeySelective(cryptoTradeOrder);
        }
        logger.info("????????????????????????????????????,???????????????:{} ????????????:{}",JSON.toJSONString(cryptoTradeOrder));
        return cryptoTradeOrderPaySubmitRes;
    }

    private CryptoTradeOrderPaySubmitRes checkPaySubmitCondition(String merchantId, String recieveCoin,String tradeFlowNo, String tradeStatus) {
        CryptoTradeOrderPaySubmitRes cryptoTradeOrderPaySubmitRes = new CryptoTradeOrderPaySubmitRes();
        if (TradeOrderStatusEnum.pending.getCode().equals(tradeStatus)
                || TradeOrderStatusEnum.init.getCode().equals(tradeStatus)) {
        } else {
            logger.info("???????????????????????????????????????,???????????????:{} ??????????????????????????????,????????????????????????", tradeFlowNo);
            cryptoTradeOrderPaySubmitRes.setReturnEnum(TradeReturnCodeEnum.ORDER_PAYMENT_OR_CLOSE_ERROR);
            return cryptoTradeOrderPaySubmitRes;
        }
        //??????????????????????????????
        boolean status = merchantConfigService.merchantSupportCoinStatus(merchantId, recieveCoin, CoinTypeEnum.crypto);
        if(!status){
            logger.info("???????????????????????????????????????,???????????????:{} ???????????????????????????:{} ", tradeFlowNo,recieveCoin);
            cryptoTradeOrderPaySubmitRes.setReturnEnum(TradeReturnCodeEnum.TRADE_COIN_NOT_SUPPORT_ERROR);
            return cryptoTradeOrderPaySubmitRes;
        }
        return cryptoTradeOrderPaySubmitRes;
    }
}
