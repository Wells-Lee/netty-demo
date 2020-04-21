package com.wells.demo.nio.blocking;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * Description socketChannel.shutdownOutput()
 * Created by wells on 2020-04-22 07:07:37
 */

public class TestBlockingClientCallBack {
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

        // 非常重要，因为服务端不知道客户端有没有发送完内容，因此不会给到反馈
        socketChannel.shutdownOutput();

        // 4、接受服务端反馈
        int len = 0;
        ByteBuffer buf = ByteBuffer.allocate(1024);
        while ((len = socketChannel.read(buf)) != -1) {
            buf.flip();
            System.out.println(new String(buf.array(), 0, len));
            buf.clear();
        }

        // 5、关闭通道
        fileChannel.close();
        socketChannel.close();
    }
}
