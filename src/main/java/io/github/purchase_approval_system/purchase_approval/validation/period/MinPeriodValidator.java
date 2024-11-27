package io.github.purchase_approval_system.purchase_approval.validation.period;

import io.github.purchase_approval_system.purchase_approval.config.AppConfiguration;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class MinPeriodValidator implements ConstraintValidator<MinPeriodConstraint, Integer> {

    private AppConfiguration config;

    @Autowired
    public MinPeriodValidator(AppConfiguration config) {
        this.config = config;
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        return value >= config.getMinPeriod();
    }
}
