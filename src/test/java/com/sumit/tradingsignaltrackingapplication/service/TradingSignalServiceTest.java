package com.sumit.tradingsignaltrackingapplication.service;

import com.sumit.tradingsignaltrackingapplication.client.BinanceClient;
import com.sumit.tradingsignaltrackingapplication.dto.CreateSignalRequest;
import com.sumit.tradingsignaltrackingapplication.dto.SignalResponse;
import com.sumit.tradingsignaltrackingapplication.entity.Direction;
import com.sumit.tradingsignaltrackingapplication.entity.SignalStatus;
import com.sumit.tradingsignaltrackingapplication.entity.TradingSignal;
import com.sumit.tradingsignaltrackingapplication.mapper.SignalMapper;
import com.sumit.tradingsignaltrackingapplication.repository.TradingSignalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TradingSignalServiceTest {

    @Mock
    private TradingSignalRepository repository;

    @Mock
    private BinanceClient binanceClient;

    @Mock
    private SignalEvaluator evaluator;

    private final SignalMapper mapper = new SignalMapper();

    @InjectMocks
    private TradingSignalService service;

    private TradingSignal signal;

    @BeforeEach
    void setup() {

        service = new TradingSignalService(
                repository,
                binanceClient,
                evaluator,
                mapper
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
    void createSignal() {

        CreateSignalRequest request = CreateSignalRequest.builder()
                .symbol("BTCUSDT")
                .direction(Direction.BUY)
                .entryPrice(BigDecimal.valueOf(100))
                .stopLoss(BigDecimal.valueOf(90))
                .targetPrice(BigDecimal.valueOf(120))
                .expiryTime(Instant.now().plusSeconds(3600))
                .build();

        when(repository.save(any())).thenReturn(signal);

        SignalResponse response = service.createSignal(request);

        assertNotNull(response);
        assertEquals("BTCUSDT", response.getSymbol());

        verify(repository).save(any());
    }

    @Test
    void getAllSignals() {

        when(repository.findAll()).thenReturn(List.of(signal));

        List<SignalResponse> result = service.getAllSignals();

        assertEquals(1, result.size());

        verify(repository).findAll();
    }

    @Test
    void getSignalById() {

        when(repository.findById(1L)).thenReturn(Optional.of(signal));

        SignalResponse response = service.getSignalById(1L);

        assertEquals("BTCUSDT", response.getSymbol());

        verify(repository).findById(1L);
    }

    @Test
    void deleteSignal() {

        when(repository.existsById(1L)).thenReturn(true);

        service.deleteSignal(1L);

        verify(repository).deleteById(1L);
    }

    @Test
    void refreshStatus() {

        when(repository.findById(1L)).thenReturn(Optional.of(signal));
        when(binanceClient.getCurrentPrice("BTCUSDT"))
                .thenReturn(BigDecimal.valueOf(121));
        when(repository.save(any())).thenReturn(signal);

        SignalResponse response = service.refreshStatus(1L);

        assertNotNull(response);

        verify(binanceClient).getCurrentPrice("BTCUSDT");
        verify(repository).save(any());
        verify(evaluator).evaluate(
                eq(signal),
                eq(BigDecimal.valueOf(121)),
                any(Instant.class)
        );
    }
}