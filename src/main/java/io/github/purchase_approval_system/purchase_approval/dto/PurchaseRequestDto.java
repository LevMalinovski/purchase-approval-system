package io.github.purchase_approval_system.purchase_approval.dto;

import io.github.purchase_approval_system.purchase_approval.validation.amount.MaxAmountConstraint;
import io.github.purchase_approval_system.purchase_approval.validation.amount.MinAmountConstraint;
import io.github.purchase_approval_system.purchase_approval.validation.period.MaxPeriodConstraint;
import io.github.purchase_approval_system.purchase_approval.validation.period.MinPeriodConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseRequestDto {
    @NotBlank(message = "Personal ID cannot be blank")
    private String personalId;

    @MinAmountConstraint
    @MaxAmountConstraint
    @NotNull(message = "Requested amount is required")
    private Double requestedAmount;

    @MinPeriodConstraint
    @MaxPeriodConstraint
    @NotNull(message = "Payment period is required")
    private Integer paymentPeriod;
}
