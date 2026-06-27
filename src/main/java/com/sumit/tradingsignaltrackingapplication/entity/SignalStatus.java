package com.sumit.tradingsignaltrackingapplication.entity;

public enum SignalStatus {
    OPEN,
    TARGET_HIT,
    STOPLOSS_HIT,
    EXPIRED;

    public boolean isTerminal() {
        return this != OPEN;
    }
}
