/**
 * Zentech-Inc
 * Copyright (C) 2017 All Rights Reserved.
 */
package rpc.client;

import rpc.client.proxy.RpcProxy;

import java.lang.reflect.Proxy;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author guangxg
 * @version $Id RpcClient.java, v 0.1 2017-09-06 19:29 guangxg Exp $$
 */
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

    public static void submit(Runnable task){
        threadPoolExecutor.submit(task);
    }

    public static void stop() {
        threadPoolExecutor.shutdown();
    }
}