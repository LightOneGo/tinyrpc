/**
 * Zentech-Inc
 * Copyright (C) 2017 All Rights Reserved.
 */
package rpc.client;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import rpc.common.RpcDecoder;
import rpc.common.RpcEncoder;
import rpc.common.RpcRequest;
import rpc.common.RpcResponse;


/**
 * @author guangxg
 * @version $Id RpcClientInitializer.java, v 0.1 2017-09-06 19:30 guangxg Exp $$
 */
public class RpcClientInitializer extends ChannelInitializer<SocketChannel> {

    private RpcClientHandler rpcClientHandler;

    public RpcClientInitializer(RpcClientHandler rpcClientHandler) {
        this.rpcClientHandler = rpcClientHandler;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast(new LengthFieldBasedFrameDecoder(65536, 0, 4, 0, 0));
        pipeline.addLast(new RpcEncoder(RpcRequest.class));
        pipeline.addLast(new RpcDecoder(RpcResponse.class));
        pipeline.addLast(rpcClientHandler);
    }
}