package com.ahaxt.hanlp.redis;

/**
 * @author hongzhangming
 */
public interface RpcCallback {
    void handle(String jsonString);
}
