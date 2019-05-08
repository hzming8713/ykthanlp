package redis;

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
        Properties properties = new Properties();
        try {
            properties.load(new BufferedReader(new FileReader("rpc.properties")));
        } catch (Exception e) {
            e.printStackTrace();
        }

        jedisPool = new JedisPool(properties.getProperty("broker.redis.host"), Integer.parseInt(properties.getProperty("broker.redis.port")));
        requestChannel_questionhanlp = properties.getProperty("redis.questionhanlp.request");
        responseChannel_questionhanlp = properties.getProperty("redis.questionhanlp.response");
    }
}
