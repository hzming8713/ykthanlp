package com.ahaxt.hanlp.redis;

import com.alibaba.fastjson.JSONObject;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author hongzhangming
 */
public class SubscribeService extends RedisConfig{
    public void publish(String channel , String jsonString){
        Jedis responseJedis = getJedis();
        responseJedis.publish(channel,jsonString);
        responseJedis.close();
    }
    /**
     * 订阅 requestsChannel
     */
    public void subscribe(String requestChannel , RpcCallback rpcCallback){
        SimpleDateFormat sdf = new SimpleDateFormat("yy/MM/dd HH:mm:ss:SSS");
        new Thread(new Runnable() {
            @Override
            public void run() {
                Jedis jedis = getJedis();
                jedis.subscribe(new JedisPubSub() {
                    @Override
                    public void onMessage(String requestsChannel, String requestString) {
                        rpcCallback.handle(requestString);
                    }
                    @Override
                    public void onPMessage(String s, String s1, String s2) {
                        System.out.println(String.format("onPMessage: %s %s %s", s, s1, s2));
                    }
                    @Override
                    public void onSubscribe(String s, int i) {
                        System.out.println(String.format("onSubscribe: %s %d", s, i));
                    }
                    @Override
                    public void onUnsubscribe(String s, int i) {
                        System.out.println(String.format("onUnsubscribe: %s %d", s, i));
                    }
                    @Override
                    public void onPUnsubscribe(String s, int i) {
                        System.out.println(String.format("onPUnsubscribe: %s %d", s, i));
                    }
                    @Override
                    public void onPSubscribe(String s, int i) {
                        System.out.println(String.format("onPSubscribe: %s %d", s, i));
                    }
                }, requestChannel);
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true){
                        Thread.sleep(600000);//向requestsChannel发送心跳数据
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("TaskId",requestChannel);
                        jsonObject.put("nowTime",sdf.format(new Date()));
                        publish(requestChannel,jsonObject.toString());
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    System.err.println(String.format("<ERROR> requestsChannel ping"));
                }
            }
        }).start();
    }

}
