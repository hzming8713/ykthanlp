import com.alibaba.fastjson.JSONObject;
import com.hankcs.hanlp.Redis.RpcCallback;
import com.hankcs.hanlp.Redis.SubscribeService;
import com.hankcs.hanlp.SubjectEval;

import java.util.HashMap;
import java.util.Map;

import static com.hankcs.hanlp.Redis.RedisConfig.requestChannel_questionhanlp;
import static com.hankcs.hanlp.Redis.RedisConfig.responseChannel_questionhanlp;

public class JedisTest {
    private static SubscribeService subscribeService = new SubscribeService();
    public static void main(String[] args) {
        subscribeService.subscribe(requestChannel_questionhanlp,responseChannel_questionhanlp,new RpcCallback(){
            @Override
            public JSONObject handle(JSONObject requestJson) {
                /*参数处理*/
                String StuText = requestJson.getString("text");//学生答案
                Map<String,Double> map = (Map)JSONObject.parseObject(requestJson.getString("keywordmap"));
                /*算法调用*/

                /*
                 *  hzming: 将SubjectEval.SubEval(null,StuText,(HashMap<String, Double>) map);函数实现，兼容以前版本自动评分
                 */
                JSONObject jsonObject = SubjectEval.SubEval(null,StuText,(HashMap<String, Double>) map);

                jsonObject.put("taskId",requestJson.getString("taskId"));
                return jsonObject;
            }
        });
    }
}
