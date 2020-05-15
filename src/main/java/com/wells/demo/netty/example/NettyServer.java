package com.wells.demo.netty.example;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Description 
 * Created by wells on 2020-05-13 07:17:49
 */

public class NettyServer {
    public static void main(String[] args) {
        /**
         * 创建 bossGroup 和 workGroup:
         * 1、bossGroup 和 workGroup是两个线程池
         * 2、bossGroup 只处理连接请求，workGroup处理真正的业务逻辑
         * 3、两个都是无限循环
         * 4、bossGroup 和 workGroup 含有的子线程(NioEventLoop)的个数默认为: CPU核数 * 2
         */
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workGroup = new NioEventLoopGroup();

        try {
            // 创建服务端启动对象，配置参数
            ServerBootstrap serverBootstrap = new ServerBootstrap();

            // 设置线程组
            serverBootstrap.group(bossGroup, workGroup)
                    // 使用 NioServerSocketChannel 作为服务器的通道实现
                    .channel(NioServerSocketChannel.class)
                    // 设置线程队列得到的连接个数
                    .option(ChannelOption.SO_BACKLOG, 128)
                    // 设置保持活动连接状态
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            // pipeline 设置处理器
                            ch.pipeline().addLast(new NettyServerHandler());
                        }
                    });

            // 启动服务器并绑定端口
            ChannelFuture channelFuture = serverBootstrap.bind("localhost", 9999);

            channelFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        System.out.println("listener port success");
                    }else {
                        System.out.println("listener port fail");
                    }
                }
            });

            System.out.println("server is ready");

            // 对关闭通道进行监听
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }
}
