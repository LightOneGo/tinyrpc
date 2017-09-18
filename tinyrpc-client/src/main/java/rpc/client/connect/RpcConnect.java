package rpc.client.connect;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc.client.RpcClientHandler;
import rpc.client.RpcClientInitializer;

import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;

public class RpcConnect {
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcConnect.class);
    private volatile static RpcConnect rpcConnect;
    private RpcClientHandler handler;
    private EventLoopGroup eventLoopGroup = new NioEventLoopGroup(4);

    private RpcConnect() {
    }

    public static RpcConnect getInstance() {
        if (rpcConnect == null) {
            synchronized (RpcConnect.class) {
                if (rpcConnect == null) {
                    rpcConnect = new RpcConnect();
                }
            }
        }
        return rpcConnect;
    }

    private void setRpcClientHandler(RpcClientHandler handler) {
        this.handler = handler;
    }

    public RpcClientHandler getHandler(final InetSocketAddress serverAddress) {
        if (handler != null) {
            return handler;
        }
        final CountDownLatch latch = new CountDownLatch(1);
        Bootstrap b = new Bootstrap();
        b.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new RpcClientInitializer());

        ChannelFuture channelFuture = b.connect(serverAddress);
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(final ChannelFuture channelFuture) throws Exception {
                if (channelFuture.isSuccess()) {
                    RpcClientHandler handler = channelFuture.channel().pipeline().get(RpcClientHandler.class);
                    RpcConnect.getInstance().setRpcClientHandler(handler);
                    latch.countDown();
                }
            }
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage());
        }
        return handler;
    }

    public void stop() {
        handler.close();
        eventLoopGroup.shutdownGracefully();
    }
}
