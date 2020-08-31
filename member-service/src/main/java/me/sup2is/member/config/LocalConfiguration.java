package me.sup2is.member.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("local")
@EnableAutoConfiguration(exclude = RedisAutoConfiguration.class)
@Configuration
public class LocalConfiguration {
}
