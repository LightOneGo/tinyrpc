package rpc.client.proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc.client.RPCFuture;
import rpc.client.RpcClientHandler;
import rpc.client.connect.RpcConnect;
import rpc.common.RpcRequest;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.UUID;

public class RpcProxy<T> implements InvocationHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcProxy.class);
    private String serverAddress;

    public RpcProxy(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (Object.class == method.getDeclaringClass()) {
            String name = method.getName();
            if ("equals".equals(name)) {
                return proxy == args[0];
            } else if ("hashCode".equals(name)) {
                return System.identityHashCode(proxy);
            } else if ("toString".equals(name)) {
                return proxy.getClass().getName() + "@" +
                        Integer.toHexString(System.identityHashCode(proxy)) +
                        ", with InvocationHandler " + this;
            } else {
                throw new IllegalStateException(String.valueOf(method));
            }
        }

        RpcRequest request = new RpcRequest();
        request.setRequestId(UUID.randomUUID().toString());
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParameterTypes(method.getParameterTypes());
        request.setParameters(args);

        String[] array = serverAddress.split(":");
        String host = array[0];
        int port = Integer.parseInt(array[1]);
        RpcClientHandler handler = RpcConnect.getInstance().getHandler(new InetSocketAddress(host, port));
        RPCFuture future = handler.sendRequest(request);
        return future.get();
    }

}