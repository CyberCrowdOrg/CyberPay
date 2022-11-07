package org.cyberpay.crypto.utils;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class RedissonLock {

    private static Logger logger = LoggerFactory.getLogger(RedissonLock.class);
    private static String LOCK_KEY;
    private static TimeUnit TIME_UNIT = TimeUnit.MILLISECONDS;
    private static Long LOCK_EXPIRY_TIME = 6000l;

    private RedissonClient REDISSON_CLIENT;

    private RLock RLOCK;


    public RedissonLock(RedissonClient redissonClient,String lockKey){
        this(redissonClient,lockKey,LOCK_EXPIRY_TIME);
    }

    public RedissonLock(RedissonClient redissonClient,String lockKey,Long lockExpiryTime){
        this.LOCK_KEY = lockKey;
        this.LOCK_EXPIRY_TIME = lockExpiryTime;
        this.REDISSON_CLIENT = redissonClient;
    }

    public boolean lock() throws InterruptedException {
        RLock rlock = this.REDISSON_CLIENT.getLock(LOCK_KEY);
        this.RLOCK = rlock;
        boolean status = rlock.tryLock(LOCK_EXPIRY_TIME, TIME_UNIT);
        return status;
    }

    public void unlock(){
        RLOCK.unlock();
    }
}
