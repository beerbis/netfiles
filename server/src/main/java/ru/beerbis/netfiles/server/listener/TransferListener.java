package ru.beerbis.netfiles.server.listener;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import ru.beerbis.netfiles.server.settings.ListenerSettings;

import javax.annotation.Nonnull;

public class TransferListener {
    protected final NioEventLoopGroup parentGroup = new NioEventLoopGroup(1);
    protected final NioEventLoopGroup childGroup = new NioEventLoopGroup();
    protected final ServerBootstrap listener = new ServerBootstrap();

    public TransferListener() {
        listener.group(parentGroup, childGroup)
                .option(ChannelOption.SO_BACKLOG, 1)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline();
                    }
                });
    }

    @Nonnull
    public ChannelFuture open(@Nonnull ListenerSettings settings) {
        return listener.bind(settings.getPort());
    }
}
