package com.luyu.server.starter;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import com.luyu.server.handlers.HttpReqHandler;
import com.luyu.server.handlers.WsReqHandler;

/**
 * Created by luyu on 2019/7/9.
 */
@Service
public class WebSocketServer implements InitializingBean {

    public void afterPropertiesSet() throws Exception {
        start();
    }

    private void start() {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();

        try {
            bootstrap.channel(NioServerSocketChannel.class)
                    .group(bossGroup, workerGroup)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel ch) throws Exception {
                            //将请求和应答消息编码or解码为http消息
                            ch.pipeline().addLast("HN_HTTP_CODEC", new HttpServerCodec());
                            //将http消息的多个部分组合为一条完整的http消息
                            ch.pipeline().addLast("HN_HTTP_AGGREGATOR", new HttpObjectAggregator(65536));
                            //支持发送H5文件，主要用于支持浏览器和服务端的ws通信
                            ch.pipeline().addLast("HN_HTTP_CHUNK", new ChunkedWriteHandler());
                            ch.pipeline().addLast(new HttpReqHandler());
                            ch.pipeline().addLast(new WsReqHandler());
                        }
                    });
            ChannelFuture future = bootstrap.bind(6666).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
