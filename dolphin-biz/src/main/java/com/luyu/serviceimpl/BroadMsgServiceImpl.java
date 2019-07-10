package com.luyu.serviceimpl;

import com.alibaba.fastjson.JSON;
import com.luyu.mq.MqBroadService;
import com.luyu.server.util.SessionUtil;
import com.luyu.service.BroadMsgService;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * Created by luyu on 2019/7/10.
 */
@Service("broadMsgService")
public class BroadMsgServiceImpl implements BroadMsgService { //todo 接入rpc框架 对外提供广播服务

    @Resource
    private MqBroadService mqBroadService;

    @Override
    public void broadMsg(String groupId, Map<String, String> msgMap) {
        ChannelGroup channels = SessionUtil.getChannelGroup(groupId);
        //not null broad client which connect with current machine
        if (!channels.isEmpty()) {
            String jsonMsg = JSON.toJSONString(msgMap);
            TextWebSocketFrame frame = new TextWebSocketFrame(jsonMsg);
            channels.write(frame);
        }
        //mq broad to other machine
        mqBroadService.send(msgMap);
    }

}
