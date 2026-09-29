package com.example.industry_content_generator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class IndustryContentGeneratorApplication {

    public static void main(String[] args) {
        SpringApplication.run(IndustryContentGeneratorApplication.class, args);
    }

}