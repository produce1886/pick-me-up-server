package com.produce.pickmeup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PickmeupApplication {

    public static void main(String[] args) {
        SpringApplication.run(PickmeupApplication.class, args);
    }

}
