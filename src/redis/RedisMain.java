package redis;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hankcs.hanlp.SubjectEval;

import java.text.SimpleDateFormat;
import java.util.*;

public class RedisMain {
    static long num =0;
    static SimpleDateFormat sdf = new SimpleDateFormat("yy/MM/dd hh:mm:ss:SS");
    public static void main(String[] args) {
        try {
            new SubscribeService().subscribe(RedisConfig.requestChannel_questionhanlp,RedisConfig.responseChannel_questionhanlp,new RpcCallback(){
                @Override
                public JSONObject handle(JSONObject requestJson) {
                    JSONObject responseJson = new JSONObject();
                    try {
                        System.out.println("\n"+sdf.format(new Date())+" <INFO-请求("+num+")>requestJson:"+requestJson);
                        int type = requestJson.getInteger("type");
                        if(type == 1 ){////情景1 单人自动评分
                            Map<String, Object> map = requestJson.getJSONObject("map");
                            HashMap<String,Double> mapcopy = new HashMap<>();
                            for (Map.Entry<String, Object> entry : map.entrySet()) {
                                mapcopy.put(entry.getKey(),Double.valueOf(entry.getValue().toString()));
                            }
                            responseJson = SubjectEval.SubEval(requestJson.getString("tecText"),requestJson.getString("stuText"),mapcopy);//算法调用
                            responseJson.put("taskId",requestJson.getString("taskId"));
                        }else if(type == 2){//情景2 多人批量自动评分
                            String tecText = requestJson.getString("tecText");
                            Map<String, Object> map = requestJson.getJSONObject("map");
                            HashMap<String,Double> mapcopy = new HashMap<>();
                            for (Map.Entry<String, Object> entry : map.entrySet()) {
                                mapcopy.put(entry.getKey(),Double.valueOf(entry.getValue().toString()));
                            }
                            List<JSONObject> data = (List<JSONObject>) requestJson.get("data");
                            List<JSONObject> data2 = new ArrayList<>();
                            for (JSONObject jsonObject:data) {
                                JSONObject json = SubjectEval.SubEval(tecText,jsonObject.getString("stuText"),mapcopy);//算法调用
                                json.put("id",jsonObject.getString("id"));
                                data2.add(json);
                            }
                            responseJson.put("code",200);
                            responseJson.put("messate","ok");
                            responseJson.put("taskId",requestJson.getString("taskId"));
                            responseJson.put("data",data2);
                        }
                    }catch (Exception e){
                        System.err.println("error("+num+")");
                    }finally {
                        System.out.println("\n"+sdf.format(new Date())+" <INFO-回执("+num+")>responseJson:"+responseJson);
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
