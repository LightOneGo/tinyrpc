package rpc.client;

import com.google.common.reflect.Reflection;
import rpc.client.connect.RpcConnect;
import rpc.client.proxy.ClientProxy;
import rpc.client.proxy.RpcProxy;

import java.lang.reflect.Proxy;
public class RpcClient {
    private String serverAddress;

    public RpcClient(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    @SuppressWarnings("unchecked")
    public <T> T create(Class<T> interfaceClass) {
        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                new RpcProxy<T>(serverAddress));
    }

    public  <T> T execute(Class<T> interfaceClass) {
        return (T) Reflection.newProxy(interfaceClass, new ClientProxy<T>(serverAddress));
    }


    public static void stop() {
        RpcConnect.getInstance().stop();
    }
}