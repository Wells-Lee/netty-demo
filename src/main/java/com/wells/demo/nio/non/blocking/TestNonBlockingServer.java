package com.wells.demo.nio.non.blocking;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * Description 
 * Created by wells on 2020-04-22 07:17:36
 */

public class TestNonBlockingServer {
    public static void main(String[] args) throws IOException, InterruptedException {
        // 1、获取服务端通道
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        // 2、修改为非阻塞模式
        serverSocketChannel.configureBlocking(false);

        // 3、绑定ip和端口
        serverSocketChannel.bind(new InetSocketAddress("127.0.0.1", 9080));

        // 4、获取选择器
        Selector selector = Selector.open();

        // 5、将通道注册到选择器上，并且指定 "选择器监听事件"
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        // 6、轮询式获取选择器上已经准备就绪的事件
        while (true) {
            if (selector.select(1000) == 0) {
                System.out.println("no connect after server waited 1 second");
                continue;
            }

            // 7、获取当前选择器所有的监听键
            // selector.selectedKeys(): 表示监听的时候有哪些通道发生了事件, 即同一时间内的发生事件的通道
            // selector.keys(): 表示总共注册了多少通道
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();

                // 8、判断具体是什么事件
                if (key.isAcceptable()) {
                    // 9、如果为 accept 事件，那么获取客户端连接
                    SocketChannel socketChannel = serverSocketChannel.accept();

                    // 10、切换为非阻塞模式
                    socketChannel.configureBlocking(false);

                    System.out.println("client connect success, sockerChannel " + socketChannel.hashCode());

                    // 11、将该通道注册到选择器上，并且制定 "选择器监听事件"
                    socketChannel.register(selector, SelectionKey.OP_READ);
                }
                if (key.isReadable()) {
                    // 12、如果为 read 事件，那么处理读过来的内容
                    SocketChannel socketChannel = (SocketChannel) key.channel();

                    ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

                    int len = 0;
                    while ((len = socketChannel.read(byteBuffer)) != -1) {
                        byteBuffer.flip();
                        System.out.println("from client " + new String(byteBuffer.array(), 0, len));
                        byteBuffer.clear();
                    }
                }
                // 13、监听处理一个选择器key，需要移除一个
                iterator.remove();
            }
        }
    }
}
