package org.cyberpay.account.constant;

public class RedisCacheKeyConstants {

    /**
     * 应用名称
     */
    private final static String APP_NAME = "CyberPay";

    /**
     * 服务名称
     */
    private static final String CYBERPAY_CRYPTO_SERVER_NAME = ":ACCOUNT";

    /**
     * 缓存前缀
     */
    public static final String REDIS_CACHE_PREFIX = APP_NAME + CYBERPAY_CRYPTO_SERVER_NAME + ":CACHE";

    /**
     * 商户信息缓存
     */
    public static final String MERCHANT_CACHE_KEY = REDIS_CACHE_PREFIX + "：MERCHANT_INFO:{0}";

}
