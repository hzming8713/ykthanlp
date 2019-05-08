package com.hankcs.hanlp;

import com.alibaba.fastjson.JSONObject;

import java.text.DecimalFormat;
import java.util.*;

/**
 * 此处为各个函数汇总后的结果，在Test中调用输出最终成绩
 */

public class SubjectEval {

    public static JSONObject SubEval(String TecText, String StuText, Map<String,Double> map) {
        JSONObject jsonObject = new JSONObject(new LinkedHashMap());
        jsonObject.put("code",200);
        jsonObject.put("message","ok");
        //当前执行加入判断，当教师输入为空时，则仅输出grade与hitkeyword
        try {
            JSONObject data = new JSONObject(new LinkedHashMap());
            YKTKeyword YKT = new YKTKeyword(StuText);
            List<String> hitKeyword=new ArrayList<String>();
            double grade = YKT.yktCorrectRate((HashMap<String, Double>) map,hitKeyword);
            if (TecText==null){
                data.put("grade:",grade);//初始成绩
                data.put("hitKeyword",hitKeyword);//学生命中关键词
            }else {
                data.put("grade:", grade);//初始成绩
                data.put("hitKeyword", hitKeyword);//学生命中关键词
                //结合分数中整合关键词词性正确率
                double KeyResult = KeyWordRate.KeyWord(TecText, StuText, (HashMap<String, Double>)map);
                grade = grade * (0.9 + 0.1 * KeyResult);
                data.put("keyResult", KeyResult);  //关键词正确率

                //判断词性
                double WordResult = WordProperty.Property(StuText);
                if (WordResult >= 0.8) {
                    grade = grade * 0.9;
                }
                data.put("wordResult", WordResult); //词性重复频率

                //判断主谓
                boolean SenResult = SenCPST.Cpst(TecText, StuText);
                if (SenResult == false) {
                    grade = grade * 0.9;
                }
                data.put("senResult", SenResult);    //主谓关系

                //判断句子中被动与并列是否正确！
                boolean OrderResult = KeyOrder.KeyOrder(TecText, StuText);
                if (OrderResult == false) {
                    grade = grade * 0.9;
                }
                data.put("orderResult", OrderResult);    //并列被动关系

                DecimalFormat Newdf = new DecimalFormat("0.##");
                data.put("score", Newdf.format(grade));//最终成绩
            }
            jsonObject.put("data",data);
        }catch (Exception e){
            jsonObject.put("code",-1);
            jsonObject.put("message",String.format("<ERROR> demo出了 xxx 错误"));
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

