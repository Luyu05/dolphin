package com.luyu.service;

import java.util.Map;

/**
 * Created by luyu on 2019/7/10.
 */
public interface BroadMsgService {
    void broadMsg(String groupId, Map<String, String> msgMap);
}
