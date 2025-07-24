package com.example.quoteapi;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@OpenAPIDefinition(
        info = @Info(title = "Quote API", version = "1.0", description = "Rate-limited inspirational quotes API")
)
@SpringBootApplication
public class QuoteApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuoteApiApplication.class, args);
    }

}
