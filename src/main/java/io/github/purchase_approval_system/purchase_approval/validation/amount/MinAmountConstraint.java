package io.github.purchase_approval_system.purchase_approval.validation.amount;

import jakarta.validation.Constraint;

import java.lang.annotation.*;
import jakarta.validation.Payload;


@Documented
@Constraint(validatedBy = MinAmountValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface MinAmountConstraint {
    String message() default "Requested amount is less than minimum allowed";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
};