package rpc.client.proxy;

import com.google.common.reflect.AbstractInvocationHandler;
import rpc.client.RPCFuture;
import rpc.client.RpcClientHandler;
import rpc.client.connect.RpcConnect;
import rpc.common.RpcRequest;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.UUID;

public class ClientProxy<T> extends AbstractInvocationHandler {

    private String serverAddress;

    public ClientProxy(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public Object handleInvocation(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest request = new RpcRequest();
        request.setRequestId(UUID.randomUUID().toString());
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParameterTypes(method.getParameterTypes());
        request.setParameters(args);

        String[] array = serverAddress.split(":");
        String host = array[0];
        int port = Integer.parseInt(array[1]);
        //RpcConnect.getInstance().connect(new InetSocketAddress(host, port));
        RpcClientHandler handler = RpcConnect.getInstance().getHandler(new InetSocketAddress(host, port));
        RPCFuture future = handler.sendRequest(request);
        return future.get();
    }
}
