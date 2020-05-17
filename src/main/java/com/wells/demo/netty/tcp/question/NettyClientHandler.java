package com.wells.demo.netty.tcp.question;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

/**
 * Description 
 * Created by wells on 2020-05-13 12:57:33
 */

public class NettyClientHandler extends ChannelInboundHandlerAdapter {
    private int count = 0;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        for (int i = 0; i < 10; i++) {
            String msg = String.format("hello server, client:%s, server:%s",
                    channel.localAddress(), channel.remoteAddress());
            ctx.writeAndFlush(Unpooled.copiedBuffer(msg, CharsetUtil.UTF_8));
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel channel = ctx.channel();
        ByteBuf byteBuf = (ByteBuf) msg;


        byte[] buffer = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(buffer);
        String message = new String(buffer, CharsetUtil.UTF_8);
        System.out.println(String.format("from server reply, addr:%s, msg:%s, count:%d",
                channel.remoteAddress(), message, (++this.count)));
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
