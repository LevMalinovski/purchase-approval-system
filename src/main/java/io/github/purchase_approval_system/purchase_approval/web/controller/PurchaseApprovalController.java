package io.github.purchase_approval_system.purchase_approval.web.controller;

import io.github.purchase_approval_system.purchase_approval.dto.AcceptedRequestResponseDto;
import io.github.purchase_approval_system.purchase_approval.dto.PurchaseRequestDto;
import io.github.purchase_approval_system.purchase_approval.service.PurchaseApprovalService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api")
public class PurchaseApprovalController {

    private final PurchaseApprovalService approvalService;

    @Autowired
    public PurchaseApprovalController(PurchaseApprovalService approvalService) {
        this.approvalService = approvalService;
    }

    @PostMapping
    public ResponseEntity<AcceptedRequestResponseDto> evaluatePurchase(@RequestBody @Valid PurchaseRequestDto requestDto) throws InterruptedException {
        UUID requestId = UUID.randomUUID();
        approvalService.evaluatePurchaseBackground(requestId, requestDto);
        return ResponseEntity.accepted().body(new AcceptedRequestResponseDto(requestId));

    }
}
