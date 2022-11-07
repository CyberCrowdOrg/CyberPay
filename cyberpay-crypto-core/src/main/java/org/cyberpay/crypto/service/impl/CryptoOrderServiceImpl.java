package org.cyberpay.crypto.service.impl;

import com.alibaba.fastjson2.JSON;
import org.cyberpay.crypto.constant.RedisCacheKeyConstants;
import org.cyberpay.crypto.constant.RedisLockKeyConstants;
import org.cyberpay.crypto.dto.CryptoAppProtocolDto;
import org.cyberpay.crypto.dto.CryptoWalletProtocolDto;
import org.cyberpay.crypto.enums.CryptoNodeMethodEnum;
import org.cyberpay.crypto.enums.CryptoOrderStatusEnum;
import org.cyberpay.crypto.enums.ReturnCodeEnum;
import org.cyberpay.crypto.mapper.CryptoOrderExtendMapper;
import org.cyberpay.crypto.mapper.CryptoOrderMapper;
import org.cyberpay.crypto.mapper.CryptoSupportNetworkMapper;
import org.cyberpay.crypto.model.*;
import org.cyberpay.crypto.node.NodeRequestDto;
import org.cyberpay.crypto.node.NodeResponseDto;
import org.cyberpay.crypto.request.*;
import org.cyberpay.crypto.response.*;
import org.cyberpay.crypto.service.*;
import org.cyberpay.crypto.service.activemq.CryptoTradeResultMessageDto;
import org.cyberpay.crypto.service.activemq.ProducerService;
import org.cyberpay.crypto.utils.IDUtils;
import org.cyberpay.crypto.utils.RedissonLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service("cryptoOrderService")
public class CryptoOrderServiceImpl implements CryptoOrderService {

    private Logger logger = LoggerFactory.getLogger(CryptoOrderServiceImpl.class);

    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    BaseCryptoCoinService baseCryptoCoinService;
    @Autowired
    CoinExchangeRateService coinExchangeRateService;
    @Autowired
    CryptoWalletProtocolService cryptoWalletProtocolService;
    @Autowired
    CryptoNodeMappingService cryptoNodeMappingService;
    @Autowired
    AddressPoolService addressPoolService;
    @Autowired
    NodeService nodeService;
    @Autowired
    ProducerService producerService;
    @Autowired
    RedissonClient redissonClient;

    @Autowired
    CryptoSupportNetworkMapper cryptoSupportNetworkMapper;
    @Autowired
    CryptoOrderExtendMapper cryptoOrderExtendMapper;
    @Autowired
    CryptoOrderMapper cryptoOrderMapper;

    @Override
    public CryptoPreOrderRes preOrder(CryptoPreOrderReq cryptoPreOrderReq) {
        logger.info("加密币支付,预订单业务处理接口,请求入参{}", JSON.toJSONString(cryptoPreOrderReq));
        CryptoPreOrderRes cryptoPreOrderRes = new CryptoPreOrderRes();
        String orderNo = cryptoPreOrderReq.getOrderNo();

        RedissonLock redissonLock = new RedissonLock(redissonClient,
                MessageFormat.format(RedisLockKeyConstants.CRYPTO_ORDER_CRATE_LOCK_KEY,orderNo));
        try {
            if(redissonLock.lock()){
                //验证订单是否已存在
                CryptoOrder cryptoOrder = cryptoOrderMapper.findOrder(orderNo);
                if (null != cryptoOrder) {
                    logger.info("加密币支付,预订单业务处理接口,订单号:{} 订单已存在", orderNo);
                    cryptoPreOrderRes.setReturnEnum(ReturnCodeEnum.CRYPTO_ORDER_ALREADY_EXISTS_ERROR);
                    return cryptoPreOrderRes;
                }
                //验证订单币种是否支持
                String orderCoin = cryptoPreOrderReq.getOrderCoin();
                if (baseCryptoCoinService.isSupportCoin(orderCoin)) {
                    //创建订单
                    BigDecimal orderAmount = cryptoPreOrderReq.getOrderAmount();
                    String bizOrderType = cryptoPreOrderReq.getBizOrderType();
                    cryptoOrder = newPreOrder(orderNo, orderCoin, orderAmount, bizOrderType);
                    //处理响应数据
                    cryptoPreOrderRes.setBizFlowNo(cryptoOrder.getBizFlowNo());
                    cryptoPreOrderRes.setLink("https://www.baidu.com");
                } else {
                    logger.info("加密币支付,预订单业务处理接口,订单号:{} 当前币种:{} 目前不支持交易", orderNo, orderCoin);
                    cryptoPreOrderRes.setReturnEnum(ReturnCodeEnum.TRADE_COIN_NOT_SUPPORT_ERROR);
                    return cryptoPreOrderRes;
                }
                return cryptoPreOrderResponseHandle(cryptoOrder,cryptoPreOrderRes);
            }else {
                logger.info("加密币支付,预订单业务处理接口,订单号:{} 获取Redis锁失败", orderNo);
                cryptoPreOrderRes.setReturnEnum(ReturnCodeEnum.FAIL);
                return cryptoPreOrderRes;
            }
        }catch (Exception e){
            logger.error("加密币支付,预订单业务处理接口,订单号:{} 执行异常:{}", orderNo, e.getMessage(),e);
            cryptoPreOrderRes.setReturnEnum(ReturnCodeEnum.FAIL);
        }finally {
            redissonLock.unlock();
        }
        logger.info("加密币支付,预订单业务处理接口,响应结果:{}",JSON.toJSONString(cryptoPreOrderRes));
        return cryptoPreOrderRes;
    }

