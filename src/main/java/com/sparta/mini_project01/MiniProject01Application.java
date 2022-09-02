package com.sparta.mini_project01;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableJpaAuditing
@SpringBootApplication
public class MiniProject01Application {

    public static void main(String[] args) {
        SpringApplication.run(MiniProject01Application.class, args);
    }

}
