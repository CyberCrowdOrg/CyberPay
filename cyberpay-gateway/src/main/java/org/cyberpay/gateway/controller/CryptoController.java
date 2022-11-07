package org.cyberpay.gateway.controller;

import com.alibaba.fastjson2.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.cyberpay.gateway.service.feign.CryptoTradeOrderFeign;
import org.cyberpay.trade.enums.TradeReturnCodeEnum;
import org.cyberpay.trade.request.CryptoTradeOrderPaySubmitReq;
import org.cyberpay.trade.request.CryptoTradePayOrderReq;
import org.cyberpay.trade.request.CryptoTradePreTradeOrderReq;
import org.cyberpay.trade.response.CryptoTradeOrderPaySubmitRes;
import org.cyberpay.trade.response.CryptoTradePayOrderRes;
import org.cyberpay.trade.response.CryptoTradePreOrderRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.text.MessageFormat;

@Api(tags = "加密币订单接口")
@RestController
public class CryptoController {

    private Logger logger = LoggerFactory.getLogger(CryptoController.class);

    @Autowired
    CryptoTradeOrderFeign cryptoTradeOrderFeign;

    @ApiOperation("加密币预订单接口")
    @PostMapping(value = "/crypto/v1/pre-order")
    @ResponseBody
    public CryptoTradePreOrderRes preOrder(@RequestBody CryptoTradePreTradeOrderReq cryptoTradePreTradeOrderReq){
        CryptoTradePreOrderRes cryptoTradePreOrderRes = new CryptoTradePreOrderRes();
        logger.info("加密币预订单接口,请求入参:{}", JSON.toJSONString(cryptoTradePreTradeOrderReq));


        String merchantId = cryptoTradePreTradeOrderReq.getMerchantId();
        if(!StringUtils.hasText(merchantId)){
            logger.info("加密币预订单接口,商户号参数不能为空...");
            cryptoTradePreOrderRes.setReturnEnum(TradeReturnCodeEnum.PARAMETER_NULL_ERROR);
            cryptoTradePreOrderRes.setMessage(MessageFormat.format(cryptoTradePreOrderRes.getMessage(),"MerchantId"));
        }

        String orderNo = cryptoTradePreTradeOrderReq.getOrderNo();
        if(!StringUtils.hasText(orderNo)){
            logger.info("加密币预订单接口,订单号参数不能为空...");
            cryptoTradePreOrderRes.setReturnEnum(TradeReturnCodeEnum.PARAMETER_NULL_ERROR);
            cryptoTradePreOrderRes.setMessage(MessageFormat.format(cryptoTradePreOrderRes.getMessage(),"OrderNo"));
        }

        String orderCoin = cryptoTradePreTradeOrderReq.getOrderCoin();
        if(!StringUtils.hasText(orderCoin)){
            logger.info("加密币预订单接口,订单币种参数不能为空...");
            cryptoTradePreOrderRes.setReturnEnum(TradeReturnCodeEnum.PARAMETER_NULL_ERROR);
            cryptoTradePreOrderRes.setMessage(MessageFormat.format(cryptoTradePreOrderRes.getMessage(),"OrderCoin"));
        }

        String bizOrderType = cryptoTradePreTradeOrderReq.getBizOrderType();
        if(!StringUtils.hasText(bizOrderType)){
            logger.info("加密币预订单接口,订单类型参数不能为空...");
            cryptoTradePreOrderRes.setReturnEnum(TradeReturnCodeEnum.PARAMETER_NULL_ERROR);
            cryptoTradePreOrderRes.setMessage(MessageFormat.format(cryptoTradePreOrderRes.getMessage(),"BizOrderType"));
        }

        BigDecimal orderAmount = cryptoTradePreTradeOrderReq.getOrderAmount();
        if(null == orderAmount || orderAmount.compareTo(BigDecimal.ZERO) <=0){
            logger.info("加密币预订单接口,订单金额错误...");
            cryptoTradePreOrderRes.setReturnEnum(TradeReturnCodeEnum.ORDER_AMOUNT_ERROR);
        }

        String sign = cryptoTradePreTradeOrderReq.getSign();
        if(!StringUtils.hasText(sign)){
            logger.info("加密币预订单接口,订单类型参数不能为空...");
            cryptoTradePreOrderRes.setReturnEnum(TradeReturnCodeEnum.PARAMETER_NULL_ERROR);
            cryptoTradePreOrderRes.setMessage(MessageFormat.format(cryptoTradePreOrderRes.getMessage(),"Sign"));
            return cryptoTradePreOrderRes;
        }

        //验证签名

        //调用接口
        cryptoTradePreOrderRes = cryptoTradeOrderFeign.cryptoTradePreOrder(cryptoTradePreTradeOrderReq);
        //处理返回码message翻译

        logger.info("加密币预订单接口,响应结果:{}",JSON.toJSONString(cryptoTradePreOrderRes));
        return cryptoTradePreOrderRes;
    }

