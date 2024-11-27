package io.github.purchase_approval_system.purchase_approval.service;

import io.github.purchase_approval_system.purchase_approval.dto.PurchaseApprovalDto;
import io.github.purchase_approval_system.purchase_approval.dto.PurchaseRequestDto;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface PurchaseApprovalService {
    /**
     * Evaluates the purchase approval for a customer.
     *
     * @param requestDto the request data containing personalId, requestedAmount, and paymentPeriod
     * @return PurchaseResponseDto containing approval status, approved amount, and approved period
     */
    CompletableFuture<PurchaseApprovalDto> evaluatePurchase(UUID requestId, PurchaseRequestDto requestDto) throws InterruptedException;
    void evaluatePurchaseBackground(UUID requestId, PurchaseRequestDto requestDto) throws InterruptedException;
    void checkRequest(Double requestedAmount, int period);
}
