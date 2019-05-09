package redis;

import com.alibaba.fastjson.JSONObject;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

public class SubscribeService {
    /**
     * 订阅 requestsChannel
     */
    public void subscribe(String requestChannel, String responseChannel,RpcCallback rpcCallback){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Jedis jedis = RedisConfig.getSubscriber();
                jedis.subscribe(new JedisPubSub() {
                    @Override
                    public void onMessage(String requestsChannel, String requestString) {
                        synchronized (this){
                            try {
                                JSONObject requestJson = JSONObject.parseObject(requestString);
                                String taskId = requestJson.getString("taskId");
                                if ("ping".equals(taskId)) {
                                    System.out.println(requestString);
                                } else {
//                                    System.err.println(String.format("<INFO> onMessage: %s requestString: %s", requestsChannel, requestString));
                                    Jedis responseJedis = RedisConfig.getPublisher();
                                    responseJedis.publish(responseChannel,rpcCallback.handle(requestJson).toString());
                                    responseJedis.close();
                                }
                            } catch (Exception e) {
                                requestString = String.format("<ERROR-50002> jedis value 非 json 格式 requestString:【%s】", requestString);
                                System.err.println(requestString);
                                JSONObject jo = new JSONObject();
                                jo.put("status",false);
                                jo.put("message",requestString);
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
                }, requestChannel);
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true){
                        Thread.sleep(600000);//向requestsChannel发送心跳数据
                        Jedis responseJedis = RedisConfig.getPublisher();
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("taskId","ping");
                        jsonObject.put("ping",requestChannel);
                        responseJedis.publish(requestChannel,jsonObject.toString());
                        responseJedis.close();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    System.err.println(String.format("<ERROR> requestsChannel ping"));
                }
            }
        }).start();
    }

}
