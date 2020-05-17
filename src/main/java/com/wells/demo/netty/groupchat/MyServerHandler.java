package com.wells.demo.netty.groupchat;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * Description 
 * Created by wells on 2020-05-17 09:24:36
 */

public class MyServerHandler extends SimpleChannelInboundHandler<String> {
    // 定义一个 channle 组，管理所有的 channel, GlobalEventExecutor.INSTANCE) 是全局的事件执行器，是一个单例
    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        channelGroup.add(ctx.channel());
        channelGroup.writeAndFlush("the client " + ctx.channel().remoteAddress() + " is online");
        System.out.println("handlerAdded called");
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        channelGroup.remove(ctx.channel());
        channelGroup.writeAndFlush("the client " + ctx.channel().remoteAddress() + " is offline");
        System.out.println("handlerRemoved called");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 客户端上线
        System.out.println("the client " + ctx.channel().remoteAddress() + " is online");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // 客户端下线
        System.out.println("the client " + ctx.channel().remoteAddress() + " is offline");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println("the server receive msg:" + msg);
        for (Channel channel : channelGroup) {
            if (channel != ctx.channel()) {
                // 转发消息到其他的客户端
                ctx.writeAndFlush("hello everyone, my name is " + ctx.channel().remoteAddress() + ", from server msg:" + msg);
            } else {
                // 回显消息
                ctx.writeAndFlush("the server reply, from server msg:" + msg);
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
