package io.github.purchase_approval_system.purchase_approval.dto;

public record PurchaseApprovalDto(
    boolean approved,
    double approvedAmount,
    int approvedPeriod
){}
