package rpc.server;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ServerBoot {
    public static void main(String[] args) {
        new ClassPathXmlApplicationContext("spring.xml");
    }
}
