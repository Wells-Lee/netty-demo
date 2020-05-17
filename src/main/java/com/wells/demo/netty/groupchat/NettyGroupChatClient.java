package com.wells.demo.netty.groupchat;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.Map;
import java.util.Scanner;

/**
 * Description 
 * Created by wells on 2020-05-17 09:42:06
 */

public class NettyGroupChatClient {

    private int port;
    private EventLoopGroup workGroup;
    private Bootstrap bootstrap;

    public NettyGroupChatClient(int port){
        this.port = port;
        workGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
    }

    public void run(){
        try {
            bootstrap.group(workGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast("encoder", new StringEncoder());
                            pipeline.addLast("decoder", new StringDecoder());
                            pipeline.addLast("myClientHandler", new MyClientHandler());
                            for (Map.Entry<String, ChannelHandler> entry : pipeline) {
                                System.out.println("client pipline addr:" + pipeline.hashCode() + ", handler:" + entry.getKey() + "-" + entry.getValue().hashCode());
                            }
                        }
                    });

            ChannelFuture channelFuture = bootstrap.connect("localhost", 9999).sync();

            // 从控制台获取输入并发送
            Channel channel = channelFuture.channel();
            Scanner scanner = new Scanner(System.in);
            while(scanner.hasNextLine()){
                channel.writeAndFlush(scanner.nextLine());
            }

            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            workGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        new NettyGroupChatClient(9999).run();
    }
}
