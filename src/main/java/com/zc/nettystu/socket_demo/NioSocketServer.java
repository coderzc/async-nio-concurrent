package com.zc.nettystu.socket_demo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

/**
 * @author: zhaocong
 */
public class NioSocketServer {

    private static ByteBuffer writeBuffer = ByteBuffer.allocateDirect(1024);
    private static ByteBuffer readBuffer = ByteBuffer.allocateDirect(5);
    private static Selector selector;
    private static StringBuffer message = new StringBuffer();

    public static void main(String[] args) throws IOException {
        // socket()、bind()、listen()
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        // 把serverSocketChannel 变成非阻塞模式（accept非阻塞）
        serverSocketChannel.configureBlocking(false);
        ServerSocket serverSocket = serverSocketChannel.socket();
        serverSocket.bind(new InetSocketAddress(8888));
        System.out.println("listening on port 8888");

        // 创建多路io复用器【select/poll/epoll】
        // 相当于 epoll_create() 创建epoll的根结点 selector
        selector = Selector.open();

        // 相当于 epoll_ctl(,EPOLL_CTL_ADD,)  把serverSocket注册到selector这个多路复用器上（上epoll树），检测类型为accept事件
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        // 相当于 epoll_wait(,,,-1)
        while (selector.select(0L) > 0) {//0永远阻塞
            // 遍历io就绪队列
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            System.out.println("selector is activity，keySize："+selectionKeys.size());
            for (SelectionKey selectionKey : selectionKeys) {
                // isAcceptable() 返回true 代表该channel是accepted的serverSocketChannel
                if (selectionKey.isAcceptable()) {
                    System.out.println("isAcceptable");
                    ServerSocketChannel serverChannel = (ServerSocketChannel) selectionKey.channel();
                    SocketChannel socketChannel = serverChannel.accept();

                    // 防止客户端传来RST，socket被移除就绪队列,判断一下（之前已经把serverSocketChannel设置为非阻塞，所有就绪队列没有会立刻返回null）
                    if (socketChannel == null) {
                        continue;
                    }
                    // 把socketChannel变成非阻塞模式（读、写非阻塞）
                    socketChannel.configureBlocking(false);
                    // 把socketChannel注册到多路复用器上（上epoll树）
                    socketChannel.register(selector, SelectionKey.OP_READ);

                    // 打印客户端ip：port
                    String ip = socketChannel.socket().getInetAddress().getHostAddress();
                    int port = socketChannel.socket().getPort();
                    String format = String.format("hi new client ip:%s,port:%s\n", ip, port);
                    System.out.println(format);

                    // 像客户端输出hi，port
                    writeBuffer.clear();
                    writeBuffer.put("hi～，".concat(String.valueOf(port)).concat("\n").getBytes());
                    writeBuffer.flip();
                    socketChannel.write(writeBuffer);

                    // 监听键盘输出，并传给客户端
//                    new Thread(() -> {
//                        // 像客户端输出相应信息
//                        Scanner scanner =new Scanner(System.in);
//                        while (scanner.hasNext()){
//                            String line = scanner.nextLine() + "\n";
//                            writeBuffer.clear();
//                            writeBuffer.put(line.getBytes());
//                            writeBuffer.flip();
//                            try {
//                                socketChannel.write(writeBuffer);
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                        }
//
//                    }).start();

                }
                // isReadable() 返回true 代表channel是readable的socketChannel
                else if (selectionKey.isReadable()) {
                    System.out.println("isReadable");
                    SocketChannel socketChannel = (SocketChannel) selectionKey.channel();

                    // 读取channel中数据
                    readBuffer.clear();
                    int read = 0;
                    while ((read = socketChannel.read(readBuffer)) > 0){// 循环读取缓冲区数据，直到把缓冲区读空，防止频繁调用select()，要配合非阻塞channel才会发挥最大性能
                        readBuffer.flip();

                        String chunk = Charset.forName("UTF-8").decode(readBuffer).toString();
                        message.append(chunk);

                        // 检测消息行尾
                        if(message.indexOf("\n") >= 0){
                            System.out.println("receiveData:" + message);
                            // 清除客户端消息行缓存
                            message.setLength(0);
                        }
                        readBuffer.clear();
                    }

                    // read == -1 代表客户端已经断开
                    if (read == -1) {
                        System.out.println("disconnect a client..");
                        // 反注册channel
                        selectionKey.cancel();
                        // 关闭socket
                        socketChannel.close();
                    }


                }

            }

            // 清除处理过的事件,防止下次循环时重复处理
            // 感觉想java的bug，你每次执行select() 时候java从调用内核函数epoll_wait()获得到最近的就绪队列后直接addAll到selectionKeys，不会帮你清空selectionKeys
            selectionKeys.clear();
        }
    }
}
