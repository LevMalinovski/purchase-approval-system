package io.github.purchase_approval_system.purchase_approval.exception;

import lombok.Getter;

@Getter
public class PurchaseApprovalException extends RuntimeException {

    private final String errorCode;

    public PurchaseApprovalException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
