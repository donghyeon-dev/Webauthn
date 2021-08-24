package com.webauthn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class WebauthnApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebauthnApplication.class, args);
    }

}
