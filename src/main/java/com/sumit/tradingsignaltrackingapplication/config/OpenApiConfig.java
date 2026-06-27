package com.sumit.tradingsignaltrackingapplication.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Trading Signal Tracker API")
                        .description("Simplified trading signal tracking system with Binance live price integration")
                        .version("1.0.0"));
    }
}