    @Override
    public CryptoPayOrderRes payOrder(CryptoPayOrderReq cryptoPayOrderReq) {
        CryptoPayOrderRes cryptoPayOrderRes =  new CryptoPayOrderRes();
        String orderNo = cryptoPayOrderReq.getOrderNo();
        logger.info("加密币支付,支付订单业务处理接口,请求入参{}", JSON.toJSONString(cryptoPayOrderRes));

        RedissonLock redissonLock = new RedissonLock(redissonClient,
                MessageFormat.format(RedisLockKeyConstants.CRYPTO_ORDER_CRATE_LOCK_KEY,orderNo));
        try {
            if(redissonLock.lock()){
                //验证订单是否已存在
                CryptoOrder cryptoOrder = cryptoOrderMapper.findOrder(orderNo);
                if (null != cryptoOrder) {
                    logger.info("加密币支付,支付订单业务处理接口,订单号:{} 订单已存在", orderNo);
                    cryptoPayOrderRes.setReturnEnum(ReturnCodeEnum.CRYPTO_ORDER_ALREADY_EXISTS_ERROR);
                    return cryptoPayOrderRes;
                }

                //验证订单币种是否支持
                //验证收款币种是否存在
                String orderCoin = cryptoPayOrderReq.getOrderCoin();
                String receiveCoin = cryptoPayOrderReq.getReceiveCoin();
                if(!baseCryptoCoinService.isSupportCoin(orderCoin)
                        || !baseCryptoCoinService.isSupportCoin(receiveCoin)) {
                    logger.info("加密币支付,支付订单业务处理接口,订单号:{} 订单币种:{} 收款币种:{} 不支持的交易对",orderCoin,receiveCoin);
                    cryptoPayOrderRes.setReturnEnum(ReturnCodeEnum.TRADE_COIN_NOT_SUPPORT_ERROR);
                    return cryptoPayOrderRes;
                }

                //查询 node mapping信息
                String receiveNetworkCode = cryptoPayOrderReq.getReceiveNetworkCode();
                CryptoNodeMapping nodeMapping = cryptoNodeMappingService.findNodeMapping(receiveCoin, receiveNetworkCode);
                if(null == nodeMapping){
                    logger.info("加密币支付,支付订单业务处理接口,订单号:{} 收款币种:{} 收款网络:{} " +
                            "未查询到Node Mapping 信息",orderCoin,receiveCoin);
                    //暂不支持交易
                    cryptoPayOrderRes.setReturnEnum(ReturnCodeEnum.TRADE_NOT_SUPPORT_ERROR);
                    return cryptoPayOrderRes;
                }

                //获取真实交易汇率
                BigDecimal realRate = getLatestCryptoRate(orderCoin,receiveCoin);
                if(null == realRate){
                    logger.info("加密币支付,支付订单业务处理接口,订单号:{} 未查询到交易汇率",orderNo,orderCoin,receiveCoin);
                    //暂不支持交易
                    cryptoPayOrderRes.setReturnEnum(ReturnCodeEnum.TRADE_NOT_SUPPORT_ERROR);
                    return cryptoPayOrderRes;
                }

                //获取订单交易汇率
                BigDecimal orderRate = realRate;
                BigDecimal floatingRate = cryptoPayOrderReq.getFloatingRate();
                if(null != floatingRate && floatingRate.compareTo(BigDecimal.ZERO) > 0){
                    orderRate = orderRate.add(orderRate.multiply(floatingRate));
                }
                logger.info("加密币支付,支付订单业务处理接口,订单号:{} 真实汇率:{} 订单汇率:{}",orderNo,realRate,orderRate);

                //查询加密币支持网络数据
                CryptoSupportNetwork cryptoSupportNetwork = cryptoSupportNetworkMapper.
                        queryAvailableCryptoSupportNetwork(receiveCoin, receiveNetworkCode);
                //创建订单
                cryptoOrder = newCryptoPayOrder(cryptoPayOrderReq, cryptoSupportNetwork, orderRate);

                //分配交易地址
                NodeRequestDto nodeRequestDto = new NodeRequestDto();
                nodeRequestDto.setCryptoNodeMapping(nodeMapping);
                NodeResponseDto nodeResponseDto = nodeService.callNode(
                        nodeRequestDto, CryptoNodeMethodEnum.generateAddress);
                if(null == nodeResponseDto || !ReturnCodeEnum.SUCCESS.getCode().equals(nodeResponseDto.getReturnCode())){
                    //调用节点异常
                    logger.info("加密币支付,支付订单业务处理接口,订单号:{} 调用节点异常",orderNo);
                    cryptoPayOrderRes.setReturnEnum(ReturnCodeEnum.FAIL);
                    cryptoOrder.setStatus(CryptoOrderStatusEnum.fail.getCode());
                    cryptoOrder.setUpdateTime(new Date());
                }else {
                    //更新订单地址
                    cryptoOrder.setReceiveAddress(nodeResponseDto.getAddress());
                    cryptoOrder.setStatus(CryptoOrderStatusEnum.unpaid.getCode());
                    cryptoOrder.setUpdateTime(new Date());
                    //创建扩展订单
                    newCryptoPayOrderExtend(cryptoOrder, realRate, orderRate,
                            null, null, null, null);
                }
                //处理响应数据
                return cryptoOrderResponseHandle(cryptoOrder, cryptoPayOrderRes);
            }else {
                logger.info("加密币支付,支付订单业务处理接口,订单号:{} 获取Redis锁失败", orderNo);
                cryptoPayOrderRes.setReturnEnum(ReturnCodeEnum.FAIL);
                return cryptoPayOrderRes;
            }
        }catch (Exception e){
            logger.error("加密币支付,支付订单业务处理接口,订单号:{} 执行异常:{}",e.getMessage(),e);
            cryptoPayOrderRes.setReturnEnum(ReturnCodeEnum.FAIL);
            return cryptoPayOrderRes;
        }finally {
            redissonLock.unlock();
        }
    }

