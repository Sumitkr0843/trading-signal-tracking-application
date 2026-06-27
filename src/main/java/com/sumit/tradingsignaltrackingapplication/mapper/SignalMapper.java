package com.sumit.tradingsignaltrackingapplication.mapper;

import com.sumit.tradingsignaltrackingapplication.dto.CreateSignalRequest;
import com.sumit.tradingsignaltrackingapplication.dto.SignalResponse;
import com.sumit.tradingsignaltrackingapplication.entity.SignalStatus;
import com.sumit.tradingsignaltrackingapplication.entity.TradingSignal;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class SignalMapper {

    public TradingSignal toEntity(CreateSignalRequest request) {
        return TradingSignal.builder()
                .symbol(request.getSymbol())
                .direction(request.getDirection())
                .entryPrice(request.getEntryPrice())
                .stopLoss(request.getStopLoss())
                .targetPrice(request.getTargetPrice())
                .entryTime(Instant.now())
                .expiryTime(request.getExpiryTime())
                .status(SignalStatus.OPEN)
                .build();
    }

    public SignalResponse toResponse(TradingSignal signal) {
        return SignalResponse.builder()
                .id(signal.getId())
                .symbol(signal.getSymbol())
                .direction(signal.getDirection())
                .entryPrice(signal.getEntryPrice())
                .stopLoss(signal.getStopLoss())
                .targetPrice(signal.getTargetPrice())
                .entryTime(signal.getEntryTime())
                .expiryTime(signal.getExpiryTime())
                .createdAt(signal.getCreatedAt())
                .status(signal.getStatus())
                .realizedRoi(signal.getRealizedRoi())
                .build();
    }
}
