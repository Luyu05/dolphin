package com.luyu.server.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.util.AttributeKey;
import org.springframework.util.CollectionUtils;
import com.luyu.server.util.SessionUtil;
import com.luyu.server.util.WsHttpUtil;

import java.util.List;
import java.util.Map;

import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * Created by luyu on 2019/7/9.
 */
public class HttpReqHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private WebSocketServerHandshaker handShaker;

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        //异常情况
        if (msg.decoderResult().isFailure()
                || (!"websocket".equals(msg.headers().get("Upgrade")))) {
            WsHttpUtil.sendHttpResponse(ctx, msg, new DefaultFullHttpResponse(HTTP_1_1, BAD_REQUEST));
            return;
        }
        Map<String, List<String>> param = WsHttpUtil.getParameterListMap(msg);
        if (CollectionUtils.isEmpty(param.get("bizKey")) || !isValid(param.get("bizKey"))) {
            WsHttpUtil.sendHttpResponse(ctx, msg, new DefaultFullHttpResponse(HTTP_1_1,
                    new HttpResponseStatus(HttpResponseStatus.NOT_ACCEPTABLE.code(), "key invalid")));
            return;
        }
        //ws 服务端主动握手
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory("ws://" +
                msg.headers().get(HttpHeaderNames.HOST) + "/websocket", null, false);
        handShaker = wsFactory.newHandshaker(msg);
        if (null == handShaker) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
            return;
        } else {
            handShaker.handshake(ctx.channel(), msg);
        }
        //将后续handler需要的资源存放到channel上
        ctx.channel().attr(AttributeKey.valueOf("WS_HAND_SHAKER")).set(handShaker);
        ctx.channel().attr(AttributeKey.valueOf("WS_GROUP_ID")).set(param.get("groupId").get(0));
        SessionUtil.bindChannelGroup(param.get("groupId").get(0), ctx.channel());
        //移除当前handler
        ctx.channel().pipeline().removeFirst();
        //添加idle handler
        ctx.channel().pipeline().addFirst(WsIdleStateHandler.instance);
    }

    //身份校验，自行实现逻辑
    private boolean isValid(List<String> list) {
        return true;
    }

}
