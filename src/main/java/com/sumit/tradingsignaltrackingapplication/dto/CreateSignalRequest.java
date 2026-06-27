package com.sumit.tradingsignaltrackingapplication.dto;

import com.sumit.tradingsignaltrackingapplication.entity.Direction;
import com.sumit.tradingsignaltrackingapplication.validation.ValidSignalRequest;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;


import java.math.BigDecimal;
import java.time.Instant;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ValidSignalRequest
public class CreateSignalRequest {


    @NotNull(message = "symbol is required")
    @Pattern(regexp = "^[A-Z0-9]{5,20}$", message = "symbol must be uppercase alphanumeric, e.g. BTCUSDT")
    private String symbol;

    @NotNull(message = "direction is required")
    private Direction direction;

    @NotNull(message = "entryPrice is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "entryPrice must be positive")
    private BigDecimal entryPrice;

    @NotNull(message = "stopLoss is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "stopLoss must be positive")
    private BigDecimal stopLoss;

    @NotNull(message = "targetPrice is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "targetPrice must be positive")
    private BigDecimal targetPrice;

    @NotNull(message = "expiryTime is required")
    private Instant expiryTime;
}
