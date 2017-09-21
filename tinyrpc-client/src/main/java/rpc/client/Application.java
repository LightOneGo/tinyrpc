/**
 * Zentech-Inc
 * Copyright (C) 2017 All Rights Reserved.
 */
package rpc.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

/**
 * @author guangxg
 * @version $Id Application.java, v 0.1 2017-09-19 20:11 guangxg Exp $$
 */
@SpringBootApplication
@EnableAutoConfiguration
@ImportResource({"classpath:spring.xml"})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}