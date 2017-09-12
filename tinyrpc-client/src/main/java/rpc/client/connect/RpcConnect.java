package rpc.client.connect;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc.client.RpcClientHandler;
import rpc.client.RpcClientInitializer;

import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class RpcConnect {
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcConnect.class);
    private volatile static RpcConnect rpcConnect;
    private RpcClientHandler handler;
    EventLoopGroup eventLoopGroup = new NioEventLoopGroup(4);

    private ReentrantLock lock = new ReentrantLock();
    private Condition connected = lock.newCondition();

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

    public void connect(final InetSocketAddress serverAddress) {

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
                    signalAvailableHandler();
                } else {
                    EventLoop loop = (EventLoop) eventLoopGroup.schedule(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("RPC server is down,start to reconnecting to: " + serverAddress.getAddress().getHostAddress() + ':' + serverAddress.getPort());
                            connect(serverAddress);
                        }
                    }, 10, TimeUnit.SECONDS);
                }
            }
        });

    }

    private void signalAvailableHandler() {
        lock.lock();
        try {
            connected.signalAll();
        } finally {
            lock.unlock();
        }
    }

    private boolean waitingForHandler() throws InterruptedException {
        lock.lock();
        try {
            connected.await();
            return true;
        } finally {
            lock.unlock();
        }
    }

    public void setRpcClientHandler(RpcClientHandler handler) {
        this.handler = handler;
    }

    public RpcClientHandler getRpcClientHandler() throws InterruptedException {
        RpcClientHandler rpcClientHandler = null;
        try {
            boolean available = waitingForHandler();
            if (available) {
                rpcClientHandler = handler;
            }
        } catch (InterruptedException e) {
            LOGGER.error("Waiting for available connect is interrupted! ", e);
            throw new RuntimeException("Can't connect server!", e);
        }
        return rpcClientHandler;
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
        signalAvailableHandler();
        handler.close();
        eventLoopGroup.shutdownGracefully();
    }
}
