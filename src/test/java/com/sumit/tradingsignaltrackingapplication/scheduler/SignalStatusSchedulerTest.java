package com.sumit.tradingsignaltrackingapplication.scheduler;

import com.sumit.tradingsignaltrackingapplication.client.BinanceClient;
import com.sumit.tradingsignaltrackingapplication.entity.Direction;
import com.sumit.tradingsignaltrackingapplication.entity.SignalStatus;
import com.sumit.tradingsignaltrackingapplication.entity.TradingSignal;
import com.sumit.tradingsignaltrackingapplication.exception.PriceFetchException;
import com.sumit.tradingsignaltrackingapplication.repository.TradingSignalRepository;
import com.sumit.tradingsignaltrackingapplication.service.SignalEvaluator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SignalSchedulerTest {

    @Mock
    private TradingSignalRepository repository;

    @Mock
    private BinanceClient binanceClient;

    private SignalEvaluator evaluator;

    @InjectMocks
    private SignalScheduler scheduler;

    private TradingSignal signal;

    @BeforeEach
    void setup() {

        evaluator = new SignalEvaluator();

        scheduler = new SignalScheduler(
                repository,
                binanceClient,
                evaluator
        );

        signal = TradingSignal.builder()
                .id(1L)
                .symbol("BTCUSDT")
                .direction(Direction.BUY)
                .entryPrice(BigDecimal.valueOf(100))
                .stopLoss(BigDecimal.valueOf(90))
                .targetPrice(BigDecimal.valueOf(120))
                .entryTime(Instant.now())
                .expiryTime(Instant.now().plusSeconds(3600))
                .status(SignalStatus.OPEN)
                .build();
    }

    @Test
    void shouldSkipWhenNoOpenSignals() {

        when(repository.findByStatus(SignalStatus.OPEN))
                .thenReturn(List.of());

        scheduler.pollOpenSignals();

        verify(repository).findByStatus(SignalStatus.OPEN);

        verifyNoInteractions(binanceClient);
    }

    @Test
    void shouldEvaluateOpenSignals() {

        when(repository.findByStatus(SignalStatus.OPEN))
                .thenReturn(List.of(signal));

        when(binanceClient.getCurrentPrice("BTCUSDT"))
                .thenReturn(BigDecimal.valueOf(121));

        scheduler.pollOpenSignals();

        verify(repository).save(any(TradingSignal.class));
    }

    @Test
    void shouldIgnorePriceFetchException() {

        when(repository.findByStatus(SignalStatus.OPEN))
                .thenReturn(List.of(signal));

        when(binanceClient.getCurrentPrice("BTCUSDT"))
                .thenThrow(new PriceFetchException("Binance unavailable"));

        scheduler.pollOpenSignals();

        verify(repository, never()).save(any());
    }

    @Test
    void evaluateOneShouldSaveSignal() {

        when(binanceClient.getCurrentPrice("BTCUSDT"))
                .thenReturn(BigDecimal.valueOf(121));

        scheduler.evaluateOne(signal, Instant.now());

        verify(repository).save(signal);
    }

}