    @Override
    public CryptoRefundOrderRes refundOrder(CryptoRefundOrderReq cryptoRefundOrderReq) {
        CryptoRefundOrderRes cryptoRefundOrderRes = new CryptoRefundOrderRes();
        String refundOrderNo = cryptoRefundOrderReq.getRefundOrderNo();

        RedissonLock redissonLock = new RedissonLock(redissonClient,
                MessageFormat.format(RedisLockKeyConstants.CRYPTO_ORDER_CRATE_LOCK_KEY,refundOrderNo));
        try {
            if(redissonLock.lock()){
                //检查原订单号是否存在已退款或者退款中的退款订单
                String orgOrderNo = cryptoRefundOrderReq.getOrgOrderNo();
                CryptoOrder refundOrder2 = cryptoOrderMapper.findRefundOrderByOrgOrderNo(orgOrderNo);
                if(null != refundOrder2
                        && !CryptoOrderStatusEnum.close.getCode().equals(refundOrder2.getStatus())){
                    //重复提交,退款已处理
                    logger.info("加密币支付,退款订单业务处理接口,原订单号:{} 退款已处理,不能再次发起退款", orgOrderNo);
                    cryptoRefundOrderRes.setReturnEnum(ReturnCodeEnum.CRYPTO_ORDER_REPEAT_REFUND_ERROR);
                    return cryptoRefundOrderRes;
                }

                //验证退款订单号是否已存在
                CryptoOrder refundOrder = cryptoOrderMapper.findOrder(refundOrderNo);
                if (null != refundOrder) {
                    logger.info("加密币支付,退款订单业务处理接口,订单号:{} 订单已存在", refundOrderNo);
                    cryptoRefundOrderRes.setReturnEnum(ReturnCodeEnum.CRYPTO_ORDER_ALREADY_EXISTS_ERROR);
                    return cryptoRefundOrderRes;
                }

                //验证原订单是否满足退款条件
                CryptoOrder orgCryptoOrder = cryptoOrderMapper.findOrder(orgOrderNo);
                if (null != orgCryptoOrder) {
                    logger.info("加密币支付,退款订单业务处理接口,原订单号:{} 订单不存在,不能发起退款", orgOrderNo);
                    cryptoRefundOrderRes.setReturnEnum(ReturnCodeEnum.CRYPTO_ORDER_ORG_NOT_FOUND_ERROR);
                    return cryptoRefundOrderRes;
                }else if(!CryptoOrderStatusEnum.lsuccess.getCode().equals(orgCryptoOrder.getStatus())
                        || !CryptoOrderStatusEnum.msuccess.getCode().equals(orgCryptoOrder.getStatus())
                        || !CryptoOrderStatusEnum.success.getCode().equals(orgCryptoOrder.getStatus())){
                    logger.info("加密币支付,退款订单业务处理接口,原订单号:{} 非付款成功状态,不能发起退款", orgOrderNo);
                    cryptoRefundOrderRes.setReturnEnum(ReturnCodeEnum.CRYPTO_ORDER_NOT_PAY_REFUND_ERROR);
                    return cryptoRefundOrderRes;
                }

                //查询node mapping信息
                String receiveCoin = orgCryptoOrder.getReceiveCoin();
                String receiveNetworkCode = orgCryptoOrder.getReceiveNetworkCode();
                CryptoNodeMapping nodeMapping = cryptoNodeMappingService.findNodeMapping(receiveCoin, receiveNetworkCode);
                if(null == nodeMapping){
                    logger.info("加密币支付,退款订单业务处理接口,订单号:{} 收款币种:{} 收款网络:{} " +
                            "未查询到Node Mapping 信息",refundOrderNo,receiveCoin,receiveCoin);
                    //暂不支持交易
                    cryptoRefundOrderRes.setReturnEnum(ReturnCodeEnum.TRADE_NOT_SUPPORT_ERROR);
                    return cryptoRefundOrderRes;
                }
                //创建退款订单
                String refundAddress = cryptoRefundOrderReq.getRefundAddress();
                refundOrder = newCryptoRefundOrder(orgCryptoOrder,refundOrderNo,refundAddress);

                //发起退款转账
                NodeRequestDto nodeRequestDto = new NodeRequestDto();
                nodeRequestDto.setCryptoNodeMapping(nodeMapping);
                NodeResponseDto nodeResponseDto = nodeService.callNode(nodeRequestDto, CryptoNodeMethodEnum.sendTransaction);
                if(null == nodeResponseDto || !ReturnCodeEnum.SUCCESS.getCode().equals(nodeResponseDto.getReturnCode())){
                    //调用节点异常
                    logger.info("加密币支付,退款订单业务处理接口,订单号:{} 调用节点异常",refundOrderNo);
                    //更新订单状态,付款失败
                    refundOrder.setStatus(CryptoOrderStatusEnum.fail.getCode());
                    refundOrder.setUpdateTime(new Date());
                    cryptoRefundOrderRes.setReturnEnum(ReturnCodeEnum.FAIL);
                }else {
                    //创建订单扩展数据
                    newCryptoPayOrderExtend(refundOrder,null,null,
                            nodeResponseDto.getFromAddress(),nodeResponseDto.getTransHash(),nodeResponseDto.getBlockHash()
                            ,nodeResponseDto.getBlockNumber());
                    //更新订单状态,未确认
                    refundOrder.setStatus(CryptoOrderStatusEnum.unconfirmed.getCode());
                    refundOrder.setUpdateTime(new Date());
                }
                //处理响应信息
                return refundOrderResponseHandle(refundOrder,cryptoRefundOrderRes,nodeResponseDto.getTransHash());
            }else {
                logger.info("加密币支付,退款订单业务处理接口,订单号:{} 获取Redis锁失败", refundOrderNo);
                cryptoRefundOrderRes.setReturnEnum(ReturnCodeEnum.FAIL);
                return cryptoRefundOrderRes;
            }
        }catch (Exception e){
            logger.error("加密币支付,退款订单业务处理接口,订单号:{} 执行异常:{}",e.getMessage(),e);
            cryptoRefundOrderRes.setReturnEnum(ReturnCodeEnum.FAIL);
            return cryptoRefundOrderRes;
        }finally {
            redissonLock.unlock();
        }
    }

