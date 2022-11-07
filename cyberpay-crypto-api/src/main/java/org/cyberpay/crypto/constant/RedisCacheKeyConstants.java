package org.cyberpay.crypto.constant;

public class RedisCacheKeyConstants {

    /**
     * 应用名称
     */
    private final static String APP_NAME = "CyberPay";

    /**
     * 服务名称
     */
    private static final String CYBERPAY_CRYPTO_SERVER_NAME = ":CRYPTO";

    /**
     * 缓存前缀
     */
    public static final String REDIS_CACHE_PREFIX = APP_NAME + CYBERPAY_CRYPTO_SERVER_NAME + ":CACHE";


    //加密币汇率缓存
    public static final String CRYPTO_RATE_CACHE_KEY = REDIS_CACHE_PREFIX + ":CRYPTO_RATE_CACHE";

    //加密币支持列表缓存
    public static final String CRYPTO_SUPPORT_LIST_CACHE_KEY = REDIS_CACHE_PREFIX + ":CRYPTO_SUPPORT_LIST_CACHE";

    //法币支持列表缓存
    public static final String FIAT_SUPPORT_LIST_CACHE_KEY = REDIS_CACHE_PREFIX + ":FIAT_SUPPORT_LIST_CACHE";

    //加密币钱包支持协议缓存
    public static final String CRYPTO_WALLET_PROTOCOL_CACHE_KEY = REDIS_CACHE_PREFIX + ":CRYPTO_WALLET_PROTOCOL_CACHE";

    //是否支持币种判断结果缓存
    public static final String CRYPTO_SUPPORT_COIN_RESULT_CACHE_KEY =  REDIS_CACHE_PREFIX + ":CRYPTO_SUPPORT_COIN_RESULT:{0}";

    //web端收银页面API访问KEY,限时过期
    public static final String WEB_CASHIER_API_ACCESS_KEY_CACHE = REDIS_CACHE_PREFIX + ":WEB_CASHIER_API_ACCESS_KEY:{0}";
    /**
     * 合约缓存
     */
    public static final String CONTRACT_CACHE = REDIS_CACHE_PREFIX + ":CONTRACT:{0}";

    /**
     *  地址池所有地址缓存
     */
    public static final String ADDRESS_POOL_ALL_CACHE = REDIS_CACHE_PREFIX + ":ADDRESS_POOL_ALL:{0}";

    /**
     * 可用地址池队列缓存
     */
    public static final String ADDRESS_POOL_AVAILABLE_QUEUE_CACHE = REDIS_CACHE_PREFIX + ":ADDRESS_POOL_AVAILABLE_QUEUE:{0}";

    /**
     * 不可用地址池队列缓存
     */
    public static final String ADDRESS_POOL_UNAVAILABLE_QUEUE_CACHE = REDIS_CACHE_PREFIX + ":ADDRESS_POOL_UNAVAILABLE_QUEUE:{0}";

    /**
     * 节点订阅消息队列
     * 如果发布消息失败了，就放这里面通过redis消费
     */
    public static final String NODE_SUBSCRIBE_MESSAGE_QUEUE = CYBERPAY_CRYPTO_SERVER_NAME + ":NODE_SUBSCRIBE_MESSAGE_QUEUE:{0}";

    /**
     * 加密币支持网络缓存
     */
    public static final String CRYPTO_SUPPORT_NETWORK_CACHE = REDIS_CACHE_PREFIX + ":CRYPTO_SUPPORT_NETWORK_CACHE:{0}";
}
