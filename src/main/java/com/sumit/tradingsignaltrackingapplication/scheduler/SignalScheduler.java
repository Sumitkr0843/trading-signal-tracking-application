package com.sumit.tradingsignaltrackingapplication.scheduler;


import com.sumit.tradingsignaltrackingapplication.client.BinanceClient;
import com.sumit.tradingsignaltrackingapplication.entity.SignalStatus;
import com.sumit.tradingsignaltrackingapplication.entity.TradingSignal;
import com.sumit.tradingsignaltrackingapplication.exception.PriceFetchException;
import com.sumit.tradingsignaltrackingapplication.repository.TradingSignalRepository;
import com.sumit.tradingsignaltrackingapplication.service.SignalEvaluator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SignalScheduler {
    private final TradingSignalRepository repository;
    private final BinanceClient binanceClient;
    private final SignalEvaluator evaluator;

    @Scheduled(fixedRateString = "${scheduler.signal-poll.fixed-rate-ms:60000}")
    public void pollOpenSignals() {
        List<TradingSignal> openSignals = repository.findByStatus(SignalStatus.OPEN);
        if (openSignals.isEmpty()) {
            return;
        }

        log.info("Scheduler: evaluating {} OPEN signal(s)", openSignals.size());
        Instant now = Instant.now();

        for (TradingSignal signal : openSignals) {
            evaluateOne(signal, now);
        }
    }

    @Transactional
    public void evaluateOne(TradingSignal signal, Instant now) {
        try {
            BigDecimal currentPrice = binanceClient.getCurrentPrice(signal.getSymbol());
            SignalStatus before = signal.getStatus();

            evaluator.evaluate(signal, currentPrice, now);

            if (signal.getStatus() != before) {
                log.info("Signal id={} symbol={} transitioned {} -> {} (price={})",
                        signal.getId(), signal.getSymbol(), before, signal.getStatus(), currentPrice);
            }
            repository.save(signal);

        } catch (PriceFetchException e) {

            log.warn("Skipping signal id={} this cycle: {}", signal.getId(), e.getMessage());
        }
    }
}
