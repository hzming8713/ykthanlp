package com.ahaxt.hanlp.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Properties;

public class RedisConfig {
    private static JedisPool jedisPool;

    public static Jedis getSubscriber() { return jedisPool.getResource(); }
    public static Jedis getPublisher() {
        return jedisPool.getResource();
    }

    public final static String requestChannel_questionhanlp;
    public final static String responseChannel_questionhanlp;

    static{
        jedisPool = new JedisPool("test.ahaxt.com", 6379);
        requestChannel_questionhanlp = "request_questionhanlp";
        responseChannel_questionhanlp = "response_questionhanlp";
    }
}
