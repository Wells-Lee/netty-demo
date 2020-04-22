package com.wells.demo.nio.non.blocking;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * Description 
 * Created by wells on 2020-04-22 07:17:24
 */

public class TestNonBlockingClient {
    public static void main(String[] args) throws IOException {
        // 1、获取通道
        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 9080));

        // 2、切换成非阻塞模式
        socketChannel.configureBlocking(false);

        // 2、分配指定大小的缓冲区
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        // 3、字符串发送到服务端
        byteBuffer.put("Hello World".getBytes());
        byteBuffer.flip();
        socketChannel.write(byteBuffer);

        // 4、关闭通道
        socketChannel.close();
    }
}
