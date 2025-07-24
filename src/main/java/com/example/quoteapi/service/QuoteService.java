package com.example.quoteapi.service;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Random;

@Service
public class QuoteService {

    private final List<String> quotes = List.of(
            "The only way to do great work is to love what you do. - Steve Jobs",
            "Believe you can and you're halfway there. - Theodore Roosevelt",
            "Success is not final, failure is not fatal: It is the courage to continue that counts. - Winston Churchill",
            "Your time is limited, don't waste it living someone else's life. - Steve Jobs"
    );

    private final Random random = new Random();

    public String getRandomQuote() {
        return quotes.get(random.nextInt(quotes.size()));
    }
}
