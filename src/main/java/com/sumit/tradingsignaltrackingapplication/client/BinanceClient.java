package com.sumit.tradingsignaltrackingapplication.client;

import com.sumit.tradingsignaltrackingapplication.dto.BinancePriceResponse;
import com.sumit.tradingsignaltrackingapplication.exception.PriceFetchException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.math.BigDecimal;
import java.time.Duration;

@Slf4j
@Component
public class BinanceClient {
    private final WebClient webClient;

    public BinanceClient(@Value("${binance.api.base-url}") String baseUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public BigDecimal getCurrentPrice(String symbol) {
        try {
            BinancePriceResponse response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/v3/ticker/price")
                            .queryParam("symbol", symbol)
                            .build())
                    .retrieve()
                    .bodyToMono(BinancePriceResponse.class)
                    .timeout(Duration.ofSeconds(5))
                    .block();

            if (response == null || response.getPrice() == null) {
                throw new PriceFetchException("Binance returned an empty price for symbol " + symbol);
            }
            return response.getPrice();

        } catch (WebClientResponseException e) {
            log.warn("Binance API error for symbol {}: {} {}", symbol, e.getStatusCode(), e.getResponseBodyAsString());
            throw new PriceFetchException(
                    "Failed to fetch price for symbol " + symbol + " (Binance returned " + e.getStatusCode() + ")", e);
        } catch (Exception e) {
            log.warn("Unexpected error fetching price for symbol {}: {}", symbol, e.getMessage());
            throw new PriceFetchException("Failed to fetch price for symbol " + symbol, e);
        }
    }
}
