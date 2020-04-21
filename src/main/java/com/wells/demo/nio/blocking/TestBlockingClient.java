package com.wells.demo.nio.blocking;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * Description 这里还是阻塞时IO
 * Created by wells on 2020-04-22 06:48:35
 */

public class TestBlockingClient {
    public static void main(String[] args) throws IOException {
        // 1、获取通道
        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 9080));

        // 2、分配指定大小的缓冲区
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        // 3、读取本地文件到通道，并且发送到服务端
        FileChannel fileChannel = FileChannel.open(Paths.get("/Users/wells/Temp/data/nio/in.png"), StandardOpenOption.READ);

        while (fileChannel.read(byteBuffer) != -1) {
            byteBuffer.flip();
            socketChannel.write(byteBuffer);
            byteBuffer.clear();
        }

        // 4、关闭通道
        fileChannel.close();
        socketChannel.close();
    }
}
