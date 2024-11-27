package io.github.purchase_approval_system.purchase_approval.validation.period;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;


@Documented
@Constraint(validatedBy = MinPeriodValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface MinPeriodConstraint {
    String message() default "Requested amount is less than minimum allowed";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
};