package com.sumit.tradingsignaltrackingapplication.validation;

import com.sumit.tradingsignaltrackingapplication.dto.CreateSignalRequest;
import com.sumit.tradingsignaltrackingapplication.entity.Direction;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SignalRequestValidatorTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void setup() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void tearDown() {
        factory.close();
    }

    private CreateSignalRequest.CreateSignalRequestBuilder validBuyBuilder() {

        Instant now = Instant.now();

        return CreateSignalRequest.builder()
                .symbol("BTCUSDT")
                .direction(Direction.BUY)
                .entryPrice(BigDecimal.valueOf(100))
                .stopLoss(BigDecimal.valueOf(90))
                .targetPrice(BigDecimal.valueOf(120))
                .expiryTime(now.plusSeconds(3600));
    }

    private CreateSignalRequest.CreateSignalRequestBuilder validSellBuilder() {

        Instant now = Instant.now();

        return CreateSignalRequest.builder()
                .symbol("BTCUSDT")
                .direction(Direction.SELL)
                .entryPrice(BigDecimal.valueOf(100))
                .stopLoss(BigDecimal.valueOf(120))
                .targetPrice(BigDecimal.valueOf(80))
                .expiryTime(now.plusSeconds(3600));
    }

    @Test
    void validBuySignal_shouldPassValidation() {

        CreateSignalRequest request = validBuyBuilder().build();

        Set<ConstraintViolation<CreateSignalRequest>> violations =
                validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void invalidBuy_stopLossGreaterThanEntry_shouldFail() {

        CreateSignalRequest request = validBuyBuilder()
                .stopLoss(BigDecimal.valueOf(110))
                .build();

        Set<ConstraintViolation<CreateSignalRequest>> violations =
                validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void invalidBuy_targetBelowEntry_shouldFail() {

        CreateSignalRequest request = validBuyBuilder()
                .targetPrice(BigDecimal.valueOf(80))
                .build();

        Set<ConstraintViolation<CreateSignalRequest>> violations =
                validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void validSellSignal_shouldPassValidation() {

        CreateSignalRequest request = validSellBuilder().build();

        Set<ConstraintViolation<CreateSignalRequest>> violations =
                validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void invalidSell_stopLossBelowEntry_shouldFail() {

        CreateSignalRequest request = validSellBuilder()
                .stopLoss(BigDecimal.valueOf(90))
                .build();

        Set<ConstraintViolation<CreateSignalRequest>> violations =
                validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void invalidSell_targetAboveEntry_shouldFail() {

        CreateSignalRequest request = validSellBuilder()
                .targetPrice(BigDecimal.valueOf(110))
                .build();

        Set<ConstraintViolation<CreateSignalRequest>> violations =
                validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void missingSymbol_shouldFail() {

        CreateSignalRequest request = validBuyBuilder()
                .symbol(null)
                .build();

        Set<ConstraintViolation<CreateSignalRequest>> violations =
                validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void missingDirection_shouldFail() {

        CreateSignalRequest request = validBuyBuilder()
                .direction(null)
                .build();

        Set<ConstraintViolation<CreateSignalRequest>> violations =
                validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void missingExpiry_shouldFail() {

        CreateSignalRequest request = validBuyBuilder()
                .expiryTime(null)
                .build();

        Set<ConstraintViolation<CreateSignalRequest>> violations =
                validator.validate(request);

        assertFalse(violations.isEmpty());
    }

}