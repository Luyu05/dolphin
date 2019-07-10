package com.luyu.server.handlers;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.AttributeKey;
import com.luyu.server.util.SessionUtil;

import java.util.concurrent.TimeUnit;

/**
 * Created by luyu on 2019/7/9.
 */
@ChannelHandler.Sharable
public class WsIdleStateHandler extends IdleStateHandler {

    public static WsIdleStateHandler instance = new WsIdleStateHandler();

    private WsIdleStateHandler() {
        super(120, 0, 0, TimeUnit.SECONDS);
    }

    @Override
    protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) {
        String groupId = (String) ctx.channel().attr(AttributeKey.valueOf("WS_GROUP_ID")).get();
        SessionUtil.getChannelGroup(groupId).remove(ctx.channel());
    }

}
