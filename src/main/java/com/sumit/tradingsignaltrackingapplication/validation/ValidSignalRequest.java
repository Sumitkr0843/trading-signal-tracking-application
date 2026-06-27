package com.sumit.tradingsignaltrackingapplication.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SignalRequestValidator.class)
@Documented
public @interface ValidSignalRequest {
    String message() default "Invalid signal request";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
