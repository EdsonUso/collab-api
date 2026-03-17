package com.edsonuso.collabapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CollabApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(CollabApiApplication.class, args);
    }

}