    @Override
    public CryptoOrderSubmitPayRes submitPayOrder(CryptoOrderSubmitPayReq cryptoOrderSubmitPayReq) {
        CryptoOrderSubmitPayRes cryptoOrderSubmitPayRes = new CryptoOrderSubmitPayRes();
        String orderNo = cryptoOrderSubmitPayReq.getOrderNo();

        RedissonLock redissonLock = null;
        try{
            //验证订单是否存在
            CryptoOrder cryptoOrder = cryptoOrderMapper.findOrder(orderNo);
            if (null == cryptoOrder) {
                logger.info("加密币支付,提交支付业务处理接口,订单号:{} 订单未找到", orderNo);
                cryptoOrderSubmitPayRes.setReturnEnum(ReturnCodeEnum.CRYPTO_ORDER_NOT_FOUND_ERROR);
                return cryptoOrderSubmitPayRes;
            }

            redissonLock = new RedissonLock(redissonClient,
                    MessageFormat.format(RedisLockKeyConstants.CRYPTO_ORDER_UPDATE_LOCK_KEY,cryptoOrder.getBizFlowNo()));

            if(redissonLock.lock()){
                //再次查询订单信息
                cryptoOrder = cryptoOrderMapper.findOrder(orderNo);

                //验证是否符合提交支付的条件
                String receiveCoin = cryptoOrderSubmitPayReq.getReceiveCoin();
                cryptoOrderSubmitPayRes = checkCryptoOrderSubmitPayConditions(cryptoOrder.getBizOrderNo(),cryptoOrder.getStatus(),receiveCoin);
                if(!ReturnCodeEnum.SUCCESS.getCode().equals(cryptoOrderSubmitPayRes.getCode())){
                    return cryptoOrderSubmitPayRes;
                }

                //验证当前提交支付信息与订单是否一致,一致则更新订单金额、汇率、时间等数据,并返回正确响应数据
                String orderReceiveCoin = cryptoOrder.getReceiveCoin();
                String orderReceiveNetworkCode = cryptoOrder.getReceiveNetworkCode();
                String receiveNetworkCode = cryptoOrderSubmitPayReq.getReceiveNetworkCode();
                if(receiveCoin.equals(orderReceiveCoin)
                        && receiveNetworkCode.equals(orderReceiveNetworkCode)){
                    logger.info("加密币支付,提交支付业务处理接口,订单号:{} 已提交支付信息与当前提交支付信息一致, 直接返回结果数据",orderNo);
                    return cryptoOrderSubmitPayResponseHandle(cryptoOrder,cryptoOrderSubmitPayRes);
                }

                //查询 node mapping信息
                String orderCoin = cryptoOrder.getOrderCoin();
                CryptoNodeMapping nodeMapping = cryptoNodeMappingService.findNodeMapping(receiveCoin, receiveNetworkCode);
                if(null == nodeMapping){
                    logger.info("加密币支付,提交支付业务处理接口,订单号:{} 收款币种:{} 收款网络:{} " +
                            "未查询到Node Mapping 信息",orderNo,receiveCoin,receiveNetworkCode);
                    //暂不支持交易
                    cryptoOrderSubmitPayRes.setReturnEnum(ReturnCodeEnum.FAIL);
                    return cryptoOrderSubmitPayRes;
                }

                //获取真实交易汇率
                BigDecimal realRate = getLatestCryptoRate(orderCoin,receiveCoin);
                if(null == realRate){
                    logger.info("加密币支付,提交支付业务处理接口,订单号:{} 未查询到交易汇率",orderNo,orderCoin,receiveCoin);
                    //暂不支持交易
                    cryptoOrderSubmitPayRes.setReturnEnum(ReturnCodeEnum.FAIL);
                    return cryptoOrderSubmitPayRes;
                }

                //获取订单交易汇率
                BigDecimal orderRate = realRate;
                BigDecimal floatingRate = cryptoOrderSubmitPayReq.getFloatingRate();
                if(null != floatingRate && floatingRate.compareTo(BigDecimal.ZERO) > 0){
                    orderRate = orderRate.add(orderRate.multiply(floatingRate));
                }
                logger.info("加密币支付,支付订单业务处理接口,订单号:{} 真实汇率:{} 订单汇率:{}",orderNo,realRate,orderRate);

                //归还订单已分配地址
                retrunOrderReceiveAddress(cryptoOrder);
                //分配交易地址
                NodeRequestDto nodeRequestDto = new NodeRequestDto();
                nodeRequestDto.setCryptoNodeMapping(nodeMapping);
                NodeResponseDto nodeResponseDto = nodeService.callNode(nodeRequestDto, CryptoNodeMethodEnum.generateAddress);
                if(null == nodeResponseDto || !ReturnCodeEnum.SUCCESS.getCode().equals(nodeResponseDto.getReturnCode())){
                    //调用节点异常
                    logger.info("加密币支付,支付订单业务处理接口,订单号:{} 调用节点异常",orderNo);
                    cryptoOrderSubmitPayRes.setReturnEnum(ReturnCodeEnum.FAIL);
                    cryptoOrder.setStatus(CryptoOrderStatusEnum.fail.getCode());
                    cryptoOrder.setUpdateTime(new Date());
                }else {
                    String address = nodeResponseDto.getAddress();
                    //set 订单地址,收款金额等信息
                    setLatestOrderPayInfo(cryptoOrder,address,realRate,receiveCoin,receiveNetworkCode);
                    //创建或者更新扩展订单
                    newCryptoPayOrderExtend(cryptoOrder, realRate, orderRate,
                            null, null, null, null);
                }
                return cryptoOrderSubmitPayResponseHandle(cryptoOrder,cryptoOrderSubmitPayRes);
            }else {
                logger.info("加密币支付,提交支付业务处理接口,订单号:{} 获取Redis锁失败", orderNo);
                cryptoOrderSubmitPayRes.setReturnEnum(ReturnCodeEnum.FAIL);
                return cryptoOrderSubmitPayRes;
            }
        }catch (Exception e){
            logger.error("加密币支付,支付订单业务处理接口,订单号:{} 执行异常:{}",orderNo,e.getMessage(),e);
            cryptoOrderSubmitPayRes.setReturnEnum(ReturnCodeEnum.FAIL);
            return cryptoOrderSubmitPayRes;
        }finally {
            redissonLock.unlock();
        }
    }

    @Override
    public void confirmOrder(String coin, String networkCode) {
        logger.info("加密币订单交易确认业务处理接口,coin:{} networkCode:{}",coin,networkCode);
        //查询待确认交易订单扩展数据
        List<CryptoOrderExtend> orderExtends =
                cryptoOrderExtendMapper.findUnconfirmedOrderExtend(coin,networkCode);

        if(null != orderExtends && orderExtends.size() > 0){
            logger.info("加密币订单交易确认业务处理接口,coin:{} networkCode:{} 待处理数据:{}",coin,networkCode,orderExtends.size());
            for(CryptoOrderExtend cryptoOrderExtend: orderExtends){
                String bizFlowNo = cryptoOrderExtend.getBizFlowNo();
                try {
                    confirmOrderHandle(cryptoOrderExtend, coin, networkCode);
                }catch (Exception e){
                    logger.info("加密币订单交易确认业务处理接口,业务流水号:{} 执行异常:{}",bizFlowNo);
                }
            }
        }else {
            logger.info("加密币订单交易确认业务处理接口,coin:{} networkCode:{} 没有需要处理的数据",coin,networkCode);
        }

    }

