package com.sumit.tradingsignaltrackingapplication.service;

import com.sumit.tradingsignaltrackingapplication.entity.Direction;
import com.sumit.tradingsignaltrackingapplication.entity.SignalStatus;
import com.sumit.tradingsignaltrackingapplication.entity.TradingSignal;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;

@Component
public class SignalEvaluator {


    private static final int ROI_SCALE = 2;
    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);


    public void evaluate(TradingSignal signal, BigDecimal currentPrice, Instant now) {
        if (signal.getStatus().isTerminal()) {
            return;
        }

        SignalStatus outcome = determineStatus(signal, currentPrice, now);
        if (outcome != SignalStatus.OPEN) {
            signal.setStatus(outcome);
            signal.setRealizedRoi(calculateRoi(signal, currentPrice));
        }
    }

    private SignalStatus determineStatus(TradingSignal signal, BigDecimal currentPrice, Instant now) {
        if (isTargetHit(signal, currentPrice)) {
            return SignalStatus.TARGET_HIT;
        }
        if (isStopLossHit(signal, currentPrice)) {
            return SignalStatus.STOPLOSS_HIT;
        }
        if (now.isAfter(signal.getExpiryTime())) {
            return SignalStatus.EXPIRED;
        }
        return SignalStatus.OPEN;
    }

    private boolean isTargetHit(TradingSignal signal, BigDecimal currentPrice) {
        if (signal.getDirection() == Direction.BUY) {
            return currentPrice.compareTo(signal.getTargetPrice()) >= 0;
        } else {
            return currentPrice.compareTo(signal.getTargetPrice()) <= 0;
        }
    }

    private boolean isStopLossHit(TradingSignal signal, BigDecimal currentPrice) {
        if (signal.getDirection() == Direction.BUY) {
            return currentPrice.compareTo(signal.getStopLoss()) <= 0;
        } else {
            return currentPrice.compareTo(signal.getStopLoss()) >= 0;
        }
    }

    /**
     * BUY:  (current - entry) / entry * 100
     * SELL: (entry - current) / entry * 100
     */
    public BigDecimal calculateRoi(TradingSignal signal, BigDecimal currentPrice) {
        BigDecimal entry = signal.getEntryPrice();
        BigDecimal diff = signal.getDirection() == Direction.BUY
                ? currentPrice.subtract(entry)
                : entry.subtract(currentPrice);

        return diff.divide(entry, 10, RoundingMode.HALF_UP)
                .multiply(HUNDRED)
                .setScale(ROI_SCALE, RoundingMode.HALF_UP);
    }
}
