package com.zc.nettystu;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;

/**
 * Created by coderzc on 2019-06-20
 */
public class IOTest {

    private long start;

    private static int bufferSize = 536870912; // 512m

    private static String usrHome = System.getProperty("user.home");


    private static File file = new File(usrHome + "/netty7.zip");

    private static RandomAccessFile raf;

    static {
        try {
            raf = new RandomAccessFile(usrHome + "/netty7.zip", "rw");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Before//在每个测试方法之前运行
    public void Testbegin() {
        start = System.currentTimeMillis();
    }


    @After//在每个方法之后运行
    public void end() {
        long end = System.currentTimeMillis();
        System.out.println("StreamFileReader: " + (end - start) + "ms");
    }


    @Test // 1s 402  InputSteam
    public void testInputSteam() throws IOException {
        InputStream inputStream = new FileInputStream(file);

        byte[] b = new byte[bufferSize];

        int bytes;
        while ((bytes = inputStream.read(b)) != -1) {
            byte[] data = new byte[bytes];
            System.arraycopy(b, 0, data, 0, bytes);
        }

        inputStream.close();

    }

    @Test // 2s23ms  BufferInputSteam
    public void testBufferInputSteam() throws IOException {
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));

        byte[] b = new byte[bufferSize];
        long readSize =0L;

        int bytes;
        while ((bytes = bis.read(b)) != -1) {
            byte[] data = new byte[bytes];
            System.arraycopy(b, 0, data, 0, bytes);
            readSize += bytes;
        }

        bis.close();

        System.out.println("读取总字节数：" + readSize);
    }


    @Test // 3s469ms  FileChannel+HeapByteBuffer
    public void testByteBuffer() throws IOException {
//        FileChannel channel = new FileInputStream(file).getChannel();
        FileChannel channel = FileChannel.open(file.toPath(), StandardOpenOption.READ);

        ByteBuffer buffer = ByteBuffer.allocate(bufferSize);

        int bytes;
        while ((bytes = channel.read(buffer)) != -1) {
            byte[] data = new byte[bytes];
            buffer.flip();
            buffer.get(data);
            buffer.clear();
        }
        channel.close();
    }


    @Test // 1s 193  FileChannel+DirectByteBuffer
    // 3s 374
    public void testByteBufferDirect() throws IOException {
        FileChannel channel = FileChannel.open(file.toPath(), StandardOpenOption.READ);

        ByteBuffer buffer = ByteBuffer.allocateDirect(bufferSize);

        int bytes;
        while ((bytes = channel.read(buffer)) != -1) {
            byte[] data = new byte[bytes];
            buffer.flip();
            buffer.get(data);
            buffer.clear();
        }

        channel.close();
    }

    @Test // 1s 429   FileChannel+ByteBuffer+RandomAccessFile
    public void testByteBufferByRandomAccessFile() throws IOException {

        FileChannel channel = raf.getChannel();

        ByteBuffer buffer = ByteBuffer.allocate(bufferSize);

        int bytes;
        while ((bytes = channel.read(buffer)) != -1) {
            byte[] data = new byte[bytes];
            buffer.flip();
            buffer.get(data);
            buffer.clear();
        }

        channel.close();
    }

    @Test // 1s 509   FileChannel+DirectByteBuffer+RandomAccessFile
    public void testByteBufferDirecByRandomAccessFile() throws IOException {

        FileChannel channel = raf.getChannel();

        ByteBuffer buffer = ByteBuffer.allocateDirect(bufferSize);

        int bytes;
        while ((bytes = channel.read(buffer)) != -1) {
            byte[] data = new byte[bytes];
            buffer.flip();
            buffer.get(data);
            buffer.clear();
        }

        channel.close();
    }

//    @BitOpt
//    public void testReadAllByte() throws IOException {
//        Files.readAllBytes(file.toPath());
//    }


    @Test
    public void testMMAP() throws IOException {
        /**
         * int 最大值 2147483647 --> 2G  ---> 无法申请2G内存   new byte[int]
         *
         * 源码体现
         * } else if (var4 > 2147483647L) {
         *    throw new IllegalArgumentException("Size exceeds Integer.MAX_VALUE");
         * } else {
         */

        FileChannel fc = raf.getChannel();

//        FileChannel fc = FileChannel.open(file.toPath(), StandardOpenOption.READ);

        long fileSize = fc.size();

        // 512m 每次映射的长度
        long length = 1L << 29;


        // 映射文件开始的位置
        long cur = 0L;

        // 读取的总字节数
        long readSize = 0L;

        while (cur < fileSize) {
            MappedByteBuffer mappedByteBuffer = fc.map(FileChannel.MapMode.READ_WRITE, cur, length);
            System.out.println(mappedByteBuffer.getClass().getName());
            byte[] data = new byte[bufferSize];
            long cycle = length / bufferSize;
            long mode = length % bufferSize;
            for (int i = 0; i < cycle; i++) {
                // 每次读取bufferSize个字节
                mappedByteBuffer.get(data);
                readSize += data.length;
            }
            // 如果余数大于0证明还剩点
            if (mode > 0) {
                data = new byte[(int) mode];
                mappedByteBuffer.get(data);
                readSize += data.length;
            }

            cur += length;
            length = Math.min(length, fileSize - cur);
        }

        fc.close();
        System.out.println("读取总字节数：" + readSize);

    }

    @Test
    public void test() {
        System.out.println("123");
    }


}
