package com.wells.demo.nio;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.SortedMap;

/**
 * Description
 * 一、通道(Channel)
 *  用于源节点与目标节点的连接。在 Java NIO 中负责缓冲数据的传输，Channel本身不存储数据，因此需要配合缓冲区进行传输。
 * 二、通道类的主要实现
 *  java.nio.channels.Channel 接口:
 *      | FileChannel
 *      | SocketChannel
 *      | ServerScoketChannel
 *      | DatagramChannel
 * 三、获取通道
 *  1、Java针对支持通道的类提供了 getChannel() 方法
 *      本地IO:
 *          FileInputStream / FileOutputStream
 *          RandomAccessFile
 *      网络IO:
 *          Socket
 *          ServerSocket
 *          DatagramSocket
 *  2、在 JDK1.7 中 NIO.2 针对各个通道提供了静态方法 open()
 *  3、在 JDK1.7 中 NIO.2 的 Files 工具类的 newByteChannel()
 *
 *  四、直接缓冲区与非直接缓冲区
 *      ByteBuffer.allocate: 非直接缓冲区，将缓冲区建立在 JVM 的内存中
 *      ByteBuffer.allocateDirect: 直接缓冲区，将缓冲区建立在物理内存中。可以提高效率
 *      直接缓冲区复制比非直接缓冲区快得多的多，因为使用的是物理机内存
 *
 *  五、使用内存映射文件
 *      MappedByteBuffer
 *
 *  六、通道之间的数据传输使用直接缓冲区，速度比较快
 *      transferTo
 *      transferFrom
 *
 *  七、分散与聚集
 *      分散读取(Scattering Reads): 将通道中的数据分散到多个缓冲区
 *      聚集写入(Gathering Writes): 将多个缓冲区的数据写入到通道
 *
 *  八、字符集: CharSet
 *      编码: 字符串 -> 字节数组
 *      解码: 字节数组 -> 字符串
 *
 *
 * 通过测试发现copy文件耗时: transferTo/transferFrom < MappedByteBuffer < allocateDirect(每次1024,多次复制) < allocate(每次1024,多次复制)
 * 通过测试发现copy文件耗时: transferTo/transferFrom ≈ allocateDirect(一次性申请文件大小的内存) < MappedByteBuffer < allocate(一次性申请文件大小的内存)
 * Created by wells on 2020-04-20 06:35:01
 */

public class TestChannel {
    public static void main(String[] args) throws Exception {
        TestChannel testChannel = new TestChannel();
        testChannel.testAllocate();
        testChannel.testAllocateDirect();
        testChannel.testDirectAllocateByMap();
        testChannel.testChannelOperationData();
//        testChannel.testScterAndGather();
//        testChannel.testCharset();

    }

    /**
     * @desc 编码解码器
     * @method testCharset
     * @param
     * @return void
     * @date 2020-04-20 07:33:36
     * @author wells
     */
    public void testCharset() throws Exception {
        // 1、打印可用的charset
        SortedMap<String, Charset> map = Charset.availableCharsets();
        map.forEach((k, v) -> System.out.println(k + "-" + v));

        Charset charset = StandardCharsets.UTF_8;

        // 2、获取编码器
        CharsetEncoder charsetEncoder = charset.newEncoder();

        CharBuffer charBuffer = CharBuffer.allocate(1024);
        charBuffer.put("编码解码器");
        charBuffer.flip();
        // 3、编码
        ByteBuffer byteBuffer = charsetEncoder.encode(charBuffer);

        // 4、获取解码器
        CharsetDecoder charsetDecoder = charset.newDecoder();

        // 5、解码
        CharBuffer decode = charsetDecoder.decode(byteBuffer);
        System.out.println(decode.toString());
    }

    /**
     * @desc 分散读取和聚集写入
     * 分散读取(Scattering Reads)是指从 Channel 中读取的数据“分 散”到多个 Buffer 中。
     * 聚集写入(Gathering Writes)是指将多个 Buffer 中的数据“聚集” 到 Channel。
     * @method testScterAndGather
     * @param
     * @return void
     * @date 2020-04-20 07:25:54
     * @author wells
     */
    public void testScterAndGather() throws IOException {
        // 1、获取通道
        FileChannel inChannel = FileChannel.open(Paths.get("/Users/wells/Downloads/1.txt"), StandardOpenOption.READ);
        FileChannel outChannel = FileChannel.open(Paths.get("/Users/wells/Downloads/2.txt"), StandardOpenOption.WRITE,
                StandardOpenOption.READ, StandardOpenOption.CREATE);

        // 2、构建分散的 ByteBuffer
        ByteBuffer[] bufs = new ByteBuffer[]{
                ByteBuffer.allocate(1024),
                ByteBuffer.allocate(1024)
        };

        // 3、分散读取
        inChannel.read(bufs);

        // 4、聚集写入
        outChannel.write(bufs);

        inChannel.close();
        outChannel.close();
    }

