package com.example.quoteapi.controller;

import com.example.quoteapi.rateLimiter.RateLimiter;
import com.example.quoteapi.service.QuoteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(QuoteController.class)
@Import(QuoteControllerTest.MockConfig.class)
class QuoteControllerTest {

    @TestConfiguration
    static class MockConfig {
        @Bean
        public QuoteService quoteService() {
            return mock(QuoteService.class);
        }

        @Bean
        public RateLimiter rateLimiter() {
            return mock(RateLimiter.class);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private QuoteService quoteService;

    @Autowired
    private RateLimiter rateLimiter;

    private final String testQuote = "Do or do not. There is no try. - Yoda";

    @BeforeEach
    void setUp() {
        when(quoteService.getRandomQuote()).thenReturn(testQuote);
    }

    @Test
    void shouldReturnQuoteWhenWithinRateLimit() throws Exception {
        when(rateLimiter.isAllowed(anyString())).thenReturn(true);

        mockMvc.perform(get("/api/quote"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quote").value(testQuote));
    }

    @Test
    void shouldReturn429WhenRateLimitExceeded() throws Exception {
        when(rateLimiter.isAllowed(anyString())).thenReturn(false);
        when(rateLimiter.getRetryAfterSeconds(anyString())).thenReturn(60L);

        mockMvc.perform(get("/api/quote"))
                .andExpect(status().isTooManyRequests())
                .andExpect(jsonPath("$.error").value("Rate limit exceeded. Try again in 60 seconds."));
    }
}
