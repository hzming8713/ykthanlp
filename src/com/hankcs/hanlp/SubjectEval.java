package com.hankcs.hanlp;

import com.alibaba.fastjson.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 此处为各个函数汇总后的结果，在Test中调用输出最终成绩
 */

public class SubjectEval {

    public static JSONObject SubEval(String TecText, String StuText, HashMap<String,Double> map) {
        JSONObject jsonObject = new JSONObject(new LinkedHashMap());
        //当前执行加入判断，当教师输入为空时，则仅输出grade与hitkeyword
        try {
            JSONObject data = new JSONObject(new LinkedHashMap());
            YKTKeyword YKT = new YKTKeyword(StuText);
            List<String> hitKeyword=new ArrayList<String>();
            double grade = YKT.yktCorrectRate(map,hitKeyword);
            if (TecText==null){
                data.put("grade:",grade);//初始成绩
                data.put("hitKeyword",hitKeyword);//学生命中关键词
            }else {
                data.put("grade:", grade);//初始成绩
                data.put("hitKeyword", hitKeyword);//学生命中关键词
                //结合分数中整合关键词词性正确率
                double KeyResult = KeyWordRate.KeyWord(TecText, StuText, map);
                grade = grade * (0.9 + 0.1 * KeyResult);
                data.put("关键词正确率", KeyResult);

                //判断词性
                double WordResult = WordProperty.Property(StuText);
                if (WordResult >= 0.8) {
                    grade = grade * 0.9;
                }
                data.put("词性重复频率", WordResult);

                //判断主谓
                boolean SenResult = SenCPST.Cpst(TecText, StuText);
                if (SenResult == false) {
                    grade = grade * 0.9;
                }
                data.put("主谓关系", SenResult);

                //判断句子中被动与并列是否正确！
                boolean OrderResult = KeyOrder.KeyOrder(TecText, StuText);
                if (OrderResult == false) {
                    grade = grade * 0.9;
                }
                data.put("并列被动关系", OrderResult);

                DecimalFormat Newdf = new DecimalFormat("0.##");
                data.put("score", Newdf.format(grade));//最终成绩
            }
            jsonObject.put("code",200);
            jsonObject.put("status",true);
            jsonObject.put("message","ok");
            jsonObject.put("data",data);
        }catch (Exception e){
            jsonObject.put("code",30302);
            jsonObject.put("status",false);
            jsonObject.put("message",String.format("<ERROR-30302> demo出了 xxx 错误"));
        }finally {
            return jsonObject;
        }
    }
/*
2019年5月8日：汪宇飞注释，函数重载已弃用！
    //函数重载，考虑教师输入文本为空时的情况
    public static JSONObject SubEval(String StuText, HashMap<String,Double> map){
        JSONObject jsonObject= new JSONObject(new LinkedHashMap());
        try{
            YKTKeyword YKT = new YKTKeyword(StuText);
            List<String> hitKeyword=new ArrayList<String>();
            double grade = YKT.yktCorrectRate(map,hitKeyword);
            jsonObject.put("grade:",grade);//初始成绩
            jsonObject.put("hitKeyword",hitKeyword);//学生命中关键词
        }catch (IndexOutOfBoundsException e){
            System.out.println("Exception Thrown"+e);
        }
        return jsonObject;
    }*/
}

