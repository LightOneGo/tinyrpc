package rpc.server.service;

import rpc.facade.Hello;
import rpc.server.RpcService;

@RpcService(Hello.class)
public class HelloImpl implements Hello {
    @Override
    public String sayHello(String name) {
        return "hello " + name;
    }
}
