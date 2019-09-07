package com.ahaxt.hanlp.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author hongzhangming
 */
public class RedisConfig {
    private static JedisPool jedisPool;

    public Jedis getJedis() { return jedisPool.getResource(); }

    static{
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(200);
        jedisPoolConfig.setMaxIdle(5);
        jedisPoolConfig.setMaxWaitMillis(100);
        //jedis 第一次启动时，会报错
        jedisPoolConfig.setTestOnBorrow(false);
        jedisPoolConfig.setTestOnReturn(true);
        jedisPool = new JedisPool(jedisPoolConfig,"test.ahaxt.com", 6379,10000,"Axt-1234");
    }
}