    @Override
    public void closeOrder(String coin, String networkCode, long timeout) {
        LocalDate localDate = LocalDate.now();
        localDate.minus(timeout, ChronoUnit.MINUTES);
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDate.atStartOfDay().atZone(zone).toInstant();
        Date endTime = Date.from(instant);
        //查询满足时间条件的待关闭订单
        List<CryptoOrder> cryptoOrders = cryptoOrderMapper.queryPendingCloseOrders(coin,networkCode,endTime);
        if(null != cryptoOrders && cryptoOrders.size() > 0){
            for(CryptoOrder cryptoOrder:cryptoOrders){
                try {
                    closeOrderHandle(cryptoOrder);
                }catch (Exception e){
                    logger.error("订单关闭业务处理,执行异常:{}",e.getMessage(),e);
                }
            }
        }
    }

    private void closeOrderHandle(CryptoOrder cryptoOrder) throws Exception{
        String bizFlowNo = cryptoOrder.getBizFlowNo();
        RedissonLock redissonLock = new RedissonLock(redissonClient,
                MessageFormat.format(RedisLockKeyConstants.CRYPTO_ORDER_UPDATE_LOCK_KEY,bizFlowNo));
        if(redissonLock.lock()) {
            cryptoOrder.setUpdateTime(new Date());
            cryptoOrder.setCloseTime(new Date());
            cryptoOrder.setStatus(CryptoOrderStatusEnum.close.getCode());
            cryptoOrderMapper.updateByPrimaryKeySelective(cryptoOrder);
            //归还地址
            String receiveNetworkCode = cryptoOrder.getReceiveNetworkCode();
            if(StringUtils.hasText(receiveNetworkCode)) {
                addressPoolService.returnAddressToAddressPoolCache(
                        cryptoOrder.getReceiveNetworkCode(),
                        cryptoOrder.getReceiveAddress(), 0);
            }
        }else {
        }
    }

    private void confirmOrderHandle(CryptoOrderExtend cryptoOrderExtend,String coin,String networkCode) throws Exception{
        Long confirm = cryptoOrderExtend.getConfirm();
        String bizFlowNo = cryptoOrderExtend.getBizFlowNo();
        String transHash = cryptoOrderExtend.getTransHash();

        //查询 node mapping信息
        CryptoNodeMapping nodeMapping = cryptoNodeMappingService.findNodeMapping(coin, networkCode);
        NodeRequestDto nodeRequestDto = new NodeRequestDto();
        nodeRequestDto.setCryptoNodeMapping(nodeMapping);
        nodeRequestDto.setTransHash(transHash);
        NodeResponseDto nodeResponseDto = nodeService.callNode(nodeRequestDto, CryptoNodeMethodEnum.confirmTransaction);
        if(!ReturnCodeEnum.SUCCESS.getCode().equals(nodeResponseDto.getReturnCode())) {
            logger.info("加密币订单交易结果确认业务处理,业务流水号:{} 交易hash:{}" +
                    " 调用Node失败:{}",bizFlowNo,transHash,JSON.toJSONString(nodeResponseDto));
        }else {
            Long blockConfirm = nodeMapping.getBlockConfirm();
            confirm = confirm + nodeResponseDto.getConfirm();

            RedissonLock redissonLock = new RedissonLock(
                    redissonClient,MessageFormat.format(RedisLockKeyConstants.CRYPTO_ORDER_UPDATE_LOCK_KEY,bizFlowNo));
            try{
                if(redissonLock.lock()) {
                    if (confirm.intValue() >= blockConfirm.intValue()) {
                        logger.info("加密币订单交易结果确认业务处理,,业务流水号:{} 交易hash:{} 区块已确认数量:{} 已满足确认交易条件", bizFlowNo, transHash, confirm);
                        CryptoOrder cryptoOrderReq = new CryptoOrder();
                        cryptoOrderReq.setBizFlowNo(bizFlowNo);
                        cryptoOrderReq.setReceiveNetworkCode(networkCode);
                        cryptoOrderReq.setReceiveCoin(coin);
                        CryptoOrder cryptoOrder = cryptoOrderMapper.selectCryptoOrder(cryptoOrderReq);

                        BigDecimal diffReceiveAmount = cryptoOrder.getDiffReceiveAmount();
                        //判断付款成功状态
                        if (null == diffReceiveAmount || diffReceiveAmount.compareTo(BigDecimal.ZERO) == 0) {
                            cryptoOrder.setStatus(CryptoOrderStatusEnum.success.getCode());
                            logger.info("加密币订单交易结果确认业务处理,,业务流水号:{} 交易hash:{} 更新确认交易状态:{}", bizFlowNo, transHash, CryptoOrderStatusEnum.success.getCode());
                        } else if (diffReceiveAmount.compareTo(BigDecimal.ZERO) < 0) {
                            cryptoOrder.setStatus(CryptoOrderStatusEnum.lsuccess.getCode());
                            logger.info("加密币订单交易结果确认业务处理,,业务流水号:{} 交易hash:{} 更新确认交易状态:{}", bizFlowNo, transHash, CryptoOrderStatusEnum.lsuccess.getCode());
                        } else if (diffReceiveAmount.compareTo(BigDecimal.ZERO) > 0) {
                            cryptoOrder.setStatus(CryptoOrderStatusEnum.msuccess.getCode());
                            logger.info("加密币订单交易结果确认业务处理,,业务流水号:{} 交易hash:{} 更新确认交易状态:{}", bizFlowNo, transHash, CryptoOrderStatusEnum.msuccess.getCode());
                        }
                        cryptoOrder.setUpdateTime(new Date());
                        cryptoOrder.setSuccessTime(new Date());
                        cryptoOrderMapper.updateByPrimaryKeySelective(cryptoOrder);
                        //归还地址,积分+1
                        addressPoolService.returnAddressToAddressPoolCache(
                                cryptoOrder.getReceiveNetworkCode(),
                                cryptoOrder.getReceiveAddress(), 1);

                        producerService.cryptoTradeResultMessage(cryptoOrder);
                    } else {
                        //交易还未确认,状态不用修改
                        logger.info("加密币订单交易结果确认业务处理,,业务流水号:{} 交易hash:{} 区块已确认数量:{} 暂未满足确认条件", bizFlowNo, transHash, confirm);
                    }
                    cryptoOrderExtend.setBlockNumber(nodeResponseDto.getBlockNumber());
                    cryptoOrderExtend.setBlockHash(nodeResponseDto.getBlockHash());
                    cryptoOrderExtend.setConfirm(confirm);
                    cryptoOrderExtend.setUpdateTime(new Date());
                    cryptoOrderExtendMapper.updateByPrimaryKeySelective(cryptoOrderExtend);
                }else {
                    logger.info("加密币订单交易结果确认业务处理,,业务流水号:{} 交易hash:{} 获取更新锁失败", bizFlowNo, transHash);
                }
            }catch (Exception e){
                throw  e;
            }finally {
                redissonLock.unlock();
            }
        }
    }

