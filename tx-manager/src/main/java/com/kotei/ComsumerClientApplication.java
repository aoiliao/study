package com.kotei;

import com.codingapi.txlcn.tm.config.EnableTransactionManagerServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableTransactionManagerServer
public class ComsumerClientApplication {

    public static void main(String[] args) {

        SpringApplication.run(ComsumerClientApplication.class, args);
    }

}
