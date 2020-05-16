package com.wells.demo.netty.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

/**
 * Description 
 * Created by wells on 2020-05-16 22:03:02
 */

public class MyServerHandler extends SimpleChannelInboundHandler<HttpObject> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
        // 判断msg是不是http请求
        if (msg instanceof HttpRequest) {
            System.out.println("pipline:" + ctx.pipeline().hashCode());
            System.out.println("msg class:" + msg.getClass());
            System.out.println("client addr:" + ctx.channel().remoteAddress());

            // 可以通过URI过滤指定请求

            /**
             * 响应
             * 1、构建响应内容
             * 2、构建Response
             */
            ByteBuf content = Unpooled.copiedBuffer("hello, from server reply", CharsetUtil.UTF_8);

            // 构建
            DefaultHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content);

            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain");
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());

            ctx.writeAndFlush(response);
        }
    }
}