    @ApiOperation("加密币订单支付提交接口")
    @PostMapping(value = "/crypto/v1/submit-pay-order")
    @ResponseBody
    public CryptoTradeOrderPaySubmitRes submitPayOrder(@RequestBody CryptoTradeOrderPaySubmitReq cryptoTradeOrderPaySubmitReq){
        CryptoTradeOrderPaySubmitRes cryptoTradeOrderPaySubmitRes = new CryptoTradeOrderPaySubmitRes();
        logger.info("加密币订单支付提交接口,请求入参:{}",JSON.toJSONString(cryptoTradeOrderPaySubmitReq));

        String accessKey = cryptoTradeOrderPaySubmitReq.getAccessKey();
        if(!StringUtils.hasText(accessKey)){
            logger.info("加密币订单支付提交接口,访问密匙参数不能为空...");
            cryptoTradeOrderPaySubmitRes.setReturnEnum(TradeReturnCodeEnum.PARAMETER_NULL_ERROR);
            cryptoTradeOrderPaySubmitRes.setMessage(MessageFormat.format(cryptoTradeOrderPaySubmitRes.getMessage(),"AccessKey"));
            return cryptoTradeOrderPaySubmitRes;
        }

        String receiveCoin = cryptoTradeOrderPaySubmitReq.getReceiveCoin();
        if(!StringUtils.hasText(receiveCoin)){
            logger.info("加密币订单支付提交接口,订单号参数不能为空...");
            cryptoTradeOrderPaySubmitRes.setReturnEnum(TradeReturnCodeEnum.PARAMETER_NULL_ERROR);
            cryptoTradeOrderPaySubmitRes.setMessage(MessageFormat.format(cryptoTradeOrderPaySubmitRes.getMessage(),"ReceiveCoin"));
            return cryptoTradeOrderPaySubmitRes;
        }

        String receiveNetworkCode = cryptoTradeOrderPaySubmitReq.getReceiveNetworkCode();
        if(!StringUtils.hasText(receiveNetworkCode)){
            logger.info("加密币订单支付提交接口,订单币种参数不能为空...");
            cryptoTradeOrderPaySubmitRes.setReturnEnum(TradeReturnCodeEnum.PARAMETER_NULL_ERROR);
            cryptoTradeOrderPaySubmitRes.setMessage(MessageFormat.format(cryptoTradeOrderPaySubmitRes.getMessage(),"ReceiveNetworkCode"));
            return cryptoTradeOrderPaySubmitRes;
        }

        //调用接口
        cryptoTradeOrderPaySubmitRes = cryptoTradeOrderFeign.cryptoTradeOrderPaySubmit(cryptoTradeOrderPaySubmitReq);
        //处理返回码message翻译

        logger.info("加密币订单支付提交接口,响应结果:{}",JSON.toJSONString(cryptoTradeOrderPaySubmitRes));
        return cryptoTradeOrderPaySubmitRes;
    }

