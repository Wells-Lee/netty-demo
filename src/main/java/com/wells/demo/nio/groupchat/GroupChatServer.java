package com.wells.demo.nio.groupchat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 * Description 
 * Created by wells on 2020-05-10 20:47:53
 */

public class GroupChatServer {
    ServerSocketChannel serverSocketChannel = null;
    Selector selector = null;

    /**
     * @desc 初始化动作
     * @method GroupChatServer
     * @param
     * @return
     * @date 2020-05-10 20:52:10
     * @author wells
     */
    public GroupChatServer() {
        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress("localhost", 9090));
            serverSocketChannel.configureBlocking(false);

            selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void listen() {
        try {
            while (true) {
                int select = selector.select(1000);
                if (select > 0) {
                    // 从 selector 获取有连接的 channel
                    Set<SelectionKey> selectionKeys = selector.selectedKeys();
                    Iterator<SelectionKey> iterator = selectionKeys.iterator();

                    while (iterator.hasNext()) {
                        SelectionKey key = iterator.next();

                        if (key.isAcceptable()) {
                            SocketChannel socketChannel = serverSocketChannel.accept();
                            System.out.println("client " + socketChannel.getRemoteAddress() + " online...");

                            socketChannel.configureBlocking(false);
                            socketChannel.register(selector, SelectionKey.OP_READ);
                        }

                        if (key.isReadable()) {
                            // 读取数据
                            readData(key);
                        }

                        iterator.remove();
                    }
                } else {
                    System.out.println("wait client connect...");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (serverSocketChannel != null) {
                try {
                    serverSocketChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (selector != null) {
                try {
                    selector.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * @desc 读取客户端数据
     * @method readData
     * @param key
     * @return void
     * @date 2020-05-10 21:14:30
     * @author wells
     */
    private void readData(SelectionKey key) throws IOException {
        SocketChannel socketChannel = null;

        try {
            socketChannel = (SocketChannel) key.channel();

            // 读取数据
            ByteBuffer buffer = ByteBuffer.allocate(1024);

            socketChannel.read(buffer);
            String msg = new String(buffer.array());
            System.out.println("from client:" + socketChannel.getRemoteAddress() + " msg:" + msg);
            buffer.clear();

            // 发送内容到其他的 Client
            sendDataToOtherClient(socketChannel, msg);
        } catch (IOException e) {
            System.out.println("client " + socketChannel.getRemoteAddress() + " offline");
            throw e;
        } finally {
            if (socketChannel != null) {
                socketChannel.close();
            }
        }
    }

    private void sendDataToOtherClient(SocketChannel selfChannel, String msg) throws IOException {
        // 遍历所有注册到 selector 上的 channel，排除自己然后发送
        Set<SelectionKey> keys = selector.keys();
        for (SelectionKey key : keys) {
            Channel targetChannel = key.channel();

            if (targetChannel instanceof SocketChannel && targetChannel != selfChannel) {
                ByteBuffer byteBuffer = ByteBuffer.wrap(msg.getBytes());
                SocketChannel dest = (SocketChannel) targetChannel;
                dest.write(byteBuffer);
            }
        }
    }

    public static void main(String[] args) {
        GroupChatServer groupChatServer = new GroupChatServer();
        groupChatServer.listen();
    }
}
