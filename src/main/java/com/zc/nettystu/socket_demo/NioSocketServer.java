package com.zc.nettystu.socket_demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

/**
 * @author: zhaocong
 */
public class NioSocketServer {
    private static Selector selector = null;
    private static ServerSocketChannel serverSocketChannel = null;

    private static ByteBuffer writeBuffer = ByteBuffer.allocateDirect(1024);
    private static ByteBuffer readBuffer = ByteBuffer.allocateDirect(1024);
    private static StringBuffer message = new StringBuffer();

    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");

    static {
        // 初始化
        try {
            // socket()
            serverSocketChannel = ServerSocketChannel.open();
            // 把serverSocketChannel 变成非阻塞模式（accept非阻塞）
            serverSocketChannel.configureBlocking(false);
            // bind()、listen()
            serverSocketChannel.bind(new InetSocketAddress(8888));
            System.out.println("listening on port 8888");

            // 创建多路io复用器【select/poll/epoll】
            // 相当于 epoll_create() 创建epoll的根结点 selector
            selector = Selector.open();

            // 相当于 epoll_ctl(,EPOLL_CTL_ADD,)  把serverSocket注册到selector这个多路复用器上（上epoll树），检测类型为accept事件
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) throws IOException {

        // 相当于 epoll_wait(,,,-1)
        while (selector.select(0L) > 0) { //参数：0L永远阻塞 ；返回值: 不会等于0
            // 遍历io就绪队列
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            System.out.println("\033[31;m"+"selector is activity \033[0m，keySize：" + selectionKeys.size());
            for (SelectionKey selectionKey : selectionKeys) {
                // isAcceptable() 返回true 代表该channel是accepted的serverSocketChannel
                if (selectionKey.isAcceptable()) {
                    System.out.println("\033[33;4m"+simpleDateFormat.format(new Date())+"\033[0m  \033[31;4misAcceptable"+"\033[0m");
                    ServerSocketChannel serverChannel = (ServerSocketChannel) selectionKey.channel();
                    SocketChannel socketChannel = serverChannel.accept();

                    // 防止客户端传来RST，socket被移除就绪队列,判断一下（之前已经把serverSocketChannel设置为非阻塞，所有就绪队列没有会立刻返回null）
                    if (socketChannel == null) {
                        continue;
                    }
                    // 把socketChannel变成非阻塞模式（读、写非阻塞）
                    socketChannel.configureBlocking(false);
                    // 把socketChannel注册到多路复用器上（上epoll树）
                    SelectionKey newSocketKey = socketChannel.register(selector, SelectionKey.OP_READ);

                    // 打印客户端ip：port
                    String ip = socketChannel.socket().getInetAddress().getHostAddress();
                    int port = socketChannel.socket().getPort();
                    String format = String.format("hi new client ip:%s,port:%s\n", ip, port);
                    System.out.println(format);

                    // 向客户端输出hi，ip:port
                    // 把读到的数据绑定到key中
                    newSocketKey.attach("hi～，"+format);
                    // 注册写事件
                    newSocketKey.interestOps(newSocketKey.interestOps() | SelectionKey.OP_WRITE);
                }
                // isReadable() 返回true 代表channel是readable的socketChannel
                else if (selectionKey.isReadable()) {
                    System.out.println("\033[33;4m"+simpleDateFormat.format(new Date())+"\033[0m  \033[31;4misReadable\033[0m");
                    handleReceive(selectionKey);

                } else if (selectionKey.isWritable()) {
                    System.out.println("\033[33;4m"+simpleDateFormat.format(new Date())+"\033[0m  \033[31;4misWritable"+"\033[0m");

                    handleSend(selectionKey);
                }

            }

            // 清除处理过的事件,防止下次循环时重复处理
            // 感觉想java的bug，你每次执行select() 时候java从调用内核函数epoll_wait()获得到最近的就绪队列后直接addAll到selectionKeys，不会帮你清空selectionKeys
            selectionKeys.clear();
        }
    }


    private static void handleReceive(SelectionKey selectionKey) throws IOException {
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();

        // 读取channel中数据
        readBuffer.clear();

        int read = 0;
        try {
            while ((read = socketChannel.read(readBuffer)) > 0) {// 循环读取缓冲区数据，直到把缓冲区读空，防止频繁调用select()，要配合非阻塞channel才会发挥最大性能
//                        System.out.println("readSize：" + read);
                readBuffer.flip();

                String chunk = Charset.forName("UTF-8").decode(readBuffer).toString();
//                        System.out.println("chunk："+chunk);
                message.append(chunk);

                // 检测消息行尾
                if (message.indexOf("\n") >= 0) {
                    System.out.print("receiveData --->" + "\033[36;4m"+message+"\033[0m");

                    // 把读到的数据绑定到key中
                    selectionKey.attach("server message echo:" + "已经收到\n");
                    // 注册写事件
                    selectionKey.interestOps(selectionKey.interestOps() | SelectionKey.OP_WRITE);

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

            //非阻塞模式下，read==0代表当前系统缓冲区已经空了
            if (read == 0) {
                System.out.println("000,接受缓冲区已经读完了---->"+"hasRemaining:" + readBuffer.hasRemaining() +"\n");
            }


        }catch (Exception e){
            System.err.println("读取数据发生异常");

            // 反注册channel
            selectionKey.cancel();
            // 关闭socket
            socketChannel.close();
        }

    }


    private static void handleSend(SelectionKey selectionKey) throws IOException {
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();

        String message = (String) selectionKey.attachment();
        if (message == null) {
            return;
        }
        selectionKey.attach(null);

        writeBuffer.clear();
        writeBuffer.put(message.getBytes(Charset.forName("UTF-8")));
        writeBuffer.flip();
        while (writeBuffer.hasRemaining()) {
            socketChannel.write(writeBuffer);
        }


        System.out.println("写出 ---> "+message);
        // 取消读事件,防止isWritable一直激活，cpu空转
        selectionKey.interestOps(selectionKey.interestOps() & ~SelectionKey.OP_WRITE);
    }


//    private static void listenUserInput() {
//        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
//        try {
//            String msg;
//            while ((msg = bufferedReader.readLine()) != null) {
//                synchronized (sendBuffer) {
//                    sendBuffer.put((msg + "\n").getBytes());
//
//
////                    System.out.println( (key.interestOps() & SelectionKey.OP_WRITE) == SelectionKey.OP_WRITE);
//
//                }
//
//                selector.wakeup()
//                registerKey.interestOps(registerKey.interestOps() | SelectionKey.OP_WRITE);//添加写事件
//
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }


}