    @ApiOperation("加密币订单支付接口")
    @PostMapping(value = "/crypto/v1/pay-order")
    @ResponseBody
    public CryptoTradePayOrderRes payOrder(@RequestBody CryptoTradePayOrderReq cryptoTradePayOrderReq){
        CryptoTradePayOrderRes cryptoTradePayOrderRes = new CryptoTradePayOrderRes();
        logger.info("加密币订单支付接口,请求入参:{}",JSON.toJSONString(cryptoTradePayOrderReq));

        String merchantId = cryptoTradePayOrderReq.getMerchantId();
        if(!StringUtils.hasText(merchantId)){
            logger.info("加密币订单支付接口,商户号参数不能为空...");
            cryptoTradePayOrderRes.setReturnEnum(TradeReturnCodeEnum.PARAMETER_NULL_ERROR);
            cryptoTradePayOrderRes.setMessage(MessageFormat.format(cryptoTradePayOrderRes.getMessage(),"MerchantId"));
        }

        String sign = cryptoTradePayOrderReq.getSign();
        if(!StringUtils.hasText(sign)){
            logger.info("加密币订单支付接口,订单类型参数不能为空...");
            cryptoTradePayOrderRes.setReturnEnum(TradeReturnCodeEnum.PARAMETER_NULL_ERROR);
            cryptoTradePayOrderRes.setMessage(MessageFormat.format(cryptoTradePayOrderRes.getMessage(),"Sign"));
            return cryptoTradePayOrderRes;
        }

        String orderNo = cryptoTradePayOrderReq.getOrderNo();
        if(!StringUtils.hasText(orderNo)){
            logger.info("加密币订单支付接口,订单号参数不能为空...");
            cryptoTradePayOrderRes.setReturnEnum(TradeReturnCodeEnum.PARAMETER_NULL_ERROR);
            cryptoTradePayOrderRes.setMessage(MessageFormat.format(cryptoTradePayOrderRes.getMessage(),"OrderNo"));
        }

        String orderCoin = cryptoTradePayOrderReq.getOrderCoin();
        if(!StringUtils.hasText(orderCoin)){
            logger.info("加密币订单支付接口,订单币种参数不能为空...");
            cryptoTradePayOrderRes.setReturnEnum(TradeReturnCodeEnum.PARAMETER_NULL_ERROR);
            cryptoTradePayOrderRes.setMessage(MessageFormat.format(cryptoTradePayOrderRes.getMessage(),"OrderCoin"));
        }

        String bizOrderType = cryptoTradePayOrderReq.getBizOrderType();
        if(!StringUtils.hasText(bizOrderType)){
            logger.info("加密币订单支付接口,订单类型参数不能为空...");
            cryptoTradePayOrderRes.setReturnEnum(TradeReturnCodeEnum.PARAMETER_NULL_ERROR);
            cryptoTradePayOrderRes.setMessage(MessageFormat.format(cryptoTradePayOrderRes.getMessage(),"BizOrderType"));
        }

        BigDecimal orderAmount = cryptoTradePayOrderReq.getOrderAmount();
        if(null == orderAmount || orderAmount.compareTo(BigDecimal.ZERO) <=0){
            logger.info("加密币订单支付接口,订单金额错误...");
            cryptoTradePayOrderRes.setReturnEnum(TradeReturnCodeEnum.ORDER_AMOUNT_ERROR);
        }

        String receiveCoin = cryptoTradePayOrderReq.getReceiveCoin();
        if(!StringUtils.hasText(receiveCoin)){
            logger.info("加密币订单支付接口,订单号参数不能为空...");
            cryptoTradePayOrderRes.setReturnEnum(TradeReturnCodeEnum.PARAMETER_NULL_ERROR);
            cryptoTradePayOrderRes.setMessage(MessageFormat.format(cryptoTradePayOrderRes.getMessage(),"ReceiveCoin"));
            return cryptoTradePayOrderRes;
        }

        String receiveNetworkCode = cryptoTradePayOrderReq.getReceiveNetworkCode();
        if(!StringUtils.hasText(receiveNetworkCode)){
            logger.info("加密币订单支付接口,订单币种参数不能为空...");
            cryptoTradePayOrderRes.setReturnEnum(TradeReturnCodeEnum.PARAMETER_NULL_ERROR);
            cryptoTradePayOrderRes.setMessage(MessageFormat.format(cryptoTradePayOrderRes.getMessage(),"ReceiveNetworkCode"));
            return cryptoTradePayOrderRes;
        }

        //查询商户验证签名

        cryptoTradePayOrderRes = cryptoTradeOrderFeign.cryptoTradePayOrder(cryptoTradePayOrderReq);
        //处理返回码message翻译

        logger.info("加密币订单支付提交接口,响应结果:{}",JSON.toJSONString(cryptoTradePayOrderRes));

        return cryptoTradePayOrderRes;
    }
}
