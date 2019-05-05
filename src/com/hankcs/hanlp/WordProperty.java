package com.hankcs.hanlp;
import com.hankcs.hanlp.corpus.dependency.CoNll.CoNLLSentence;
import com.hankcs.hanlp.corpus.dependency.CoNll.CoNLLWord;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 此程序的作用是在于判断句子的词性是否重复性过高，其中设定的权值可以进行改变
 * 程序输入的为句子，输出的为判断结果，后期可将输出改为分数的权重值
 */

public class WordProperty {
    public static double Property(String StuText){
        //直接拿到数组中，进行正序遍历
        Double result= new Double("0.0");
        try {
            CoNLLSentence Answer = HanLP.parseDependency(StuText);
            CoNLLWord[] StuArray = Answer.getWordArray();
            String[] strArr = new String[StuArray.length];
            for (int i = 0; i <= StuArray.length - 1; i++) {
                CoNLLWord word = StuArray[i];
                strArr[i] = word.CPOSTAG;
            }
            //String Array = Arrays.toString(strArr);
            //System.out.println(Array);
            Map<String, Integer> ProMap = new HashMap<String, Integer>();
            for (int i = 0; i < strArr.length; i++) {
                if (ProMap.containsKey(strArr[i])) {//检查当前值在数组中是否还存在
                    ProMap.put(strArr[i], ProMap.get(strArr[i]) + 1);//map存储当前重复值与重复次数值
                } else {
                    ProMap.put(strArr[i], 1);
                }
            }
            String key = new String();
            double val = 0;//最多的次数
            Iterator<Map.Entry<String, Integer>> iterator = ProMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Integer> next = iterator.next();
                key = next.getKey();
                Integer value = next.getValue();
                if (value > val) {
                    val = value;
                }
            }
            result = val / StuArray.length;
            DecimalFormat df = new DecimalFormat("0.##");
        }catch (Exception e){
            System.out.println("Exception:"+e);
        }
        return result;
    }

/* voyageryf: 2019年5月5日
    //主程序进行测试
    public static void main(String[] args){
        String StuText="上层建筑是社会意识形态和政治法律制度";
        Property(StuText);
    }
    */
}

