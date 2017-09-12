package rpc.client;

import com.google.common.reflect.Reflection;
import rpc.client.connect.RpcConnect;
import rpc.client.proxy.ClientProxy;
import rpc.client.proxy.RpcProxy;

import java.lang.reflect.Proxy;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class RpcClient {
    private String serverAddress;
    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(16, 16, 600L, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(65536));

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

    public  <T> T execute(Class<T> rpcInterface) throws Exception {
        return (T) Reflection.newProxy(rpcInterface, new ClientProxy<T>(serverAddress));
    }

    public static void submit(Runnable task){
        threadPoolExecutor.submit(task);
    }

    public static void stop() {
        threadPoolExecutor.shutdown();
        RpcConnect.getInstance().stop();
    }
}