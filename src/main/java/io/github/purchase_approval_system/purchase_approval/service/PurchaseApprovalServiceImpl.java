package io.github.purchase_approval_system.purchase_approval.service;

import io.github.purchase_approval_system.purchase_approval.config.AppConfiguration;
import io.github.purchase_approval_system.purchase_approval.dto.PurchaseApprovalDto;
import io.github.purchase_approval_system.purchase_approval.dto.PurchaseRequestDto;
import io.github.purchase_approval_system.purchase_approval.exception.PurchaseApprovalException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class PurchaseApprovalServiceImpl implements PurchaseApprovalService {
    public static final String MAX_AMOUNT_EXCEEDED = "MAX_AMOUNT_EXCEEDED";
    public static final String MIN_AMOUNT_VIOLATION = "MIN_AMOUNT_VIOLATION";
    private final AppConfiguration config;
    private final FinancialCapacityFactorService financialCapacityFactorService;
    private final NotificationService notificationService;

    @Autowired
    public PurchaseApprovalServiceImpl(
            AppConfiguration config,
            FinancialCapacityFactorService financialCapacityFactorService,
            NotificationService notificationService
    ) {
        this.config = config;
        this.financialCapacityFactorService = financialCapacityFactorService;
        this.notificationService = notificationService;
    }

    @Async
    @Override
    public void evaluatePurchaseBackground(UUID requestId, PurchaseRequestDto requestDto) throws InterruptedException {
        evaluatePurchase(requestId, requestDto).thenAccept(purchaseApprovalDto -> {
            notificationService.sendMessageWithDelay(requestId, purchaseApprovalDto, 2); // 2 seconds delay because client need time to connect
        });
    }

    @Async
    @Override
    public CompletableFuture<PurchaseApprovalDto> evaluatePurchase(UUID requestId, PurchaseRequestDto requestDto) throws InterruptedException {
        Integer financialCapacity = financialCapacityFactorService.getFactor(requestDto.getPersonalId());
        if (financialCapacity == null) {
            return CompletableFuture.completedFuture(new PurchaseApprovalDto(false, 0, 0));
        }
        Double requestedAmount = requestDto.getRequestedAmount();
        int paymentPeriod = requestDto.getPaymentPeriod();
        checkRequest(requestedAmount, paymentPeriod);

        // Try initial requested amount with payment period
        for (int period = paymentPeriod; period <= config.getMaxPeriod(); period++) {
            if (isApproved(financialCapacity, requestedAmount, period)) {
                return CompletableFuture.completedFuture(new PurchaseApprovalDto(true, requestedAmount, period));
            }
        }

        // Try reducing the requested amount and period if initial values fail
        PurchaseApprovalDto approval;
        for (double amount = requestedAmount - config.getStepReduce(); amount >= config.getMinAmount(); amount -= config.getStepReduce()) {
            approval = findApproval(financialCapacity, amount, paymentPeriod);
            if (approval.approved()) {
                return CompletableFuture.completedFuture(approval);
            }
        }

        return CompletableFuture.completedFuture(new PurchaseApprovalDto(false, 0, 0));
    }

    @Async
    private boolean isApproved(Integer financialCapacity, double amount, int period) {
        return (double) financialCapacity / amount * period >= 1;
    }

    private PurchaseApprovalDto findApproval(int financialCapacity, double amount, int paymentPeriod) {
        for (int period = paymentPeriod; period <= config.getMaxPeriod(); period++) {
            boolean approval = isApproved(financialCapacity, amount, period);
            if (approval) {
                return new PurchaseApprovalDto(true, amount, period);
            }
        }
        return new PurchaseApprovalDto(false, 0, 0);
    }

    public void checkRequest(Double requestedAmount, int period) {
        if (requestedAmount < config.getMinAmount()) {
            throw new PurchaseApprovalException(String.format("Requested amount is below the allowed minimum %.2f", config.getMinAmount()), MIN_AMOUNT_VIOLATION);
        }
        if (requestedAmount > config.getMaxAmount()) {
            throw new PurchaseApprovalException(String.format("Requested amount exceeds the allowed maximum %.2f", config.getMaxAmount()), MAX_AMOUNT_EXCEEDED);
        }
        if (period > config.getMaxPeriod()) {
            throw new PurchaseApprovalException(String.format("Payment period exceeds the allowed maximum (%d months)", config.getMaxPeriod()), "MAX_PERIOD_EXCEEDED");
        }
        if (period < config.getMinPeriod()) {
            throw new PurchaseApprovalException(String.format("Payment period is below the allowed minimum (%d months)", config.getMinPeriod()), "MIN_PERIOD_VIOLATION");
        }
    }
}
