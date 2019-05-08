package redis;

import com.alibaba.fastjson.JSONObject;
import com.hankcs.hanlp.SubjectEval;

import java.util.Map;

public class Main {
    public static void main(String[] args) {
        try {
            new SubscribeService().subscribe(RedisConfig.requestChannel_questionhanlp,RedisConfig.responseChannel_questionhanlp,new RpcCallback(){
                @Override
                public JSONObject handle(JSONObject requestJson) {
                    JSONObject jsonObject = new JSONObject();
                    try{
                        jsonObject.put("taskId",requestJson.getString("taskId"));
                        System.out.println("requestJson = [" + requestJson + "]");
                        jsonObject = SubjectEval.SubEval(
                            requestJson.getString("tecText"),
                            requestJson.getString("stutext"),
                            (Map)JSONObject.parseObject(requestJson.getString("keywordmap"))
                        );
                    }catch (Exception e){
                        jsonObject.put("status",false);
                        jsonObject.put("code","-1");
                        jsonObject.put("message","未知异常");
                    }finally {
                        return jsonObject;
                    }
                }
            });
        }catch (Exception e){
            System.err.println("\n=== 启动时异常 ===\n");
            e.printStackTrace();
        }
    }
}
