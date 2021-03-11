package com.kotei;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@SpringBootApplication
@ServletComponentScan(basePackages = {"com.kotei.*"})
@EnableDiscoveryClient
@EnableZuulProxy
@RefreshScope
public class ComsumerClientApplication {

    public static void main(String[] args) {

        SpringApplication.run(ComsumerClientApplication.class, args);
    }

}
