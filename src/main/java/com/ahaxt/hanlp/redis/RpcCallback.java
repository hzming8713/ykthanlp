package com.ahaxt.hanlp.redis;

import com.alibaba.fastjson.JSONObject;

public interface RpcCallback {
    JSONObject handle(JSONObject requestJson);
}
