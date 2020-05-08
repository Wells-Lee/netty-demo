package com.wells.demo.nio.blocking;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * Description 
 * Created by wells on 2020-04-22 06:55:56
 */

public class TestBlockingServer {
    public static void main(String[] args) throws IOException {
        // 1、获取通道
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        // 2、绑定ip、port
        serverSocketChannel.bind(new InetSocketAddress("127.0.0.1", 9080));

        // 3、获取客户端连接的通道
        SocketChannel socketChannel = serverSocketChannel.accept();

        // 4、分配指定大小的缓冲区
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        // 5、读取socker缓冲区内容，并且写到本地文件
        FileChannel fileChannel = FileChannel.open(Paths.get("/Users/wells/Temp/data/nio/out.png"), StandardOpenOption.WRITE, StandardOpenOption.CREATE);
        while (socketChannel.read(byteBuffer) != -1) {
            byteBuffer.flip();
            fileChannel.write(byteBuffer);
            byteBuffer.clear();
        }

        // 6、关闭通道
        fileChannel.close();
        socketChannel.close();
        serverSocketChannel.close();
    }
}
