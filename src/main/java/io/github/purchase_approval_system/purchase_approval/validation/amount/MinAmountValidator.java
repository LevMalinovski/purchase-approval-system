package io.github.purchase_approval_system.purchase_approval.validation.amount;

import io.github.purchase_approval_system.purchase_approval.config.AppConfiguration;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class MinAmountValidator implements ConstraintValidator<MinAmountConstraint, Double> {

    private AppConfiguration config;

    @Autowired
    public MinAmountValidator(AppConfiguration config) {
        this.config = config;
    }

    @Override
    public boolean isValid(Double value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        return value >= config.getMinAmount();
    }
}
