package com.victory.blog.security.redis.config;

import org.springframework.beans.factory.annotation.Value;

//@Configuration
public class RedisProperties {

    private String host;

    private Integer port;

    public RedisProperties(
            @Value("${spring.redis.port}") int redisPort,
            @Value("${spring.redis.host}") String redisHost) {
        this.port = redisPort;
        this.host = redisHost;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }
}
