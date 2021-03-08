package com.kotei;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;

@SpringBootApplication
@ServletComponentScan(basePackages = {"com.kotei.*"})
@EnableDiscoveryClient
@EnableFeignClients
@EnableHystrix
public class ComsumerClientApplication {

    public static void main(String[] args) {

        SpringApplication.run(ComsumerClientApplication.class, args);
    }

}
