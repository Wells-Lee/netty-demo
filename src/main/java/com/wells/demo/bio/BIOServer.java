package com.wells.demo.bio;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Description
 * 使用BIO模型编写一个服务器端，监听6666端口，当有客户端连接时，就启动一个线程与之通讯；
 * 要求使用线程池机制改善，可以连接多个客户端；
 * 服务器端可以接收客户端发送的数据(telnet 方式即可)；
 * Created by wells on 2020-05-07 09:32:02
 */

public class BIOServer {
    public static void main(String[] args) throws IOException {
        // 监听端口
        ServerSocket serverSocket = new ServerSocket();
        serverSocket.bind(new InetSocketAddress("127.0.0.1", 9999));

        // 构建线程池
        ExecutorService executorService = Executors.newCachedThreadPool();

        while (true) {
            System.out.println("wait connect...");
            Socket socket = serverSocket.accept();
            System.out.println("accept connect");
            if (null != socket) {
                executorService.execute(new Handler(socket));
            }
        }
    }
}

class Handler implements Runnable {
    private Socket socket;

    public Handler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        InputStream inputStream = null;
        try {
            inputStream = socket.getInputStream();
            byte[] content = new byte[1024];
            int read = inputStream.read(content);
            while (read != -1) {
                System.out.println(new String(content, 0, 1024));
                read = inputStream.read(content);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
