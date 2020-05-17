package com.wells.demo.netty.tcp.question;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Description 
 * Created by wells on 2020-05-13 09:50:08
 */

public class NettyClient {
    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup clientWorkGroup = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(clientWorkGroup)
                    // 使用 NioSocketChannel 作为客户端通道的实现
                    .channel(NioSocketChannel.class)
                    .handler(new NettyClientHandler());

            // 启动客户端并连接端口
            ChannelFuture channelFuture = bootstrap.connect("localhost", 9999);

            System.out.println("client is ready");

            // 对关闭通道进行监听
            channelFuture.channel().closeFuture().sync();
        } finally {
            clientWorkGroup.shutdownGracefully();
        }
    }
}
