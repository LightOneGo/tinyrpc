package rpc.client;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import rpc.common.RpcDecoder;
import rpc.common.RpcEncoder;
import rpc.common.RpcRequest;
import rpc.common.RpcResponse;

public class RpcClientInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast(new LengthFieldBasedFrameDecoder(65536, 0, 4, 0, 0));
        pipeline.addLast(new RpcEncoder(RpcRequest.class));
        pipeline.addLast(new RpcDecoder(RpcResponse.class));
        pipeline.addLast(new RpcClientHandler());
    }
}