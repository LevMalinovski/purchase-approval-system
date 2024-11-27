package io.github.purchase_approval_system.purchase_approval.validation.period;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;


@Documented
@Constraint(validatedBy = MaxPeriodValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface MaxPeriodConstraint {
    String message() default "Wrong requested period";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
};