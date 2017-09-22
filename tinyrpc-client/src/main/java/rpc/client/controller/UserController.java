package rpc.client.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import rpc.client.RpcClient;
import rpc.facade.Hello;

import javax.annotation.PostConstruct;

@RequestMapping("/user")
@RestController
public class UserController {
    @Autowired
    private RpcClient rpcClient;

    private Hello hello;

    @PostConstruct
    public void init() {
        hello = rpcClient.create(Hello.class);
    }

    @RequestMapping("/sayhello")
    @ResponseBody
    public String sayHello(@RequestParam("name") String name) {
        return hello.sayHello(name);
    }
}