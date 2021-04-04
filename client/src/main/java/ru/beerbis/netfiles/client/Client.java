package ru.beerbis.netfiles.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.json.JsonObjectDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Client {
    private final static Logger logger = LogManager.getLogger(Client.class);
    private final Bootstrap bootstrap = new Bootstrap();
    private String description = "uninitialized";

    public ChannelFuture open(EventLoopGroup workers, String host, Integer port) {
        description = String.format("\"%s:%d\"", host, port);
        bootstrap.group(workers)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(new JsonObjectDecoder(), new StringEncoder(), new StringDecoder(),
                                new SimpleChannelInboundHandler<String>() {

                                    @Override
                                    public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                        logger.info("{}, New active channel", description);
                                    }

                                    @Override
                                    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
                                        logger.info("{}, msg: {}", description, msg);
                                    }

                                    @Override
                                    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                        logger.error(description, cause);
                                        ctx.close();
                                    }

                                    @Override
                                    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                                        logger.info("{}, client disconnect", description);
                                    }
                                });
                    }
                });

        var connectionFuture = bootstrap.connect(host, port).addListener(f -> logger.info("{}, connected", description));
        connectionFuture.channel().closeFuture().addListener(t -> logger.info("{}, disconnected", description));
        return connectionFuture;
    }

}
