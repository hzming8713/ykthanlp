package com.ahaxt.hanlp.redis;

import com.ahaxt.hanlp.SubjectEval;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

public class RedisMain {
    static long num =0;
    static String loggerFlag;
    static SimpleDateFormat sdf = new SimpleDateFormat("yy/MM/dd hh:mm:ss:SS");
    public static void main(String[] args) {
        new RedisMain().getPath();
        try {
            new SubscribeService().subscribe(RedisConfig.requestChannel_questionhanlp,RedisConfig.responseChannel_questionhanlp,new RpcCallback(){
                @Override
                public JSONObject handle(JSONObject requestJson) {
                    loggerFlag = String.format("%d",++num);
                    JSONObject responseJson = new JSONObject();
                    try {
                        System.out.println("\n"+sdf.format(new Date())+" <INFO-请求("+ loggerFlag +")>requestJson:"+requestJson);
                        int type = requestJson.getInteger("type");
                        if(type == 1 ){//情景1 单人自动评分
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
                            responseJson.put("message","ok");
                            responseJson.put("taskId",requestJson.getString("taskId"));
                            responseJson.put("data",data2);
                        }
                    }catch (Exception e){
                        System.err.println("error("+ loggerFlag +")");
                    }finally {
                        System.out.println("\n"+sdf.format(new Date())+" <INFO-回执("+ loggerFlag +")>responseJson:"+responseJson);
                        return responseJson;
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
