package com.ecommerce.userservice.security;

import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import io.github.bucket4j.*;
import java.time.Duration;


@Component
public class RateLimiterService {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    public Bucket resolveBucket(String ip, String action) {
        String key=ip+":"+action;
        return buckets.computeIfAbsent(key, k -> {
            Bandwidth limit;
            switch (action) {
                case "resetpwd":
                    limit = Bandwidth.classic(3, Refill.intervally(5, Duration.ofMinutes(5))); // 3 resets per 5 mins
                    break;
                case "login":
                    limit = Bandwidth.classic(5, Refill.intervally(5, Duration.ofMinutes(5)));  // 5 logins per 5 mins
                    break;
                default:
                    limit = Bandwidth.classic(10, Refill.intervally(5, Duration.ofMinutes(1)));
            }
            return Bucket.builder().addLimit(limit).build();
        });
    }
}
