package com.calculator.config;

import com.google.common.cache.CacheBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class SpringCachingConfig {

    private final Integer percentageTTL;
    public SpringCachingConfig(
            @Value("${caching.spring.percentageTTL}") Integer percentageTTL
    ) {
        this.percentageTTL = percentageTTL;
    }

    @Bean
    @Primary
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("percentage", "lastPercentageReturned");
    }

    @Bean
    public CacheManager percentageCacheManager() {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager() {

            @Override
            protected Cache createConcurrentMapCache(final String name) {
                return new ConcurrentMapCache(name,
                        CacheBuilder.newBuilder().expireAfterWrite(percentageTTL, TimeUnit.SECONDS).maximumSize(100).build().asMap(), false);
            }
        };

        return cacheManager;
    }
}
