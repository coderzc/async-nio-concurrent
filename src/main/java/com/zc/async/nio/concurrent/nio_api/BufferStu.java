package com.zc.async.nio.concurrent.nio_api;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.StandardOpenOption;

/**
 * Created by coderzc on 2019-06-20
 */
public class BufferStu {

    public static void main(String[] args) throws IOException {
        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("www.baidu.com",80));


        //DirectByteBuffer (堆外内存)
        ByteBuffer directByteBuffer = ByteBuffer.allocateDirect(1024);

        //HeapByteBuffer (JVM堆内存)
        ByteBuffer heapByteBuffer = ByteBuffer.allocate(1024);

        // MappedByteBuffer (mmap)
        FileChannel fc = FileChannel.open(new File("a.txt").toPath(), StandardOpenOption.CREATE_NEW,StandardOpenOption.WRITE,StandardOpenOption.READ);
        MappedByteBuffer mappedByteBuffer = fc.map(FileChannel.MapMode.READ_WRITE, 0, 1000);

        // transferTo (sendfile)
        // linux2.4以前：直接在内核空间中进行拷贝 ；2.4以后DMA直接从原来的内存buffer中读取
        fc.transferTo(0,fc.size(),socketChannel);


    }
}
