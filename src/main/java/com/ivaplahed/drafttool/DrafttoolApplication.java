package com.ivaplahed.drafttool;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DrafttoolApplication {

    public static void main(String[] args) {
        SpringApplication.run(DrafttoolApplication.class, args);
    }

}
