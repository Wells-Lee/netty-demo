package com.wells.demo.netty.tcp.question;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

import java.util.UUID;

/**
 * Description 自定义Handler实现
 * Created by wells on 2020-05-13 09:41:05
 */

public class NettyServerHandler extends ChannelInboundHandlerAdapter {
    /**
     * ChannelHandlerContext 上下文对象，其中有：Channel、Pipeline
     */

    private int count = 0;

    // read msg
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel channel = ctx.channel();

        ByteBuf byteBuf = (ByteBuf) msg;

        byte[] buffer = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(buffer);
        // 将 buffer 转成字符串
        String message = new String(buffer, CharsetUtil.UTF_8);
        System.out.println(String.format("from client msg, addr:%s, msg:%s, count:%d",
                channel.remoteAddress(), message, (++this.count)));
        // 服务器回送数据给客户端, 回送一个随机 id
        ByteBuf responseByteBuf = Unpooled.copiedBuffer(UUID.randomUUID().toString(), CharsetUtil.UTF_8);
        ctx.writeAndFlush(responseByteBuf);
    }

    // catch exception
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