    private CryptoOrderSubmitPayRes checkCryptoOrderSubmitPayConditions(String bizOrderNo,String status, String receiveCoin) {
        CryptoOrderSubmitPayRes cryptoOrderSubmitPayRes = new CryptoOrderSubmitPayRes();

        //验证收款币种是否存在
        if(!baseCryptoCoinService.isSupportCoin(receiveCoin)) {
            logger.info("加密币支付,提交支付业务处理接口,订单号:{} 收款币种:{} 不支持的交易币种",receiveCoin);
            cryptoOrderSubmitPayRes.setReturnEnum(ReturnCodeEnum.TRADE_COIN_NOT_SUPPORT_ERROR);
            return cryptoOrderSubmitPayRes;
        }

        if(CryptoOrderStatusEnum.unconfirmed.getCode().equals(status)){
            logger.info("加密币支付,提交支付业务处理接口,订单号:{} 不符合提交条件, 订单已付款正在处理中",bizOrderNo);
            cryptoOrderSubmitPayRes.setReturnEnum(ReturnCodeEnum.CRYPTO_ORDER_PAYING_ERROR);
            return cryptoOrderSubmitPayRes;
        }else if(CryptoOrderStatusEnum.lsuccess.getCode().equals(status)
                || CryptoOrderStatusEnum.msuccess.getCode().equals(status)
                || CryptoOrderStatusEnum.success.getCode().equals(status)){
            logger.info("加密币支付,提交支付业务处理接口,订单号:{} 不符合提交条件, 订单已付款成功",bizOrderNo);
            cryptoOrderSubmitPayRes.setReturnEnum(ReturnCodeEnum.CRYPTO_ORDER_PAYMENT_OR_CLOSE_ERROR);
            return cryptoOrderSubmitPayRes;
        }else if(CryptoOrderStatusEnum.close.getCode().equals(status)){
            logger.info("加密币支付,提交支付业务处理接口,订单号:{} 不符合提交条件, 订单超时关闭",bizOrderNo);
            cryptoOrderSubmitPayRes.setReturnEnum(ReturnCodeEnum.CRYPTO_ORDER_PAYMENT_OR_CLOSE_ERROR);
            return cryptoOrderSubmitPayRes;
        }
        return cryptoOrderSubmitPayRes;
    }

    private CryptoPreOrderRes cryptoPreOrderResponseHandle(CryptoOrder cryptoOrder, CryptoPreOrderRes cryptoPreOrderRes) {

        String bizFlowNo = cryptoOrder.getBizFlowNo();
        String bizOrderNo = cryptoOrder.getBizOrderNo();
        if(ReturnCodeEnum.SUCCESS.getCode().equals(cryptoPreOrderRes.getCode())) {
            //保存
            cryptoOrderMapper.insertSelective(cryptoOrder);
            //创建redis缓存,允许订单在前端进行访问,当订单状态终止后删除(支付成功、订单关闭)
            //根据bizFlowNo和时间戳得到Redis缓存Key,并设置过期时间
            String accessKey = DigestUtils.md5DigestAsHex(
                    MessageFormat.format("{0}{1}", bizFlowNo, System.currentTimeMillis()).getBytes());
            redisTemplate.opsForValue().set(
                    MessageFormat.format(
                            RedisCacheKeyConstants.WEB_CASHIER_API_ACCESS_KEY_CACHE, accessKey),
                    bizOrderNo, 50, TimeUnit.MINUTES);
            //Web端访问地址
            cryptoPreOrderRes.setLink("https://www.baidu.com?access_key=" + accessKey);
        }
        return cryptoPreOrderRes;
    }

    private CryptoOrderSubmitPayRes cryptoOrderSubmitPayResponseHandle(CryptoOrder cryptoOrder, CryptoOrderSubmitPayRes cryptoOrderSubmitPayRes) {

        String code = cryptoOrderSubmitPayRes.getCode();

        cryptoOrderMapper.updateByPrimaryKeySelective(cryptoOrder);

        if(ReturnCodeEnum.SUCCESS.getCode().equals(code)){

            String receiveAddress = cryptoOrder.getReceiveAddress();
            String bizFlowNo = cryptoOrder.getBizFlowNo();
            String receiveCoin = cryptoOrder.getReceiveCoin();
            BigDecimal receiveAmount = cryptoOrder.getReceiveAmount();
            String bizOrderNo = cryptoOrder.getBizOrderNo();

            //查询加密币钱包协议支持数据,适配钱包
            List<CryptoAppProtocolDto> cryptoAppProtocolList = cryptoWalletProtocolAdapter(receiveCoin,receiveAmount,receiveAddress);
            cryptoOrderSubmitPayRes.setCryptoAppProtocolList(cryptoAppProtocolList);

            cryptoOrderSubmitPayRes.setBizFlowNo(bizFlowNo);
            cryptoOrderSubmitPayRes.setReceiveAmount(receiveAmount);
            cryptoOrderSubmitPayRes.setReceiveAddress(receiveAddress);
            cryptoOrderSubmitPayRes.setReceiveCoin(receiveCoin);
            cryptoOrderSubmitPayRes.setCryptoAppProtocolList(cryptoAppProtocolList);
            //创建redis缓存,允许订单在前端进行访问,当订单状态终止后删除(支付成功、订单关闭)
            //根据bizFlowNo和时间戳得到Redis缓存Key,并设置过期时间
            String accessKey = DigestUtils.md5DigestAsHex(
                    MessageFormat.format("{0}{1}",bizFlowNo,System.currentTimeMillis()).getBytes());
            redisTemplate.opsForValue().set(
                    MessageFormat.format(
                            RedisCacheKeyConstants.WEB_CASHIER_API_ACCESS_KEY_CACHE,accessKey),
                    bizOrderNo,50, TimeUnit.MINUTES);
            //Web端访问地址
            cryptoOrderSubmitPayRes.setLink("https://www.baidu.com?access_key=" + accessKey);
        }
        return cryptoOrderSubmitPayRes;
    }

