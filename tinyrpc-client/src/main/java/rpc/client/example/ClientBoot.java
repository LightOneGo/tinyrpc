package rpc.client.example;

import rpc.client.RpcClient;
import rpc.facade.Hello;

public class ClientBoot {
    public static void main(String[] args) throws InterruptedException {
        RpcClient client = new RpcClient("127.0.0.1:8000");
        Hello hello = client.create(Hello.class);
        String result = hello.sayHello("simida");
        System.out.print(result);
        client.stop();
    }
}
