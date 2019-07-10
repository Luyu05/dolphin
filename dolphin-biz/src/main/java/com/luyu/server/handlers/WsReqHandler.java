package com.luyu.server.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.AttributeKey;
import com.luyu.server.util.SessionUtil;

/**
 * Created by luyu on 2019/7/9.
 */
public class WsReqHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame msg) throws Exception {
        WebSocketServerHandshaker handshaker = (WebSocketServerHandshaker) ctx.channel().attr(AttributeKey.valueOf("WS_HAND_SHAKER")).get();
        String groupId = (String) ctx.channel().attr(AttributeKey.valueOf("WS_GROUP_ID")).get();
        if (msg instanceof CloseWebSocketFrame) {
            handshaker.close(ctx.channel(),
                    (CloseWebSocketFrame) msg.retain());
            SessionUtil.getChannelGroup(groupId).remove(ctx.channel());
            return;
        } else if (msg instanceof PingWebSocketFrame) { // 判断是否是Ping消息
            ctx.channel().write(
                    new PongWebSocketFrame(msg.content().retain()));
            return;
        } else if (!(msg instanceof TextWebSocketFrame)) {
            throw new UnsupportedOperationException(String.format(
                    "%s frame types not supported", msg.getClass().getName()));
        }
    }
}
