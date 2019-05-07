package com.hankcs.hanlp.Redis;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

public class SubscribeService {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    /**
     * 订阅 requestsChannel
     */
    public void subscribe(String requestsChannel, String responseChannel,RpcCallback rpcCallback){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Jedis jedis = RedisConfig.getSubscriber();
                jedis.subscribe(new JedisPubSub() {
                    @Override
                    public void onMessage(String requestsChannel, String result) {
                        synchronized (this){
                            JSONObject jo = new JSONObject();
                            try {
                                JSONObject requestJson = JSONObject.parseObject(result);
                                String taskId = requestJson.getString("taskId");
                                if ("ping".equals(taskId)) {
                                    System.out.println(result);
                                } else {
                                    System.err.println(String.format("<INFO-50001> onMessage: %s result:%s", requestsChannel, result));
                                    jo.put("message",rpcCallback.handle(requestJson));//处理任务  调度算法
                                    jo.put("status",true);
                                    Jedis responseJedis = RedisConfig.getPublisher();
                                    responseJedis.publish(responseChannel,jo.toString());
                                    responseJedis.close();
                                }
                            } catch (Exception e) {
                                jo.put("status",false);
                                jo.put("message",String.format("<ERROR-50002> jedis value 非 json 格式 result:%s", result));
                                Jedis responseJedis = RedisConfig.getPublisher();
                                responseJedis.publish(responseChannel,jo.toString());
                                responseJedis.close();
                            }
                        }
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
                }, requestsChannel);
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true){
                        Jedis responseJedis = RedisConfig.getPublisher();
                            JSONObject jo = new JSONObject();
                            jo.put("taskId","ping");
                            jo.put("ping",requestsChannel);
                        responseJedis.publish(requestsChannel,jo.toString());
                        responseJedis.close();
                        Thread.sleep(120000);//向responseChannel发送心跳数据
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    logger.error(String.format("<ERROR> responseChannel ping"));
                }
            }
        }).start();
    }

}
