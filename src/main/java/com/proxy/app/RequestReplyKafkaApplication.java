package com.proxy.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = {
        "com.proxy.config",
        "com.proxy.consumer",
        "com.proxy.controller",
        "com.proxy.model"
    })
@SpringBootApplication
public class RequestReplyKafkaApplication {

	public static void main(String[] args) {
		SpringApplication.run(RequestReplyKafkaApplication.class, args);
	}
}
