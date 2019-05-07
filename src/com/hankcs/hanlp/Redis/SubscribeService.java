package com.ahaxt.ykt.common.rpc;

import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.util.Date;
import java.util.Map;

@Component
public class SubscribeService {

    public void subscribe(RedisBroker redisBroker, String responseChannel, Logger logger, Map<String, RpcCallback> taskCallbacks){

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true){
                        Jedis responseJedis = redisBroker.getPublisher();
                        JSONObject jo = new JSONObject();
                        jo.put("taskId","ping");
                        jo.put("ping",responseChannel);
                        responseJedis.publish(responseChannel,jo.toString());
                        responseJedis.close();
                        Thread.sleep(120000);//2分/次 向responseChannel发送一条心跳数据（callbacks 中无，不处理）
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    logger.error(String.format("<ERROR> responseChannel ping"));
                }
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Jedis jedis = redisBroker.getSubscriber();
                jedis.subscribe(new JedisPubSub() {
                    @Override
                    public void onMessage(String onMessage, String result) {
                        try {
                            String taskId = JSONObject.fromObject(result).getString("taskId");
                            if("ping".equals(taskId)){
                                LoggerFactory.getLogger(this.getClass()).debug(result);
                            }else {
                                logger.info(String.format("<INFO-50001> onMessage: %s result:%s", onMessage, result));
                            }
                            RpcCallback callback = taskCallbacks.get(taskId);
                            if(callback != null){
                                callback.handle(result);
                                taskCallbacks.remove(taskId);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                            logger.error(String.format("<ERROR-50002> jedis value 转 json 失败 result:%s",result));
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
                }, responseChannel);
            }
        }).start();

    }

}
