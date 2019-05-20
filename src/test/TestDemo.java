import com.alibaba.fastjson.JSONObject;
import com.ahaxt.hanlp.SubjectEval;

import java.util.HashMap;


public class TestDemo {
    /* 静态参数 */
    static String tecText="上层建筑是社会意识形态和政治法律制度，包含了阶级关系和维持这种关系的国家机器和社会意识形态，及其政治法律制度，组织等";
    static String stuText="上层建筑是社会意识形态和政治法律制度";
    static HashMap map = new HashMap<String,Double>();
    static {
        map.put("社会意识形态",0.25);
        map.put("国家机器",0.25);
        map.put("政治法律制度",0.25);
        map.put("阶级关系", 0.15);
        map.put("上层建筑", 0.1);
    }
    public static void main(String[] args){
        //情景 1
        System.err.println("SubEvalTest(stuText,map):\n" + SubEvalTest(null,stuText,map) + "\n");
        //情景 2
        System.err.println("SubEvalTest(tecText,stuText,map):\n" + SubEvalTest(tecText,stuText,map) + "\n");
    }

    /**
     *  算法调用
     * return json格式   // code !=200 即出现算法异常
     *      {
     *          "code":200,         int
     *          "message":"ok",     String
     *          "data":{}           json
     *      }
     */
    public static JSONObject SubEvalTest(String tecText, String stuText, HashMap<String,Double> map){
        return SubjectEval.SubEval(tecText,stuText,map);
    }
}