    /**
     * @desc 通道直接操作数据: 直接缓冲区操作数据
     * @method testChannelOperationData
     * @param
     * @return void
     * @date 2020-04-20 07:07:36
     * @author wells
     */
    public void testChannelOperationData() throws IOException {
        FileChannel inChannel = FileChannel.open(Paths.get("/Users/wells/Downloads/flink-1.10.0-bin-scala_2.11.tgz"), StandardOpenOption.READ);
        FileChannel outChannel = FileChannel.open(Paths.get("/Users/wells/Downloads/flink-1.10.0-bin-scala_2.11.tgz-4"), StandardOpenOption.WRITE,
                StandardOpenOption.READ, StandardOpenOption.CREATE);

        long start = System.currentTimeMillis();
        inChannel.transferTo(0, inChannel.size(), outChannel);
//        outChannel.transferFrom(inChannel, 0, inChannel.size());
        long end = System.currentTimeMillis();
        System.out.println("transferTo cost time:" + (end - start));

        inChannel.close();
        outChannel.close();
    }

    /**
     * @desc 使用直接缓冲区完成文件的复制(内存映射文件)
     * @method testDirectAllocate
     * @param
     * @return void
     * @date 2020-04-20 07:00:42
     * @author wells
     */
    public void testDirectAllocateByMap() throws IOException {
        FileChannel inChannel = FileChannel.open(Paths.get("/Users/wells/Downloads/flink-1.10.0-bin-scala_2.11.tgz"), StandardOpenOption.READ);
        FileChannel outChannel = FileChannel.open(Paths.get("/Users/wells/Downloads/flink-1.10.0-bin-scala_2.11.tgz-3"), StandardOpenOption.WRITE, StandardOpenOption.READ, StandardOpenOption.CREATE_NEW);

        // 内存映射文件
        MappedByteBuffer inMappedBuf = inChannel.map(FileChannel.MapMode.READ_ONLY, 0, inChannel.size());
        MappedByteBuffer outMappedBuf = outChannel.map(FileChannel.MapMode.READ_WRITE, 0, inChannel.size());

        long start = System.currentTimeMillis();
        // 直接对缓冲区的数据进行读写
        byte[] dst = new byte[inMappedBuf.capacity()];
        inMappedBuf.get(dst);
        outMappedBuf.put(dst);

        long end = System.currentTimeMillis();
        System.out.println("test direct allocate by map cost time:" + (end - start));

        inChannel.close();
        outChannel.close();
    }

    /**
     * @desc 使用 Buffer 的allocateDirect
     * @method testAllocateDirect
     * @param
     * @return void
     * @date 2020-05-14 13:51:34
     * @author wells
     */
    public void testAllocateDirect() throws Exception {
        FileInputStream fis = new FileInputStream("/Users/wells/Downloads/flink-1.10.0-bin-scala_2.11.tgz");
        FileOutputStream fos = new FileOutputStream("/Users/wells/Downloads/flink-1.10.0-bin-scala_2.11.tgz-2");

        // 1、创建通道
        FileChannel inChannel = fis.getChannel();
        FileChannel outChannel = fos.getChannel();

        ByteBuffer byteBuffer = ByteBuffer.allocateDirect((int) inChannel.size());
        long start = System.currentTimeMillis();
        while (inChannel.read(byteBuffer) != -1) {
            byteBuffer.flip();
            outChannel.write(byteBuffer);
            byteBuffer.clear();
        }
        long end = System.currentTimeMillis();
        System.out.println("allocate direct cost time:" + (end - start));

        outChannel.close();
        inChannel.close();
        fos.close();
        fis.close();
    }

    /**
     * @desc 利用通道完成文件复制: 非直接缓冲区
     * @method testAllocate
     * @param
     * @return void
     * @date 2020-04-20 06:51:15
     * @author wells
     */
    public void testAllocate() throws Exception {
        FileInputStream fis = new FileInputStream("/Users/wells/Downloads/flink-1.10.0-bin-scala_2.11.tgz");
        FileOutputStream fos = new FileOutputStream("/Users/wells/Downloads/flink-1.10.0-bin-scala_2.11.tgz-1");

        // 1、创建通道
        FileChannel inChannel = fis.getChannel();
        FileChannel outChannel = fos.getChannel();

        long start = System.currentTimeMillis();

        // 2、分配指定大小的缓冲区
        ByteBuffer dst = ByteBuffer.allocate((int) inChannel.size());
        while (inChannel.read(dst) != -1) {
            // 3、切换读数据模式
            dst.flip();

            // 4、将缓冲区的数据写入通道中
            outChannel.write(dst);

            // 5、清空缓冲区
            dst.clear();
        }

        long end = System.currentTimeMillis();
        System.out.println("allocate cost time:" + (end - start));

        outChannel.close();
        inChannel.close();
        fis.close();
        fos.close();
    }
}