    private CryptoRefundOrderRes refundOrderResponseHandle(CryptoOrder refundOrder, CryptoRefundOrderRes cryptoRefundOrderRes,String transHash) {

        cryptoOrderMapper.updateByPrimaryKeySelective(refundOrder);

        if(ReturnCodeEnum.SUCCESS.getCode().equals(cryptoRefundOrderRes.getCode())){
            cryptoRefundOrderRes.setTransHash(transHash);
            cryptoRefundOrderRes.setBizFlowNo(refundOrder.getBizFlowNo());
        }

        return cryptoRefundOrderRes;
    }

    private CryptoPayOrderRes cryptoOrderResponseHandle(CryptoOrder cryptoOrder, CryptoPayOrderRes cryptoPayOrderRes) {
        String bizOrderNo = cryptoOrder.getBizOrderNo();
        //更新订单
        cryptoOrderMapper.updateByPrimaryKeySelective(cryptoOrder);

        if(ReturnCodeEnum.SUCCESS.getCode().equals(cryptoPayOrderRes.getCode())){

            String receiveAddress = cryptoOrder.getReceiveAddress();
            String bizFlowNo = cryptoOrder.getBizFlowNo();
            String receiveCoin = cryptoOrder.getReceiveCoin();
            BigDecimal receiveAmount = cryptoOrder.getReceiveAmount();

            //查询加密币钱包协议支持数据,适配钱包
            List<CryptoAppProtocolDto> cryptoAppProtocolList = cryptoWalletProtocolAdapter(receiveCoin,receiveAmount,receiveAddress);
            cryptoPayOrderRes.setCryptoAppProtocolList(cryptoAppProtocolList);

            cryptoPayOrderRes.setBizFlowNo(bizOrderNo);
            cryptoPayOrderRes.setReceiveAmount(receiveAmount);
            cryptoPayOrderRes.setReceiveAddress(receiveAddress);
            cryptoPayOrderRes.setCryptoAppProtocolList(cryptoAppProtocolList);
            //创建redis缓存,允许订单在前端进行访问,当订单状态终止后删除(支付成功、订单关闭)
            //根据bizFlowNo和时间戳得到Redis缓存Key,并设置过期时间
            String accessKey = DigestUtils.md5DigestAsHex(
                    MessageFormat.format("{0}{1}",bizFlowNo,System.currentTimeMillis()).getBytes());
            redisTemplate.opsForValue().set(
                    MessageFormat.format(
                            RedisCacheKeyConstants.WEB_CASHIER_API_ACCESS_KEY_CACHE,accessKey),
                    bizOrderNo,50, TimeUnit.MINUTES);
            //Web端访问地址
            cryptoPayOrderRes.setLink("https://www.baidu.com?access_key=" + accessKey);
        }
        logger.info("加密币支付,支付订单业务处理接口,订单号:{} 响应结果:{}",bizOrderNo,JSON.toJSONString(cryptoPayOrderRes));
        return cryptoPayOrderRes;
    }

    private void setLatestOrderPayInfo(CryptoOrder cryptoOrder, String address,BigDecimal orderRate,String receiveCoin,String receiveNetworkCode) {
        //查询加密币支持网络数据
        CryptoSupportNetwork cryptoSupportNetwork = cryptoSupportNetworkMapper.queryAvailableCryptoSupportNetwork(receiveCoin, receiveNetworkCode);
        cryptoOrder.setReceiveAddress(address);
        cryptoOrder.setReceiveNetworkCode(cryptoSupportNetwork.getNetworkCode());
        cryptoOrder.setReceiveCoinType(cryptoSupportNetwork.getCryptoType());
        cryptoOrder.setReceiveCoin(receiveCoin);
        cryptoOrder.setReceiveAmount(orderRate.multiply(cryptoOrder.getOrderAmount()).setScale(6,BigDecimal.ROUND_HALF_UP));
        cryptoOrder.setStatus(CryptoOrderStatusEnum.unpaid.getCode());
        cryptoOrder.setUpdateTime(new Date());

    }

    private CryptoOrder newCryptoRefundOrder(CryptoOrder orgCryptoOrder,String refundOrderNo, String refundAddress) {

        CryptoOrder refundOrder = new CryptoOrder();
        refundOrder.setBizOrderNo(refundOrderNo);
        refundOrder.setOrgBizOrderNo(orgCryptoOrder.getBizOrderNo());
        refundOrder.setBizFlowNo(IDUtils.generateBizFlowNo());

        refundOrder.setReceiveAddress(refundAddress);
        refundOrder.setReceiveAmount(orgCryptoOrder.getReceiptAmount());
        refundOrder.setReceiveCoin(orgCryptoOrder.getReceiveCoin());
        refundOrder.setReceiveCoinType(orgCryptoOrder.getReceiveCoinType());
        refundOrder.setReceiveNetworkCode(orgCryptoOrder.getReceiveNetworkCode());

        refundOrder.setStatus(CryptoOrderStatusEnum.init.getCode());
        refundOrder.setCreateTime(new Date());
        refundOrder.setUpdateTime(new Date());
        cryptoOrderMapper.insertSelective(refundOrder);

        return refundOrder;
    }

    private void newCryptoPayOrderExtend(CryptoOrder cryptoOrder, BigDecimal realRate,
                                         BigDecimal orderRate, String paymentAddress,
                                         String tradeHash,String blockHash,Long blockNumber) {

        String bizFlowNo = cryptoOrder.getBizFlowNo();
        CryptoOrderExtend cryptoOrderExtend = cryptoOrderExtendMapper.findCryptoOrderExtend(bizFlowNo);
        if(null == cryptoOrderExtend){
            cryptoOrderExtend = new CryptoOrderExtend();
            cryptoOrderExtend.setBizFlowNo(bizFlowNo);
            cryptoOrderExtend.setOrderExchangeRate(orderRate);
            cryptoOrderExtend.setRealExchangeRate(realRate);
            cryptoOrderExtend.setCreateTime(new Date());
            cryptoOrderExtend.setUpdateTime(new Date());

            cryptoOrderExtend.setBlockHash(blockHash);
            cryptoOrderExtend.setTransHash(tradeHash);
            cryptoOrderExtend.setBlockHash(blockHash);
            cryptoOrderExtend.setBlockNumber(blockNumber);
            cryptoOrderExtend.setPaymentAddress(paymentAddress);

            if(null != realRate && realRate.compareTo(BigDecimal.ZERO) >0) {
                cryptoOrderExtend.setRealReceiveAmount(cryptoOrder.getOrderAmount()
                        .multiply(realRate).setScale(6, BigDecimal.ROUND_HALF_UP));
            }
            cryptoOrderExtendMapper.insertSelective(cryptoOrderExtend);
        }else {
            cryptoOrderExtend.setOrderExchangeRate(orderRate);
            cryptoOrderExtend.setRealExchangeRate(realRate);
            cryptoOrderExtend.setUpdateTime(new Date());

            cryptoOrderExtend.setBlockHash(blockHash);
            cryptoOrderExtend.setTransHash(tradeHash);
            cryptoOrderExtend.setBlockHash(blockHash);
            cryptoOrderExtend.setBlockNumber(blockNumber);
            cryptoOrderExtend.setPaymentAddress(paymentAddress);

            if (null != realRate && realRate.compareTo(BigDecimal.ZERO) > 0) {
                cryptoOrderExtend.setRealReceiveAmount(cryptoOrder.getOrderAmount()
                        .multiply(realRate).setScale(6, BigDecimal.ROUND_HALF_UP));
            }
            cryptoOrderExtendMapper.updateByPrimaryKeySelective(cryptoOrderExtend);
        }


    }

