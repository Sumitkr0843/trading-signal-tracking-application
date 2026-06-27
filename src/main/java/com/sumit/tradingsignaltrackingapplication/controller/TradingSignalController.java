package com.sumit.tradingsignaltrackingapplication.controller;


import com.sumit.tradingsignaltrackingapplication.dto.CreateSignalRequest;
import com.sumit.tradingsignaltrackingapplication.dto.SignalResponse;
import com.sumit.tradingsignaltrackingapplication.service.TradingSignalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/signals")
@RequiredArgsConstructor
@Tag(name = "Trading Signals", description = "Create and track trading signals against live Binance prices")
public class TradingSignalController {

    private final TradingSignalService service;

    @PostMapping
    @Operation(summary = "Create a new trading signal")
    public ResponseEntity<SignalResponse> createSignal(@Valid @RequestBody CreateSignalRequest request) {
        SignalResponse response = service.createSignal(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "List all trading signals")
    public ResponseEntity<List<SignalResponse>> getAllSignals() {
        return ResponseEntity.ok(service.getAllSignals());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a trading signal by id")
    public ResponseEntity<SignalResponse> getSignalById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getSignalById(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a trading signal")
    public ResponseEntity<Void> deleteSignal(@PathVariable Long id) {
        service.deleteSignal(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/status")
    @Operation(summary = "On-demand status check against the live Binance price")
    public ResponseEntity<SignalResponse> refreshStatus(@PathVariable Long id) {
        return ResponseEntity.ok(service.refreshStatus(id));
    }
}
