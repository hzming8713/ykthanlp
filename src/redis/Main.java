package redis;

import com.alibaba.fastjson.JSONObject;
import com.hankcs.hanlp.SubjectEval;

import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        try {
            new SubscribeService().subscribe(RedisConfig.requestChannel_questionhanlp,RedisConfig.responseChannel_questionhanlp,new RpcCallback(){
                @Override
                public JSONObject handle(JSONObject requestJson) {
                    JSONObject reponseJson = new JSONObject();
                    try{
                        System.out.println("requestJson = [" + requestJson + "]");
                        Map<String, Object> map = JSONObject.parseObject(requestJson.getString("map"));
                        HashMap<String,Double> mapcopy = new HashMap<>();
                        for (Map.Entry<String, Object> entry : map.entrySet()) {
                            mapcopy.put(entry.getKey(),Double.valueOf(entry.getValue().toString()));
                        }
                        reponseJson = SubjectEval.SubEval(requestJson.getString("tecText"), requestJson.getString("stuText"),mapcopy);
                        reponseJson.put("taskId",requestJson.getString("taskId"));
                    }catch (Exception e){
                        reponseJson.put("status",false);
                        reponseJson.put("code","-1");
                        reponseJson.put("message","未知异常");
                    }finally {
                        return reponseJson;
                    }
                }
            });
        }catch (Exception e){
            System.err.println("\n=== 启动时异常 ===\n");
            e.printStackTrace();
        }
    }
}
