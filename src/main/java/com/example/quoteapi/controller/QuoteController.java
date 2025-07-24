package com.example.quoteapi.controller;

import com.example.quoteapi.service.QuoteService;
import com.example.quoteapi.rateLimiter.RateLimiter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class QuoteController {

    private final QuoteService quoteService;
    private final RateLimiter rateLimiter;

    public QuoteController(QuoteService quoteService, RateLimiter rateLimiter) {
        this.quoteService = quoteService;
        this.rateLimiter = rateLimiter;
    }

    @GetMapping("/quote")
    public ResponseEntity<Object> getQuote(HttpServletRequest request) {
        String ip = request.getRemoteAddr();

        if (!rateLimiter.isAllowed(ip)) {
            long retryAfter = rateLimiter.getRetryAfterSeconds(ip);
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(Map.of("error", "Rate limit exceeded. Try again in " + retryAfter + " seconds."));
        }

        return ResponseEntity.ok(Map.of("quote", quoteService.getRandomQuote()));
    }
}
