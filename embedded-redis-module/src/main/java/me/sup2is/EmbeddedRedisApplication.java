package me.sup2is;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EmbeddedRedisApplication {
    public static void main(String[] args) {
        SpringApplication.run(EmbeddedRedisApplication.class, args);
    }
}
