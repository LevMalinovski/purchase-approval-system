package io.github.purchase_approval_system.purchase_approval.validation.amount;

import io.github.purchase_approval_system.purchase_approval.config.AppConfiguration;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class MaxAmountValidator implements ConstraintValidator<MaxAmountConstraint, Double> {

    private AppConfiguration config;

    @Autowired
    public MaxAmountValidator(AppConfiguration config) {
        this.config = config;
    }

    @Override
    public boolean isValid(Double value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        return value <= config.getMaxAmount();
    }
}
