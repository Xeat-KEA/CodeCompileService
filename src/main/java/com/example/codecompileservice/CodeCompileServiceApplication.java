package com.example.codecompileservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class CodeCompileServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CodeCompileServiceApplication.class, args);
    }

}
