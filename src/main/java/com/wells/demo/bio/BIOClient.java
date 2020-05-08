package com.wells.demo.bio;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

/**
 * Description 
 * Created by wells on 2020-05-08 10:12:05
 */

public class BIOClient {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress("localhost", 9999));
        OutputStream outputStream = null;
        while (true) {
            Scanner scanner = new Scanner(System.in);
            String line = scanner.nextLine();
            outputStream = socket.getOutputStream();
            outputStream.write(line.getBytes());
        }
    }
}
