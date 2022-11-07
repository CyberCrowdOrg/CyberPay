package org.cyberpay.crypto.constant;

public class RedisLockKeyConstants {

    /**
     * 应用名称
     */
    private final static String APP_NAME = "CyberPay";

    /**
     * 服务名称
     */
    private static final String CYBERPAY_CRYPTO_SERVER_NAME = ":CRYPTO";


    private static final String REDIS_LOCK_PREFIX = APP_NAME + CYBERPAY_CRYPTO_SERVER_NAME;

    /**
     * 订单创建锁
     */
    public static final String CRYPTO_ORDER_CRATE_LOCK_KEY = REDIS_LOCK_PREFIX + ":CRYPTO_ORDER_CRATE_LOCK:{0}";

    /**
     * 订单更新锁
     */
    public static final String CRYPTO_ORDER_UPDATE_LOCK_KEY = REDIS_LOCK_PREFIX + ":CRYPTO_ORDER_UPDATE_LOCK:{0}";

}
