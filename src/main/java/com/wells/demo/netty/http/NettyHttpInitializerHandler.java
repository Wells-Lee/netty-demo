package com.wells.demo.netty.http;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * Description 
 * Created by wells on 2020-05-16 21:58:02
 */

public class NettyHttpInitializerHandler extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        // 1、得到Pipeline
        ChannelPipeline pipeline = ch.pipeline();

        // 2、增加编解码器Handler
        pipeline.addLast("HttpServerCodec", new HttpServerCodec());

        // 3、增加自定义Handler
        pipeline.addLast("MyServerHandler", new MyServerHandler());
    }
}
