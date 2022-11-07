package org.cyberpay.trade.constant;

public class RedisCacheKeyConstants {

    /**
     * 应用名称
     */
    private final static String APP_NAME = "CyberPay";

    /**
     * 服务名称
     */
    private static final String CYBERPAY_CRYPTO_SERVER_NAME = ":TRADE";

    /**
     * 缓存前缀
     */
    public static final String REDIS_CACHE_PREFIX = APP_NAME + CYBERPAY_CRYPTO_SERVER_NAME + ":CACHE";

    /**
     * Ssemitter 连接缓存
     */
    public static final String SSEMITTER_SERVER_CONNECT_CACHE = REDIS_CACHE_PREFIX + ":SSEMITTER_SERVER_CONNECT:{0}";

    /**
     * Ssemitter 消息缓存
     */
    public static final String SSEMITTER_SERVER_MESSAGE_CACHE = REDIS_CACHE_PREFIX + ":SSEMITTER_SERVER_MESSAGE:{0}";


    //加密币web端收银页面API访问KEY,限时过期
    public static final String CRYPTO_WEB_CASHIER_API_ACCESS_KEY_CACHE = "CyberPay:CRYPTO:CACHE:WEB_CASHIER_API_ACCESS_KEY:{0}";
}
