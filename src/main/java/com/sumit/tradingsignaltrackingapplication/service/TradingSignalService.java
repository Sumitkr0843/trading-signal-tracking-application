package com.sumit.tradingsignaltrackingapplication.service;

import com.sumit.tradingsignaltrackingapplication.client.BinanceClient;
import com.sumit.tradingsignaltrackingapplication.dto.CreateSignalRequest;
import com.sumit.tradingsignaltrackingapplication.dto.SignalResponse;

import com.sumit.tradingsignaltrackingapplication.entity.TradingSignal;
import com.sumit.tradingsignaltrackingapplication.exception.SignalNotFoundException;
import com.sumit.tradingsignaltrackingapplication.mapper.SignalMapper;
import com.sumit.tradingsignaltrackingapplication.repository.TradingSignalRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TradingSignalService {

    private final TradingSignalRepository repository;
    private final BinanceClient binanceClient;
    private final SignalEvaluator evaluator;
    private final SignalMapper mapper;

    @Transactional
    public SignalResponse createSignal(CreateSignalRequest request) {
        TradingSignal signal = mapper.toEntity(request);
        signal.setEntryTime(Instant.now());
        TradingSignal saved = repository.save(signal);
        log.info("Created signal id={} symbol={} direction={}", saved.getId(), saved.getSymbol(), saved.getDirection());
        return mapper.toResponse(saved);
    }

    @Transactional
    public List<SignalResponse> getAllSignals() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional
    public SignalResponse getSignalById(Long id) {
        TradingSignal signal = findOrThrow(id);
        return mapper.toResponse(signal);
    }

    @Transactional
    public void deleteSignal(Long id) {
        if (!repository.existsById(id)) {
            throw new SignalNotFoundException(id);
        }
        repository.deleteById(id);
        log.info("Deleted signal id={}", id);
    }

    @Transactional
    public SignalResponse refreshStatus(Long id) {
        TradingSignal signal = findOrThrow(id);

        if (!signal.getStatus().isTerminal()) {
            BigDecimal currentPrice = binanceClient.getCurrentPrice(signal.getSymbol());
            evaluator.evaluate(signal, currentPrice, Instant.now());
            signal = repository.save(signal);
        }

        return mapper.toResponse(signal);
    }

    private TradingSignal findOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new SignalNotFoundException(id));
    }
}
