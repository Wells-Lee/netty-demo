package com.wells.demo.nio.groupchat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Description 
 * Created by wells on 2020-05-10 20:48:03
 */

public class GroupChatClient {
    SocketChannel socketChannel = null;
    Selector selector = null;

    public GroupChatClient() {
        try {
            socketChannel = SocketChannel.open(new InetSocketAddress("localhost", 9090));
            socketChannel.configureBlocking(false);
            selector = Selector.open();
            socketChannel.register(selector, SelectionKey.OP_READ);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMsg(String msg) {
        ByteBuffer buffer = ByteBuffer.wrap(msg.getBytes());
        try {
            socketChannel.write(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readMsg() {
        SocketChannel channel = null;
        try {
            int count = selector.select(1000);
            if (count > 0) {
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();

                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();

                    if (key.isReadable()) {
                        channel = (SocketChannel) key.channel();

                        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

                        channel.read(byteBuffer);
                        System.out.println("from server reply:" + new String(byteBuffer.array()));
                        byteBuffer.clear();
                    }

                    iterator.remove();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (channel != null) {
                try {
                    channel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }


    public static void main(String[] args) {
        GroupChatClient groupChatClient = new GroupChatClient();

        // 启动一个定时器，每隔一段时间读取server的回复
        ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(2);
        scheduledExecutorService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                groupChatClient.readMsg();
            }
        }, 1, 2, TimeUnit.SECONDS);

        while (true) {
            // 在main线程中进行写数据
            Scanner scanner = new Scanner(System.in);
            String content = scanner.nextLine();
            groupChatClient.sendMsg(content);
        }
    }
}
