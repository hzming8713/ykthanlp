package com.ahaxt.hanlp.redis;

import com.ahaxt.hanlp.SubjectEval;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RedisMain extends SubscribeService{
//    public final static String requestChannel_questionhanlp = "request_questionhanlp";
//    public final static String responseChannel_questionhanlp = "response_questionhanlp";
    public final static String requestChannel_questionhanlp = "requestChannel_autoAssess";
    public final static String responseChannel_questionhanlp = "responseChannel_autoAssess";

    public static void main(String[] args) {
        subscribe();
    }

    static long num =0;
    static String loggerFlag;
    static SimpleDateFormat sdf = new SimpleDateFormat("yy/MM/dd hh:mm:ss:SS");
    public static void subscribe(){
        try {
            new SubscribeService().subscribe(requestChannel_questionhanlp,
                    new RpcCallback(){
                        @Override
                        public synchronized void handle(String jsonString) {
                            try {
                                JSONObject requestJson = JSONObject.parseObject(jsonString);
                                String TaskId = requestJson.getString("TaskId");
                                System.err.println(requestChannel_questionhanlp+" : "+jsonString);
                                if (!requestChannel_questionhanlp.equals(TaskId)) {
                                    JSONObject responseJson = new JSONObject();
                                    loggerFlag = String.format("%d",++num);
                                    try {
                                        System.out.println("\n"+sdf.format(new Date())+" <INFO-请求("+ loggerFlag +")>requestJson:"+requestJson);
                                        int type = requestJson.getInteger("type");
                                        if(1 == type){//情景1 单人自动评分
                                            Map<String, Object> map = requestJson.getJSONObject("map");
                                            HashMap<String,Double> mapcopy = map.entrySet().stream()
                                                    .collect(Collectors.toMap(Map.Entry::getKey,
                                                            entry -> Double.valueOf(entry.getValue().toString()),
                                                            (a, b) -> b,
                                                            () -> new HashMap<>(map.size())));
                                            responseJson = SubjectEval.SubEval(requestJson.getString("tecText")
                                                    ,requestJson.getString("stuText"),mapcopy);//算法调用
                                            responseJson.put("TaskId",TaskId);
                                            responseJson.put("type",type);
                                        }else if(2 == type){//情景2 多人批量自动评分
                                            String tecText = requestJson.getString("tecText");
                                            Map<String, Object> map = requestJson.getJSONObject("map");
                                            HashMap<String,Double> mapcopy = map.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> Double.valueOf(entry.getValue().toString()), (a, b) -> b, () -> new HashMap<>(map.size())));
                                            JSONArray jsonArray = requestJson.getJSONArray("stuText");
                                            JSONArray data = new JSONArray();
                                            IntStream.range(0, jsonArray.size()).mapToObj(jsonArray::getJSONObject).forEach(
                                                jsonObject -> {
                                                    JSONObject json = SubjectEval.SubEval(tecText, jsonObject.getString("stuText"), mapcopy);//算法调用
                                                    json.put("TaskId", jsonObject.getString("TaskId"));
                                                    data.add(json);
                                                });
                                            responseJson.put("code",200);
                                            responseJson.put("message","ok");
                                            responseJson.put("type",type);
                                            responseJson.put("TaskId",TaskId);
                                            responseJson.put("data",data);
                                        }
                                    }catch (Exception e){
                                        System.err.println("error("+ loggerFlag +")");
                                    }finally {
                                        System.out.println("\n"+sdf.format(new Date())+" <INFO-回执("+ loggerFlag +")>responseJson:"+responseJson);
                                    }
                                    new SubscribeService().publish(responseChannel_questionhanlp,responseJson.toString());
                                }
                            } catch (Exception e) {
                                JSONObject jo = new JSONObject();
                                jo.put("status",false);
                                jo.put("message",String.format("<ERROR-50002> jedis value 非 json 格式 requestString:【%s】", jsonString));
                                System.err.println(jo.toString());
                                new SubscribeService().publish(responseChannel_questionhanlp,jo.toString());
                            }
                        }
                    });
        }catch (Exception e){
            System.err.println("\n=== 启动时异常 ===\n");
            e.printStackTrace();
        }

    }

    public void getPath(){
        try {
            // 第一种：获取类加载的根路径   D:\git\daotie\daotie\target\classes
            File f = new File(this.getClass().getResource("/").getPath());
            System.out.println(f+"\n");//X:\Users\hongzhangming\Desktop\HzmingHot\IXUETANG\GitRepository\ykthanlp\target\classes

            // 获取当前类的所在工程路径; 如果不加“/”  获取当前类的加载目录  D:\git\daotie\daotie\target\classes\my
            File f2 = new File(this.getClass().getResource("").getPath());
            System.out.println(f2+"\n");//X:\Users\hongzhangming\Desktop\HzmingHot\IXUETANG\GitRepository\ykthanlp\target\classes\com\ahaxt\hanlp\redis

            // 第二种：获取项目路径    D:\git\daotie\daotie
            File directory = new File("");// 参数为空
            String courseFile = directory.getCanonicalPath();
            System.out.println(courseFile+"\n");//X:\Users\hongzhangming\Desktop\HzmingHot\IXUETANG\GitRepository\ykthanlp


            // 第三种：  file:/D:/git/daotie/daotie/target/classes/
            URL xmlpath = this.getClass().getClassLoader().getResource("");
            System.out.println(xmlpath+"\n");//file:/X:/Users/hongzhangming/Desktop/HzmingHot/IXUETANG/GitRepository/ykthanlp/target/classes/


            // 第四种： D:\git\daotie\daotie
            System.out.println(System.getProperty("user.dir")+"\n");//X:\Users\hongzhangming\Desktop\HzmingHot\IXUETANG\GitRepository\ykthanlp
            /*
             * 结果： C:\Documents and Settings\Administrator\workspace\projectName
             * 获取当前工程路径
             */

            // 第五种：  获取所有的类路径 包括jar包的路径
//            System.out.println(System.getProperty("java.class.path")+"\n");

        }catch (Exception e){};

    }
}
