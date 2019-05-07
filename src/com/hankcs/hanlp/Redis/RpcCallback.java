package com.hankcs.hanlp.Redis;

import com.alibaba.fastjson.JSONObject;

public interface RpcCallback {
    JSONObject handle(JSONObject requestJson);
}
