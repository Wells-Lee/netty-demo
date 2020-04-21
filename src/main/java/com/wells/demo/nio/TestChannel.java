package com.wells.demo.nio;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
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
 *  直接缓冲区复制比非直接缓冲区快得多的多
 *
 *  四、通道之间的数据传输
 *      transferTo
 *      transferFrom
 *  五、分散与聚集
 *      分散读取(Scattering Reads): 将通道中的数据分散到多个缓冲区
 *      聚集写入(Gathering Writes): 将多个缓冲区的数据写入到通道
 *
 *  六、字符集: CharSet
 *      编码: 字符串 -> 字节数组
 *      解码: 字节数组 -> 字符串
 *
 * Created by wells on 2020-04-20 06:35:01
 */

public class TestChannel {
    public static void main(String[] args) throws Exception {
        TestChannel testChannel = new TestChannel();
//        testChannel.testAllocate();
//        testChannel.testDirectAllocate();
//        testChannel.testChannelOperationData();
//        testChannel.testScterAndGather();
        testChannel.testCharset();

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

        Charset charset = Charset.forName("UTF-8");

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
        FileChannel inChannel = FileChannel.open(Paths.get("/Users/wells/Pictures/1.png"), StandardOpenOption.READ);
        FileChannel outChannel = FileChannel.open(Paths.get("/Users/wells/Pictures/4.png"), StandardOpenOption.WRITE,
                StandardOpenOption.READ, StandardOpenOption.CREATE);

        inChannel.transferTo(0, inChannel.size(), outChannel);
//        outChannel.transferFrom(inChannel, 0, inChannel.size());

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
    public void testDirectAllocate() throws IOException {
        FileChannel inChannel = FileChannel.open(Paths.get("/Users/wells/Pictures/1.png"), StandardOpenOption.READ);
        FileChannel outChannel = FileChannel.open(Paths.get("/Users/wells/Pictures/4.png"), StandardOpenOption.WRITE, StandardOpenOption.READ, StandardOpenOption.CREATE_NEW);

        // 内存映射文件
        MappedByteBuffer inMappedBuf = inChannel.map(FileChannel.MapMode.READ_ONLY, 0, inChannel.size());
        MappedByteBuffer outMappedBuf = outChannel.map(FileChannel.MapMode.READ_WRITE, 0, inChannel.size());

        // 直接对缓冲区的数据进行读写
        byte[] dst = new byte[inMappedBuf.capacity()];
        inMappedBuf.get(dst);
        outMappedBuf.put(dst);

        inChannel.close();
        outChannel.close();
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
        FileInputStream fis = new FileInputStream("/Users/wells/Pictures/1.png");
        FileOutputStream fos = new FileOutputStream("/Users/wells/Pictures/3.png");

        // 1、创建通道
        FileChannel inChannel = fis.getChannel();
        FileChannel outChannel = fos.getChannel();

        // 2、分配指定大小的缓冲区
        ByteBuffer dst = ByteBuffer.allocate(1024);
        while (inChannel.read(dst) != -1) {
            // 3、切换读数据模式
            dst.flip();

            // 4、将缓冲区的数据写入通道中
            outChannel.write(dst);

            // 5、清空缓冲区
            dst.clear();
        }

        outChannel.close();
        inChannel.close();
        fis.close();
        fos.close();
    }
}
