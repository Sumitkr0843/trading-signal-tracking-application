package com.sumit.tradingsignaltrackingapplication.exception;

public class SignalNotFoundException extends RuntimeException {
    public SignalNotFoundException(Long id) {
        super("Trading signal not found with id: " + id);
    }
}
