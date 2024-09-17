package com.morty.friend.config;

import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redisson配置
 *
 * @author morty
 * @create 2024--10 23:53
 */
@Configuration
@ConfigurationProperties(prefix = "spring.redis")
@Data
public class RedissonConfig {

    private String host;

    private String port;

    @Bean
    public RedissonClient redissonClient() {
        // 1. 创建Redisson配置
        Config config = new Config();
        String redisAddress = String.format("redis://%s:%s", host, port);
        // 单个集群：useSingleServer
        // 多个集群：useClusterServers
        config.useSingleServer().setAddress(redisAddress).setDatabase(3);

        // 2. 创建Redisson实例
        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }

}
