package com.sumit.tradingsignaltrackingapplication.service;

import com.sumit.tradingsignaltrackingapplication.entity.Direction;
import com.sumit.tradingsignaltrackingapplication.entity.SignalStatus;
import com.sumit.tradingsignaltrackingapplication.entity.TradingSignal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class SignalEvaluatorTest {

    private SignalEvaluator evaluator;

    private Instant now;

    @BeforeEach
    void setup() {
        evaluator = new SignalEvaluator();
        now = Instant.now();
    }

    private TradingSignal buySignal() {

        return TradingSignal.builder()
                .symbol("BTCUSDT")
                .direction(Direction.BUY)
                .entryPrice(BigDecimal.valueOf(100))
                .stopLoss(BigDecimal.valueOf(90))
                .targetPrice(BigDecimal.valueOf(120))
                .entryTime(now.minusSeconds(3600))
                .expiryTime(now.plusSeconds(3600))
                .status(SignalStatus.OPEN)
                .build();
    }

    private TradingSignal sellSignal() {

        return TradingSignal.builder()
                .symbol("BTCUSDT")
                .direction(Direction.SELL)
                .entryPrice(BigDecimal.valueOf(100))
                .stopLoss(BigDecimal.valueOf(120))
                .targetPrice(BigDecimal.valueOf(80))
                .entryTime(now.minusSeconds(3600))
                .expiryTime(now.plusSeconds(3600))
                .status(SignalStatus.OPEN)
                .build();
    }

    @Test
    void buyTargetHit() {

        TradingSignal signal = buySignal();

        evaluator.evaluate(signal, BigDecimal.valueOf(120), now);

        assertEquals(SignalStatus.TARGET_HIT, signal.getStatus());
        assertEquals(
                BigDecimal.valueOf(20.00).stripTrailingZeros(),
                signal.getRealizedRoi().stripTrailingZeros()
        );
    }

    @Test
    void buyStopLossHit() {

        TradingSignal signal = buySignal();

        evaluator.evaluate(signal, BigDecimal.valueOf(90), now);

        assertEquals(SignalStatus.STOPLOSS_HIT, signal.getStatus());
        assertEquals(
                BigDecimal.valueOf(-10.00).stripTrailingZeros(),
                signal.getRealizedRoi().stripTrailingZeros()
        );
    }

    @Test
    void sellTargetHit() {

        TradingSignal signal = sellSignal();

        evaluator.evaluate(signal, BigDecimal.valueOf(80), now);

        assertEquals(SignalStatus.TARGET_HIT, signal.getStatus());
        assertEquals(
                BigDecimal.valueOf(20.00).stripTrailingZeros(),
                signal.getRealizedRoi().stripTrailingZeros()
        );
    }

    @Test
    void sellStopLossHit() {

        TradingSignal signal = sellSignal();

        evaluator.evaluate(signal, BigDecimal.valueOf(120), now);

        assertEquals(SignalStatus.STOPLOSS_HIT, signal.getStatus());
        assertEquals(
                BigDecimal.valueOf(-20.00).stripTrailingZeros(),
                signal.getRealizedRoi().stripTrailingZeros()
        );
    }

    @Test
    void signalExpired() {

        TradingSignal signal = buySignal();
        signal.setExpiryTime(now.minusSeconds(10));

        evaluator.evaluate(signal, BigDecimal.valueOf(105), now);

        assertEquals(SignalStatus.EXPIRED, signal.getStatus());
    }

    @Test
    void buyStillOpen() {

        TradingSignal signal = buySignal();

        evaluator.evaluate(signal, BigDecimal.valueOf(105), now);

        assertEquals(SignalStatus.OPEN, signal.getStatus());
        assertNull(signal.getRealizedRoi());
    }

    @Test
    void sellStillOpen() {

        TradingSignal signal = sellSignal();

        evaluator.evaluate(signal, BigDecimal.valueOf(95), now);

        assertEquals(SignalStatus.OPEN, signal.getStatus());
        assertNull(signal.getRealizedRoi());
    }

}