package io.github.purchase_approval_system.purchase_approval.validation.amount;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;


@Documented
@Constraint(validatedBy = MaxAmountValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface MaxAmountConstraint {
    String message() default "Wrong requested amount";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
};