package ru.beerbis.netfiles.client;

import io.netty.channel.Channel;
import io.netty.channel.nio.NioEventLoopGroup;

import java.util.Scanner;

public class ClientApplication {
    final static NioEventLoopGroup GROUP = new NioEventLoopGroup();
    final static Scanner SCANNER = new Scanner(System.in);

    public static void main(String[] args) throws InterruptedException {
        Channel channel = null;
        while (true) {
            if (channel != null && channel.closeFuture().isDone()) channel = null;
            if (channel == null) channel = new Client().open(GROUP, "127.0.0.1", 9999).sync().channel();

            var cmd = SCANNER.nextLine();
            if (cmd.toUpperCase().equals("EXIT")) {
                channel.close().sync();
                GROUP.shutdownGracefully();
                break;
            }

            channel.writeAndFlush(cmd);
        }
    }
}
