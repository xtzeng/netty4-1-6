package com.xmqbeast.netty.server;
import java.nio.charset.Charset;

import com.xmqbeast.netty.server.handler.EchoServerHandler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * 使用Netty实现一个Socket通讯，包括客户端和服务端，通过服务端进行监听，客户端发送信息，
 * 服务端可进行接收，并进行返回数据，完成一个完整的通讯。
 * Netty是由JBOSS提供的一个java开源框架。Netty提供异步的、事件驱动的网络应用程序框架和工具，
 * 用以快速开发高性能、高可靠性的网络服务器和客户端程序。
 * 也就是说，Netty 是一个基于NIO的客户、服务器端编程框架，
 * 使用Netty 可以确保你快速和简单的开发出一个网络应用，
 * 例如实现了某种协议的客户，服务端应用。Netty相当简化和流线化了网络应用的编程开发过程，
 * 例如，TCP和UDP的socket服务开发。
 * “快速”和“简单”并不用产生维护性或性能上的问题。
 * Netty 是一个吸收了多种协议的实现经验，这些协议包括FTP,SMTP,HTTP，
 * 各种二进制，文本协议，并经过相当精心设计的项目，最终，Netty 成功的找到了一种方式，
 * 在保证易于开发的同时还保证了其应用的性能，稳定性和伸缩性
 *
 */
public class EchoServer {
    private final int port;

    public EchoServer(int port) {
        this.port = port;
    }

    public void start() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();

        EventLoopGroup group = new NioEventLoopGroup();
        try {
            ServerBootstrap sb = new ServerBootstrap();
            sb.option(ChannelOption.SO_BACKLOG, 1024);
            sb.group(group, bossGroup) // 绑定线程池
                    .channel(NioServerSocketChannel.class) // 指定使用的channel
                    .localAddress(this.port)// 绑定监听端口
                    .childHandler(new ChannelInitializer<SocketChannel>() { // 绑定客户端连接时候触发操作

                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            System.out.println("报告");
                            System.out.println("信息：有一客户端链接到本服务端");
                            System.out.println("IP:" + ch.localAddress().getHostName());
                            System.out.println("Port:" + ch.localAddress().getPort());
                            System.out.println("报告完毕");

                            ch.pipeline().addLast(new StringEncoder(Charset.forName("GBK")));
                            ch.pipeline().addLast(new EchoServerHandler()); // 客户端触发操作
                            ch.pipeline().addLast(new ByteArrayEncoder());
                        }
                    });
            ChannelFuture cf = sb.bind().sync(); // 服务器异步创建绑定
            System.out.println(EchoServer.class + " 启动正在监听： " + cf.channel().localAddress());
            cf.channel().closeFuture().sync(); // 关闭服务器通道
        } finally {
            group.shutdownGracefully().sync(); // 释放线程池资源
            bossGroup.shutdownGracefully().sync();
        }
    }

    public static void main(String[] args) throws Exception {

        new EchoServer(8888).start(); // 启动
    }
}
