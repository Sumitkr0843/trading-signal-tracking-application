package com.sumit.tradingsignaltrackingapplication.dto;

import com.sumit.tradingsignaltrackingapplication.entity.Direction;
import com.sumit.tradingsignaltrackingapplication.entity.SignalStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignalResponse {

    private Long id;
    private String symbol;
    private Direction direction;
    private BigDecimal entryPrice;
    private BigDecimal stopLoss;
    private BigDecimal targetPrice;
    private Instant entryTime;
    private Instant expiryTime;
    private Instant createdAt;
    private SignalStatus status;
    private BigDecimal realizedRoi;
}
