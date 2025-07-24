package com.example.quoteapi.rateLimiter;

import io.github.bucket4j.*;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimiter {

    private final Map<String, Bucket> ipBucketMap = new ConcurrentHashMap<>();

    public boolean isAllowed(String ip) {
        Bucket bucket = ipBucketMap.computeIfAbsent(ip, this::newBucket);
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
        return probe.isConsumed();
    }

    public long getRetryAfterSeconds(String ip) {
        Bucket bucket = ipBucketMap.get(ip);
        if (bucket == null) return 0;

        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
        return !probe.isConsumed() ? probe.getNanosToWaitForRefill() / 1_000_000_000 : 0;
    }

    private Bucket newBucket(String ip) {
        Refill refill = Refill.greedy(5, Duration.ofMinutes(5));
        Bandwidth limit = Bandwidth.classic(5, refill);
        return Bucket.builder().addLimit(limit).build();
    }
}
