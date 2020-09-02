package me.sup2is.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@Profile("!local")
@EnableRedisHttpSession
@Configuration
public class RedisHttpSessionConfiguration {
}
