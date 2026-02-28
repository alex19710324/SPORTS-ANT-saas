package com.sportsant.saas;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "SPORTS ANT SaaS API", version = "1.0", description = "Documentation for SPORTS ANT SaaS API v1.0"))
public class SportsAntApplication {

    public static void main(String[] args) {
        SpringApplication.run(SportsAntApplication.class, args);
    }

}
