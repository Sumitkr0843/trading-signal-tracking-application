package com.sumit.tradingsignaltrackingapplication.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "trading_signals")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradingSignal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String symbol;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Direction direction;

    @Column(name = "entry_price", nullable = false, precision = 20, scale = 8)
    private BigDecimal entryPrice;

    @Column(name = "stop_loss", nullable = false, precision = 20, scale = 8)
    private BigDecimal stopLoss;

    @Column(name = "target_price", nullable = false, precision = 20, scale = 8)
    private BigDecimal targetPrice;

    @Column(name = "entry_time", nullable = false)
    private Instant entryTime;

    @Column(name = "expiry_time", nullable = false)
    private Instant expiryTime;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SignalStatus status;

    @Column(name = "realized_roi", precision = 10, scale = 2)
    private BigDecimal realizedRoi;

    @Version
    @Column(nullable = false)
    private Long version;

    @PrePersist
    public void prePersist() {
        if (status == null) {
            status = SignalStatus.OPEN;
        }
    }
}
