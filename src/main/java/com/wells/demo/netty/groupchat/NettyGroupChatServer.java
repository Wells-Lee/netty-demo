package com.wells.demo.netty.groupchat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.Map;

/**
 * Description Netty实现的群聊系统
 * 实例要求:
 * 编写一个 Netty 群聊系统，实现服务器端和客户端之间的数据简单通讯(非阻塞)
 * 实现多人群聊
 * 服务器端:可以监测用户上线，离线，并实现消息转发功能
 * 客户端:通过channel可以无阻塞发送消息给其它所有用户，同时可以接受其它用户发送的消息(有服务器转发得到)
 * Created by wells on 2020-05-17 09:17:05
 */

public class NettyGroupChatServer {
    private int port;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workGroup;
    private ServerBootstrap serverBootstrap;

    public NettyGroupChatServer(int port){
        this.port = port;
        bossGroup = new NioEventLoopGroup(1);
        workGroup = new NioEventLoopGroup();
        serverBootstrap = new ServerBootstrap();
    }

    public void run(){
        try {
            serverBootstrap.group(bossGroup, workGroup)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .channel(NioServerSocketChannel.class)
                    .handler(new ChannelInboundHandlerAdapter(){
                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                            System.out.println("server bossGroup pipeline addr:" + ctx.pipeline().hashCode());
                        }
                    })
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast("decoder", new StringDecoder());
                            pipeline.addLast("encoder", new StringEncoder());
                            pipeline.addLast("myServerHandler", new MyServerHandler());
                            for (Map.Entry<String, ChannelHandler> entry : pipeline) {
                                System.out.println("server workGroup pipline addr:" + pipeline.hashCode() + ", handler:" + entry.getKey() + "-" + entry.getValue().hashCode());
                            }
                        }
                    });

            ChannelFuture channelFuture = serverBootstrap.bind("localhost", this.port).sync();

            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        new NettyGroupChatServer(9999).run();
    }
}
