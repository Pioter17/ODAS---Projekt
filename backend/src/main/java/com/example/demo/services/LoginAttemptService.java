package com.example.demo.services;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
public class LoginAttemptService {

    private static final int MAX_ATTEMPTS = 6;
    private static final int LOCKOUT_DURATION_DAYS = 7;

    @Autowired
    private HttpServletRequest request;

    private final LoadingCache<String, Integer> loginAttemptsCache;

    public LoginAttemptService() {
        this.loginAttemptsCache = CacheBuilder.newBuilder()
                .expireAfterWrite(LOCKOUT_DURATION_DAYS, TimeUnit.DAYS)
                .build(new CacheLoader<String, Integer>() {
                    @Override
                    public Integer load(String key) {
                        return 0;
                    }
                });
    }

    public void loginFailAttempt(String ipAddress) {
        try {
            int attempts = loginAttemptsCache.get(ipAddress) + 1;
            loginAttemptsCache.put(ipAddress, attempts);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public boolean isBlocked() {
        int attempts = 0;
        try {
            attempts = loginAttemptsCache.get(getClientIp());
            if (attempts > MAX_ATTEMPTS) {
                return true;
            }
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    public String getClientIp() {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isEmpty()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
