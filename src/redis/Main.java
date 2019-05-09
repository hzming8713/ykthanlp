package redis;

import com.alibaba.fastjson.JSONObject;
import com.hankcs.hanlp.SubjectEval;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Main {
    static long num =0;
    public static void main(String[] args) {
        try {
            new SubscribeService().subscribe(RedisConfig.requestChannel_questionhanlp,RedisConfig.responseChannel_questionhanlp,new RpcCallback(){
                @Override
                public JSONObject handle(JSONObject requestJson) {
                    JSONObject responseJson = new JSONObject();
                    SimpleDateFormat sdf = new SimpleDateFormat("yy/MM/dd hh:mm:ss:SS");
                    try{
                        System.out.println("\n"+sdf.format(new Date())+" <INFO-请求>("+ num +")：" + requestJson );
                        Map<String, Object> map = JSONObject.parseObject(requestJson.getString("map"));
                        HashMap<String,Double> mapcopy = new HashMap<>();
                        for (Map.Entry<String, Object> entry : map.entrySet()) {
                            mapcopy.put(entry.getKey(),Double.valueOf(entry.getValue().toString()));
                        }
                        responseJson = SubjectEval.SubEval(requestJson.getString("tecText"), requestJson.getString("stuText"),mapcopy);
                        responseJson.put("taskId",requestJson.getString("taskId"));
                    }catch (Exception e){
                        responseJson.put("status",false);
                        responseJson.put("code","-1");
                        responseJson.put("message","未知异常");
                    }finally {
                        System.out.println(sdf.format(new Date())+" <INFO-回执>("+ num++ +")："+responseJson.toString());
                        responseJson.put("data","{\"grade:\":0.4,\"hitKeyword\":[\"洪\",\"张\"]}");//todo 测试一会删掉
                        return responseJson;
                    }
                }
            });
        }catch (Exception e){
            System.err.println("\n=== 启动时异常 ===\n");
            e.printStackTrace();
        }
    }
}
