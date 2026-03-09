package com.warzone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WarzoneApplication {

    public static void main(String[] args) {
        SpringApplication.run(WarzoneApplication.class, args);
    }
}
