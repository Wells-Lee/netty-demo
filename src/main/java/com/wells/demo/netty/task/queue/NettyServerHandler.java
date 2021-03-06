package com.wells.demo.netty.task.queue;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

import java.util.concurrent.TimeUnit;

/**
 * Description TaskQueue例子
 * Created by wells on 2020-05-13 09:41:05
 */

public class NettyServerHandler extends ChannelInboundHandlerAdapter {
    /**
     * ChannelHandlerContext 上下文对象，其中有：Channel、Pipeline
     */

    // read msg
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel channel = ctx.channel();

        ByteBuf byteBuf = (ByteBuf) msg;
        /**
         * 1、用户自定义普通任务
         */
        channel.eventLoop().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    TimeUnit.SECONDS.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(String.format("from client msg in task, addr:%s, msg:%s", channel.remoteAddress(), byteBuf.toString(CharsetUtil.UTF_8)));
            }
        });

        /**
         * 2、用户自定义定时任务
         */
        channel.eventLoop().schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    TimeUnit.SECONDS.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(String.format("from client msg in schedule task, addr:%s, msg:%s", channel.remoteAddress(), byteBuf.toString(CharsetUtil.UTF_8)));
            }
        }, 2, TimeUnit.SECONDS);
    }

    // 数据读取完成后do something
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        String reply = String.format("reply client, client:%s, server:%s, ", channel.remoteAddress(), channel.localAddress());
        ctx.writeAndFlush(Unpooled.copiedBuffer(reply, CharsetUtil.UTF_8));
    }

    // catch exception
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
