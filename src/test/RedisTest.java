import com.alibaba.fastjson.JSONObject;
import com.hankcs.hanlp.SubjectEval;

import java.text.SimpleDateFormat;
import java.util.*;

public class RedisTest {
    /* 模拟参数 */
    static String taskId="2389419*32412#";
    static String tecText="上层建筑是社会意识形态和政治法律制度，包含了阶级关系和维持这种关系的国家机器和社会意识形态，及其政治法律制度，组织等";
    static String stuText="上层建筑是社会意识形态和政治法律制度";
    static Map<String,Double> map = new HashMap();
    static List<JSONObject> data = new ArrayList<>();
    static {
        map.put("社会意识形态",0.25);
        map.put("国家机器",0.25);
        map.put("政治法律制度",0.25);
        map.put("阶级关系", 0.15);
        map.put("上层建筑", 0.1);

        for (int i =0; i<100;i++) {
            JSONObject stujson = new JSONObject();
            stujson.put("stuText","上层建筑是社会意识形态和政治法律制度，包含了阶级关系和维持这种关系的国家机器和社会意识形态上层建筑是社会意识形态和政治法律制度，包含了阶级关系和维持这种关系的国家机器和社会意识形态上层建筑是社会意识形态和政治法律制度，包含了阶级关系和维持这种关系的国家机器和社会意识形态上层建筑是社会意识形态和政治法律制度，包含了阶级关系和维持这种关系的国家机器和社会意识形态"+i);
            stujson.put("id",""+i);
            data.add(stujson);
        }
    }
    static JSONObject requestJson1 = new JSONObject();
    static JSONObject requestJson2 = new JSONObject();
    static JSONObject requestJson3 = new JSONObject();
    static JSONObject requestJson4 = new JSONObject();
    static {
        requestJson1.put("type",1);
        requestJson1.put("taskId",taskId);
        requestJson1.put("stuText",stuText);
        requestJson1.put("map",map);

        requestJson2.put("type",2);
        requestJson2.put("taskId",taskId);
        requestJson2.put("tecText",tecText);
        requestJson2.put("stuText",stuText);
        requestJson2.put("map",map);

        requestJson3.put("type",3);
        requestJson3.put("taskId",taskId);
        requestJson3.put("map",map);
        requestJson3.put("data",data);

        requestJson4.put("type",4);
        requestJson4.put("taskId",taskId);
        requestJson4.put("tecText",tecText);
        requestJson4.put("map",map);
        requestJson4.put("data",data);
    }

    public static void main(String[] args){
        SimpleDateFormat sdf = new SimpleDateFormat("yy/MM/dd hh:mm:ss:SS");
        JSONObject requestJson = requestJson3;
        //=========== 模拟调用 =========
        JSONObject responseJson = new JSONObject();
        try {
            System.out.println("\n"+sdf.format(new Date())+" <INFO-请求>requestJson:"+requestJson);
            int type = requestJson.getInteger("type");
            if(type == 1 || type == 2){////情景1或2
                Map<String, Object> map = requestJson.getJSONObject("map");
                HashMap<String,Double> mapcopy = new HashMap<>();
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    mapcopy.put(entry.getKey(),Double.valueOf(entry.getValue().toString()));
                }
                responseJson = SubjectEval.SubEval(requestJson.getString("tecText"),requestJson.getString("stuText"),mapcopy);//算法调用
                responseJson.put("taskId",requestJson.getString("taskId"));
            }else if(type == 3 || type == 4){//情景3或4
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
            System.err.println("error:"+requestJson);
        }finally {
            System.out.println("\n"+sdf.format(new Date())+" <INFO-回执>responseJson:"+responseJson);
        }
    }
}
