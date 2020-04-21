package com.wells.demo.nio;


import java.nio.ByteBuffer;

/**
 * Description
 * 1、分配指定大小的缓冲区
 *  通过 allocate
 *
 * 2、读写数据
 *  put(): 存出数据
 *  get(): 取数据
 *
 * 3、flip: 切换到读模式
 * 4、clear: 使各个位置回到初始状态，数据不会丢失
 * 5、mark标记position的位置, 可以通过 reset 回到mark位置
 * 6、直接缓冲区与非直接缓冲区(前者的效率会比后者效率高)
 *  直接缓冲区使用的是服务器的物理内存
 *  非直接缓冲区使用的是JVM的heap内存
 *
 * Created by wells on 2020-04-19 18:59:08
 */

public class TestBuffer {
    public static void main(String[] args) {
        TestBuffer testBuffer = new TestBuffer();
        testBuffer.testByteBuffer();
//        testBuffer.testMark();
    }

    public void testMark(){
        ByteBuffer buf = ByteBuffer.allocate(1024);
        buf.put("abcded".getBytes());

        // 切换到读模式
        buf.flip();
        byte[] dst = new byte[2];
        buf.get(dst, 0 , 2);
        System.out.println(new String(dst));
        System.out.println("position:" + buf.position());

        // 使用mark标记当前position
        buf.mark();
        byte[] dst2 = new byte[2];
        buf.get(dst2, 0 , 2);
        System.out.println(new String(dst2));
        System.out.println("position:" + buf.position());

        // 使用rest可以回到position
        buf.reset();
        System.out.println(buf.position());
    }

    public void testByteBuffer(){
        // 1、分配一个指定大小的缓冲区
        ByteBuffer buf = ByteBuffer.allocate(1024);

        System.out.println("-------------- create bytebuffer --------------");
        System.out.println("capacity:" + buf.capacity());
        System.out.println("limit:" + buf.limit());
        System.out.println("position:" + buf.position());

        // 2、使用put()将数据存入缓冲区
        String str = "abcdef";
        buf.put(str.getBytes());
        System.out.println("-------------- put --------------");
        System.out.println("capacity:" + buf.capacity());
        System.out.println("limit:" + buf.limit());
        System.out.println("position:" + buf.position());

        // 3、切换成都数据模式: 因为 NIO 中读写公用一个通道
        buf.flip();
        System.out.println("-------------- flip --------------");
        System.out.println("capacity:" + buf.capacity());
        System.out.println("limit:" + buf.limit());
        System.out.println("position:" + buf.position());

        // 4、使用get()读取缓冲区的数据
        byte[] dst = new byte[buf.limit()];
        buf.get(dst);
        System.out.println("-------------- get --------------");
        System.out.println("capacity:" + buf.capacity());
        System.out.println("limit:" + buf.limit());
        System.out.println("position:" + buf.position());

        // 5、rewind(): 将 position 设置为0, 可重复读数据
        buf.rewind();
        System.out.println("-------------- rewind --------------");
        System.out.println("capacity:" + buf.capacity());
        System.out.println("limit:" + buf.limit());
        System.out.println("position:" + buf.position());

        // 6、清空缓冲区: 但是缓冲区的数据还存在的, 但是出于"被遗忘"状态(即: capacity、position、limit回到了初始状态)
        buf.clear();
        System.out.println("-------------- clear --------------");
        System.out.println("capacity:" + buf.capacity());
        System.out.println("limit:" + buf.limit());
        System.out.println("position:" + buf.position());
    }
}
