package ru.beerbis.netfiles.server.listener;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.beerbis.netfiles.server.settings.ListenerSettings;

import javax.annotation.Nonnull;

public class TransferListener implements AutoCloseable {
    private final Logger logger = LogManager.getLogger(TransferListener.class);
    protected final NioEventLoopGroup parentGroup = new NioEventLoopGroup(1);
    protected final NioEventLoopGroup childGroup = new NioEventLoopGroup();
    protected final ServerBootstrap listener = new ServerBootstrap();
    protected ChannelFuture bindingFuture = null;
    private final String name;

    public TransferListener(String name) {
        this.name = name;
        listener.group(parentGroup, childGroup)
                .option(ChannelOption.SO_BACKLOG, 1)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel ch) {
                        ch.pipeline();
                    }
                });
    }

    @Nonnull
    public ChannelFuture open(@Nonnull ListenerSettings settings) {
        logger.info("{} gonna binding on port {}...", name, settings.getPort());
        bindingFuture = listener.bind(settings.getPort())
                .addListener(onBindFinished(settings.getPort()));

        bindingFuture.channel().closeFuture()
                .addListener(this::onListenerClosed);

        return bindingFuture;
    }

    private GenericFutureListener<? extends Future<? super Void>> onBindFinished(Integer port) {
        return future -> {
            if (future.cause() != null) {
                logger.error(name + " did not bound on port " + port, future.cause());
            } else {
                logger.info("{} bound on port {}", name, port);
            }
        };
    }

    private void onListenerClosed(Future<?> closingFuture) {
        logger.info(name + " closed for incoming");

        logger.info("{} closing incoming registration threads...", name);
        parentGroup.shutdownGracefully();
        logger.info("{} closed incoming registration threads", name);

        logger.info("{} closing incoming processing threads...", name);
        childGroup.shutdownGracefully();
        logger.info("{} closed incoming processing threads", name);
    }

    @Override
    public void close() throws InterruptedException {
        if (bindingFuture == null) {
            logger.info("{} close attempted, noting to close", name);
        } else if (bindingFuture.cause() != null) {
            logger.info("{} close attempted, binding was broken", name);
        } else {
            logger.info("{} gonna closing...", name);
            bindingFuture.channel().close().sync();
        }
    }
}
