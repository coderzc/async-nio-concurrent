package com.zc.nettystu.socket_demo;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by coderzc on 2019-06-03
 */
public class BioSocketServer {
    public static void main(String[] args) throws Exception {
//            //获取本机的InetAddress实例
//            InetAddress address = InetAddress.getLocalHost();
//            String hostName = address.getHostName();//获取计算机名
//            String hostAddress = address.getHostAddress();//获取IP地址
//            byte[] bytes = address.getAddress();//获取字节数组形式的IP地址,以点分隔的四部分

        ServerSocket serverSocket = new ServerSocket(9000);

        System.out.println("服务端已启动端口：9000,等待客户端连接..");

        while (true){

            Socket socket = serverSocket.accept(); //侦听并接受到此套接字的连接,返回一个Socket对象

            new Thread(() -> {
                InputStream inputStream = null;
                OutputStream outputStream = null;
                try {
                    inputStream = socket.getInputStream();
                    outputStream = socket.getOutputStream();
                    int readLength = 0;
                    byte[] buffer = new byte[1024];

                    BufferedInputStream bis = new BufferedInputStream(inputStream);
                    BufferedOutputStream bos = new BufferedOutputStream(outputStream);

                    while (BioSocketServer.socketAlive(socket)) {
                        StringBuilder message = new StringBuilder();
                        String messageStr = null;
                        while ((readLength = bis.read(buffer, 0, buffer.length)) != -1) {
                            if(readLength==0){ // 阻塞读readLength不可能为0
                                System.out.println("00000");
                            }
                            String chunk = new String(buffer, 0, readLength, "UTF-8");
                            System.out.println(chunk);
                            message.append(chunk);

                            int i = 0;
                            // 遇到exit 关闭socket连接
                            if(message.indexOf("exit") > -1){
                                socket.close();
                                break;
                            }
                            //遇到两个回车就输出代表这条消息完成
                            if((i = message.indexOf("\r\n\r\n")) > -1){
                                messageStr = message.substring(0,i);
                                break;
                            }
                        }


                        if(socketAlive(socket)){
                            System.out.format("\nfrom socket ip：%s，port:%s ,message：%s\n", socket.getInetAddress().getHostAddress(),socket.getPort(),messageStr);

                            //将调用结果写到sokect的输出流中，以发送给客户端
                            bos.write("hello socket!\n".getBytes());
                            bos.flush();
                        }else {
                            System.out.format("socket is close ip：%s，port:%s", socket.getInetAddress().getHostAddress(),socket.getPort());
                        }


                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (outputStream != null) {
                            outputStream.close();
                        }
                        if (inputStream != null) {
                            inputStream.close();
                        }
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();


        }

    }


    private static boolean socketAlive(Socket socket){
        try{
            socket.sendUrgentData(0xFF);
        }catch(IOException e){
            try {
                socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            return false;
        }
        return true;
    }
}
