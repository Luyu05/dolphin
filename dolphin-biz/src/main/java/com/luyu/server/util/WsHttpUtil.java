package com.luyu.server.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.util.CharsetUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by luyu on 2019/7/9.
 */
public class WsHttpUtil {

    public static void sendHttpResponse(ChannelHandlerContext ctx,
                                        FullHttpRequest req, FullHttpResponse res) {
        // 返回应答给客户端
        if (res.status().code() != 200) {
            ByteBuf buf = Unpooled.copiedBuffer(res.status().toString(),
                    CharsetUtil.UTF_8);
            res.content().writeBytes(buf);
            buf.release();
        }

        // 如果是非Keep-Alive，关闭连接
        ChannelFuture f = ctx.channel().writeAndFlush(res);
        if (!HttpUtil.isKeepAlive(req) || res.status().code() != 200) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }

    public static Map<String, List<String>> getParameterListMap(
            FullHttpRequest request) {

        try {
            QueryStringDecoder queryStringDecoder = new QueryStringDecoder(
                    request.uri());
            Map<String, List<String>> parameters = queryStringDecoder.parameters();

            if (request.content().isReadable()) {
                HttpPostRequestDecoder httpPostRequestDecoder = new HttpPostRequestDecoder(
                        request);
                List<InterfaceHttpData> datas = httpPostRequestDecoder
                        .getBodyHttpDatas();
                if (datas != null && !datas.isEmpty()) {
                    for (InterfaceHttpData interfaceHttpData : datas) {
                        if (interfaceHttpData instanceof Attribute) {
                            Attribute attribute = (Attribute) interfaceHttpData;
                            List<String> list = parameters
                                    .get(interfaceHttpData.getName());
                            if (list != null) {
                                list.add(attribute.getValue());
                            } else {
                                list = new ArrayList<String>(1);
                                list.add(attribute.getValue());
                                parameters.put(interfaceHttpData.getName(),
                                        list);
                            }
                        }
                    }
                }
            }
            return parameters;
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Cannot parse parameters from http request", e);
        }
    }
}