    private CryptoOrder newCryptoPayOrder(CryptoPayOrderReq cryptoPayOrderReq, CryptoSupportNetwork cryptoSupportNetwork, BigDecimal orderRate) {
        BigDecimal orderAmount = cryptoPayOrderReq.getOrderAmount();
        CryptoOrder cryptoOrder = new CryptoOrder();
        cryptoOrder.setOrderCoin(cryptoPayOrderReq.getOrderCoin());
        cryptoOrder.setBizOrderNo(cryptoPayOrderReq.getOrderNo());
        cryptoOrder.setOrderAmount(cryptoPayOrderReq.getOrderAmount());
        cryptoOrder.setBizType(cryptoPayOrderReq.getBizOrderType());

        cryptoOrder.setBizFlowNo(IDUtils.generateBizFlowNo());
        cryptoOrder.setReceiveAmount(orderRate.multiply(orderAmount).setScale(6,BigDecimal.ROUND_HALF_UP));
        cryptoOrder.setReceiveNetworkCode(cryptoSupportNetwork.getNetworkCode());
        cryptoOrder.setReceiveCoinType(cryptoSupportNetwork.getCryptoType());
        cryptoOrder.setReceiveCoin(cryptoPayOrderReq.getReceiveCoin());

        cryptoOrder.setCreateTime(new Date());
        cryptoOrder.setUpdateTime(new Date());
        cryptoOrder.setStatus(CryptoOrderStatusEnum.init.getCode());

        cryptoOrderMapper.insertSelective(cryptoOrder);
        return cryptoOrder;
    }

    private CryptoOrder newPreOrder(String orderNo, String orderCoin, BigDecimal orderAmount, String bizOrderType) {
        CryptoOrder cryptoOrder = new CryptoOrder();
        cryptoOrder.setOrderCoin(orderCoin);
        cryptoOrder.setBizOrderNo(orderNo);
        cryptoOrder.setOrderAmount(orderAmount);
        cryptoOrder.setBizType(bizOrderType);
        cryptoOrder.setBizFlowNo(IDUtils.generateBizFlowNo());

        cryptoOrder.setCreateTime(new Date());
        cryptoOrder.setUpdateTime(new Date());
        cryptoOrder.setStatus(CryptoOrderStatusEnum.init.getCode());
        return cryptoOrder;
    }

    private List<CryptoAppProtocolDto> cryptoWalletProtocolAdapter(String receiveCoin,BigDecimal receiveAmount, String receiveAddress) {
        List<CryptoWalletProtocolDto> walletProtocolList = cryptoWalletProtocolService.queryCryptoWalletProtocol(receiveCoin);
        if(null != walletProtocolList && walletProtocolList.size() >0){
            List<CryptoAppProtocolDto> cryptoAppProtocolList = new ArrayList<>();
            for(CryptoWalletProtocolDto cryptoWalletProtocolDto:walletProtocolList){
                CryptoAppProtocolDto cryptoAppProtocolDto = new CryptoAppProtocolDto();
                cryptoAppProtocolDto.setAppName(cryptoWalletProtocolDto.getAppName());

                String scanProtocol = cryptoWalletProtocolDto.getScanProtocol();
                if(StringUtils.hasText(scanProtocol)){
                    cryptoAppProtocolDto.setScanProtocol(MessageFormat.format(scanProtocol,receiveAddress,receiveAddress.toString()));
                }
                String pluginProtocol = cryptoWalletProtocolDto.getPluginProtocol();
                if(StringUtils.hasText(pluginProtocol)){
                    cryptoAppProtocolDto.setPluginProtocol(MessageFormat.format(pluginProtocol,receiveAddress,receiveAddress.toString()));
                }
                String schemeProtocol = cryptoWalletProtocolDto.getSchemeProtocol();
                if(StringUtils.hasText(schemeProtocol)){
                    cryptoAppProtocolDto.setPluginProtocol(MessageFormat.format(schemeProtocol,receiveAddress,receiveAddress.toString()));
                }
                cryptoAppProtocolList.add(cryptoAppProtocolDto);
            }
            return cryptoAppProtocolList;
        }
        return null;
    }

    private BigDecimal getLatestCryptoRate(String orderCoin, String receiveCoin) {
        LatestCryptoRateQueryReq latestCryptoRateQueryReq = new LatestCryptoRateQueryReq();
        latestCryptoRateQueryReq.setFromSymbol(orderCoin);
        latestCryptoRateQueryReq.setToSymbol(receiveCoin);
        LatestCryptoRateQueryRes latestCryptoRateQueryRes = coinExchangeRateService.queryLatestCryptoRate(latestCryptoRateQueryReq);
        if(ReturnCodeEnum.SUCCESS.getCode().equals(latestCryptoRateQueryRes.getCode())){
            return latestCryptoRateQueryRes.getRealRete();
        }
        return null;
    }

    private void retrunOrderReceiveAddress(CryptoOrder cryptoOrder) {
        String receiveAddress = cryptoOrder.getReceiveAddress();
        if(StringUtils.hasText(receiveAddress)) {
            addressPoolService.returnAddressToAddressPoolCache(
                    cryptoOrder.getReceiveNetworkCode(), cryptoOrder.getReceiveAddress(), 0);
        }
    }
}
