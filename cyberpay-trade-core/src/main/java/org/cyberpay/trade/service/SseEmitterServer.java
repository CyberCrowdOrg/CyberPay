package org.cyberpay.trade.service;

import com.alibaba.fastjson2.JSON;
import org.cyberpay.trade.constant.RedisCacheKeyConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Service("sseEmitterServer")
public class SseEmitterServer {

    private static Logger logger = LoggerFactory.getLogger(SseEmitterServer.class);

    private static final long SSEEmitterExpiredTime = 60;

    @Autowired
    StringRedisTemplate redisTemplate;

    /**
     * 创建用户连接并返回 SseEmitter
     * @param employeeCode 用户ID
     * @return SseEmitter
     */
    public SseEmitter connect(String employeeCode) {
        //设置超时时间，0表示不过期。默认30秒，超过时间未完成会抛出异常：AsyncRequestTimeoutException
        SseEmitter sseEmitter = new SseEmitter(20L);
        //注册回调
        sseEmitter.onCompletion(completionCallBack(employeeCode));
        sseEmitter.onError(errorCallBack(employeeCode));
        sseEmitter.onTimeout(timeoutCallBack(employeeCode));
//        sseEmitterMap.put(employeeCode, sseEmitter);
        saveSseEmitter(employeeCode,sseEmitter);
        logger.info("创建新的sse连接，当前用户：{}", employeeCode);
        return sseEmitter;
    }

    /**
     * 给指定用户发送信息
     * @param employeeCode
     * @param jsonMsg
     */
    public void sendMessage(String employeeCode, String jsonMsg) {
        try {
            SseEmitter emitter = getSseEmitter(employeeCode);
            if (emitter == null) {
                logger.warn("sse用户[{}]不在注册表，消息推送失败", employeeCode);
                return;
            }
            emitter.send(jsonMsg, MediaType.APPLICATION_JSON);
        } catch (IOException e) {
            logger.error("sse用户[{}]推送异常:{}", employeeCode, e.getMessage());
            removeUser(employeeCode);
        }
    }

    /**
     * 移除用户连接
     */
    public void removeUser(String employeeCode) {
        SseEmitter emitter = getSseEmitter(employeeCode);
        if(emitter != null){
            emitter.complete();
        }
        removeSseEmitter(employeeCode);
        logger.info("移除sse用户：{}", employeeCode);
    }

    private Runnable completionCallBack(String employeeCode) {
        return () -> {
            logger.info("结束sse用户连接：{}", employeeCode);
            removeUser(employeeCode);
        };
    }

    private Runnable timeoutCallBack(String employeeCode) {
        return () -> {
            logger.info("连接sse用户超时：{}", employeeCode);
            removeUser(employeeCode);
        };
    }

    private Consumer<Throwable> errorCallBack(String employeeCode) {
        return throwable -> {
            logger.info("sse用户连接异常：{}", employeeCode);
            removeUser(employeeCode);
        };
    }

    /**
     * 获取SSE链接信息
     * @param employeeCode
     * @return
     */
    private SseEmitter getSseEmitter(String employeeCode){
        String server = redisTemplate.opsForValue().get(MessageFormat.format(RedisCacheKeyConstants.SSEMITTER_SERVER_CONNECT_CACHE, employeeCode));
        if(StringUtils.hasText(server)){
            SseEmitter sseEmitter = JSON.parseObject(server, SseEmitter.class);
            return sseEmitter;
        }
        return null;
    }

    /**
     * 删除SSE
     * @param employeeCode
     */
    private void removeSseEmitter(String employeeCode){
        redisTemplate.delete(employeeCode);
    }

    /**
     * 保存SSE链接信息,redis 过期
     * @param employeeCode
     * @param sseEmitter
     */
    private void saveSseEmitter(String employeeCode,SseEmitter sseEmitter){
        redisTemplate.opsForValue().set(
                MessageFormat.format(RedisCacheKeyConstants.SSEMITTER_SERVER_CONNECT_CACHE,
                        employeeCode),JSON.toJSONString(sseEmitter),SSEEmitterExpiredTime, TimeUnit.MINUTES);
    }

    /**
     * 保存SSE 消息缓存到redis 30 分钟过期
     * @param employeeCode
     * @param message
     */
    private void saveSseEmitterMessage(String employeeCode,String message){
        redisTemplate.opsForValue().set(
                MessageFormat.format(RedisCacheKeyConstants.SSEMITTER_SERVER_CONNECT_CACHE,
                        employeeCode),message,30, TimeUnit.MINUTES);
    }
}
