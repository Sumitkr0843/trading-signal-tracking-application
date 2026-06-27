-- V1__create_trading_signals_table.sql

CREATE TABLE trading_signals (
    id              BIGSERIAL PRIMARY KEY,
    symbol          VARCHAR(20)     NOT NULL,
    direction       VARCHAR(10)     NOT NULL,
    entry_price     NUMERIC(20, 8)  NOT NULL,
    stop_loss       NUMERIC(20, 8)  NOT NULL,
    target_price    NUMERIC(20, 8)  NOT NULL,
    entry_time      TIMESTAMPTZ     NOT NULL,
    expiry_time     TIMESTAMPTZ     NOT NULL,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT now(),
    status          VARCHAR(20)     NOT NULL DEFAULT 'OPEN',
    realized_roi    NUMERIC(10, 2),
    version         BIGINT          NOT NULL DEFAULT 0,

    CONSTRAINT chk_direction CHECK (direction IN ('BUY', 'SELL')),
    CONSTRAINT chk_status CHECK (status IN ('OPEN', 'TARGET_HIT', 'STOPLOSS_HIT', 'EXPIRED')),
    CONSTRAINT chk_prices_positive CHECK (entry_price > 0 AND stop_loss > 0 AND target_price > 0),
    CONSTRAINT chk_expiry_after_entry CHECK (expiry_time > entry_time)
);

-- The scheduler's main query path: fetch all OPEN signals every poll cycle.
CREATE INDEX idx_trading_signals_status ON trading_signals (status);

-- Supports lookups/filtering by trading pair (e.g. future "all BTCUSDT signals" use cases).
CREATE INDEX idx_trading_signals_symbol ON trading_signals (symbol);
