package com.sumit.tradingsignaltrackingapplication.validation;

import com.sumit.tradingsignaltrackingapplication.dto.CreateSignalRequest;
import com.sumit.tradingsignaltrackingapplication.entity.Direction;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;

public class SignalRequestValidator implements ConstraintValidator<ValidSignalRequest, CreateSignalRequest> {

    private static final Duration MAX_HISTORICAL_ENTRY_WINDOW = Duration.ofHours(24);

    @Override
    public boolean isValid(CreateSignalRequest request, ConstraintValidatorContext context) {
        if (request == null) {
            return true; // let @NotNull elsewhere handle nulls
        }

        // Skip cross-field checks if individual fields are missing/null —
        // field-level @NotNull annotations already report those errors.
        if (request.getDirection() == null
                || request.getEntryPrice() == null
                || request.getStopLoss() == null
                || request.getTargetPrice() == null
                || request.getExpiryTime() == null) {
            return true;
        }

        context.disableDefaultConstraintViolation();
        boolean valid = true;

        valid &= validatePriceOrdering(request, context);
        valid &= validateTimeOrdering(request, context);
        valid &= validateHistoricalEntryWindow(request, context);

        return valid;
    }

    private boolean validatePriceOrdering(CreateSignalRequest request, ConstraintValidatorContext context) {
        BigDecimal entry = request.getEntryPrice();
        BigDecimal stopLoss = request.getStopLoss();
        BigDecimal target = request.getTargetPrice();

        boolean valid;
        if (request.getDirection() == Direction.BUY) {
            valid = stopLoss.compareTo(entry) < 0 && target.compareTo(entry) > 0;
            if (!valid) {
                addViolation(context, "for BUY signals, stopLoss must be < entryPrice and targetPrice must be > entryPrice");
            }
        } else { // SELL
            valid = stopLoss.compareTo(entry) > 0 && target.compareTo(entry) < 0;
            if (!valid) {
                addViolation(context, "for SELL signals, stopLoss must be > entryPrice and targetPrice must be < entryPrice");
            }
        }
        return valid;
    }

    private boolean validateTimeOrdering(CreateSignalRequest request, ConstraintValidatorContext context) {
        if (!request.getExpiryTime().isAfter(Instant.now())) {
            addViolation(context, "expiryTime must be after entryTime");
            return false;
        }
        return true;
    }

    private boolean validateHistoricalEntryWindow(CreateSignalRequest request, ConstraintValidatorContext context) {
        Instant now = Instant.now();
        Instant earliestAllowedEntry = now.minus(MAX_HISTORICAL_ENTRY_WINDOW);

        if (Instant.now().isAfter(now)) {
            addViolation(context, "entryTime cannot be in the future");
            return false;
        }
        if (Instant.now().isBefore(earliestAllowedEntry)) {
            addViolation(context, "entryTime cannot be more than 24 hours in the past");
            return false;
        }
        return true;
    }

    private void addViolation(ConstraintValidatorContext context, String message) {
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }
}
